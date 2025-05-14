package com.example.csvservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class CsvService {

    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);

    public <T> CsvParseResult<T> parseCsv(
            BufferedReader br,
            String[] expectedHeaders,
            Function<String[], T> mapper,
            Predicate<String[]> validator,
            Function<T, String> uniqueKeyExtractor) {

        CsvParseResult<T> result = new CsvParseResult<>();
        Set<String> uniqueKeys = new HashSet<>();

        try {
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new CsvProcessingException("El archivo CSV está vacío");
            }

            String[] actualHeaders = headerLine.split(",");
            if (!Arrays.equals(Arrays.stream(expectedHeaders).map(String::trim).toArray(),
                               Arrays.stream(actualHeaders).map(String::trim).toArray())) {
                throw new CsvProcessingException("Encabezado CSV inválido: " + headerLine);
            }

            String line;
            while ((line = br.readLine()) != null) {
                result.incrementTotalLines();
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] fields = line.split(",");
                if (!validator.test(fields)) {
                    result.addInvalidLine(line);
                    continue;
                }

                try {
                    T record = mapper.apply(fields);
                    String key = uniqueKeyExtractor.apply(record);
                    if (!uniqueKeys.add(key) && record instanceof Persona) {
                        ((Persona) record).setDuplicated(true);
                    }
                    result.addValidRecord(record);
                } catch (Exception e) {
                    result.addInvalidLine(line + " [Error: " + e.getMessage() + "]");
                }
            }

        } catch (IOException e) {
            throw new CsvProcessingException("Error al leer archivo CSV", e);
        }

        return result;
    }
}