package com.pasanb.cdw.core.csv;

import com.pasanb.cdw.core.util.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pasanb.cdw.core.util.CDWConstants.*;

/**
 * Created by Pasan on 6/25/2016.
 */
public class CSVRecord {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVRecord.class);

    public static final String DEAL_UNIQUE_ID = "dealUniqueId";
    public static final String FROM_CURRENCY = "fromCurrency";
    public static final String TO_CURRENCY = "toCurrency";
    public static final String TIME_STAMP = "timeStamp";
    public static final String DEAL_AMOUNT = "dealAmount";

    private String dealUniqueId;
    private String fromCurrency;
    private String toCurrency;
    private String timeStamp;
    private Double dealAmount;
    private boolean isValid = true;

    public CSVRecord(String recordLine) {
        processCSVRecord(recordLine);
    }

    /**
     * @param recordLine
     */
    private void processCSVRecord(String recordLine) {

        if (recordLine != null) {
            String[] data = recordLine.split(CSV_SPLITTER);

            for (int i = 0; i < data.length; i++) {
                setValues(i, data[i].trim());
            }

            if (data.length != DATA_COLUMNS) {
                isValid = false;
                LOGGER.error("validation failed. invalid column length. {}, record={}" , data.length, recordLine);
            }
        }

    }

    /**
     * @param position
     * @param data
     */
    private void setValues(int position, String data) {

        Validation validation = Validation.getInstance();
        switch (position) {
            case DataPosition.DEAL_UNIQUE_ID:
                validateData(validation.getValidation(DEAL_UNIQUE_ID), data);
                dealUniqueId = data;
                break;
            case DataPosition.FROM_CURRENCY:
                validateData(validation.getValidation(FROM_CURRENCY), data);
                fromCurrency = data;
                break;
            case DataPosition.TO_CURRENCY:
                validateData(validation.getValidation(TO_CURRENCY), data);
                toCurrency = data;
                break;
            case DataPosition.TIME_STAMP:
                validateData(validation.getValidation(TIME_STAMP), data);
                timeStamp = data;
                break;
            case DataPosition.DEAL_AMOUNT:
                validateData(validation.getValidation(DEAL_AMOUNT), data);
                try {
                    dealAmount = Double.valueOf(data);
                } catch (NumberFormatException e) {
                    dealAmount = -1.0;
                    isValid = false;
                    LOGGER.error("validation failed for data={}, NumberFormatException", data);
                }
        }
    }

    /**
     * Validate data against the given regular expression
     * @param pattern
     * @param data
     */
    private void validateData(Pattern pattern, String data) {
        Matcher matcher;

        try {
            matcher = pattern.matcher(data);
            if (!matcher.matches()) {
                LOGGER.error("regex validation failed for data={}, pattern={}", data, pattern.pattern());
                isValid = false;
            }
        } catch (Exception e) {
            LOGGER.error("regex validation failed for data={}, pattern={}", data, pattern.pattern(), e);
        }
    }

    public String getDealUniqueId() {
        return dealUniqueId;
    }

    public void setDealUniqueId(String dealUniqueId) {
        this.dealUniqueId = dealUniqueId;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Double getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(Double dealAmount) {
        this.dealAmount = dealAmount;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Override
    public String toString() {
        return "CSVRecord{" +
                "dealUniqueId='" + dealUniqueId + '\'' +
                ", fromCurrency='" + fromCurrency + '\'' +
                ", toCurrency='" + toCurrency + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", dealAmount=" + dealAmount +
                ", isValid=" + isValid +
                '}';
    }
}
