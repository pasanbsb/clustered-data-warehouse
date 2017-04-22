package com.pasanb.cdw.core.csv;

/**
 * Created by Pasan on 6/26/2016.
 */
public class RawCSV {
    private String fileName;
    private String body;

    public RawCSV(String fileName, String body) {
        this.fileName = fileName;
        this.body = body;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
