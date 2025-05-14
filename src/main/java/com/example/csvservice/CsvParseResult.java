package com.example.csvservice;

import java.util.ArrayList;
import java.util.List;

public class CsvParseResult<T> {
    private final List<T> validRecords = new ArrayList<>();
    private final List<String> invalidLines = new ArrayList<>();
    private int totalLines = 0;

    public void addValidRecord(T record) {
        validRecords.add(record);
    }

    public void addInvalidLine(String line) {
        invalidLines.add(line);
    }

    public void incrementTotalLines() {
        totalLines++;
    }

    public List<T> getValidRecords() {
        return validRecords;
    }

    public List<String> getInvalidLines() {
        return invalidLines;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public int getValidCount() {
        return validRecords.size();
    }

    public int getInvalidCount() {
        return invalidLines.size();
    }
}