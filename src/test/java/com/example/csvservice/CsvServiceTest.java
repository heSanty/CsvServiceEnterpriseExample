package com.example.csvservice;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class CsvServiceTest {

    @Test
    void testCsvParsing() {
        String csvContent = "id,nombre,apellido,curp\n" +
                            "1,Juan,Pérez,CURP123\n" +
                            "2,Ana,López,CURP456\n" +
                            "3,Carlos,García,CURP123\n" +
                            "x,Invalido,123,CURP000\n";

        CsvService service = new CsvService();

        BufferedReader reader = new BufferedReader(new StringReader(csvContent));

        CsvParseResult<Persona> result = service.parseCsv(
                reader,
                new String[]{"id", "nombre", "apellido", "curp"},
                campos -> new Persona(
                        Integer.parseInt(campos[0].trim()),
                        campos[1].trim(),
                        campos[2].trim(),
                        campos[3].trim()
                ),
                campos -> campos.length == 4 &&
                          !campos[1].trim().isEmpty() &&
                          !campos[2].trim().isEmpty() &&
                          !campos[3].trim().isEmpty() &&
                          isInteger(campos[0].trim()),
                Persona::getCurp
        );

        assertEquals(3, result.getValidCount());
        assertEquals(1, result.getInvalidCount());

        Persona duplicated = result.getValidRecords().stream()
                                   .filter(p -> p.getCurp().equals("CURP123") && p.toString().contains("true"))
                                   .findFirst()
                                   .orElse(null);

        assertNotNull(duplicated);
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}