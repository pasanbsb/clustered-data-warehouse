package com.pasanb.cdw.core.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pasan on 6/25/2016.
 */
public class CSVFile {
    private String fileName;
    private List<CSVRecord> validRecords;
    private List<CSVRecord> invalidRecords;

    public CSVFile(String fileName) {
        this.fileName = fileName;
        validRecords = new ArrayList<>();
        invalidRecords = new ArrayList<>();
    }

    public void addValidRecord(CSVRecord csvRecord) {
        validRecords.add(csvRecord);
    }

    public void addInvalidRecord(CSVRecord csvRecord) {
        invalidRecords.add(csvRecord);
    }


    public List<CSVRecord> getValidRecords() {
        return validRecords;
    }

    public List<CSVRecord> getInvalidRecords() {
        return invalidRecords;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
