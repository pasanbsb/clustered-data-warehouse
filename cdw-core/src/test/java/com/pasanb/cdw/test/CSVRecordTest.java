package com.pasanb.cdw.test;

import com.pasanb.cdw.core.csv.CSVRecord;
import com.pasanb.cdw.core.util.ConfigReader;
import com.pasanb.cdw.core.util.Validation;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by Pasan on 6/26/2016.
 */
public class CSVRecordTest extends TestCase {

    public void testProcessCSVRecord() throws Exception {

        Validation validation = Validation.getInstance();
        validation.addValidation(CSVRecord.DEAL_UNIQUE_ID, ".*");
        validation.addValidation(CSVRecord.FROM_CURRENCY, ".*");
        validation.addValidation(CSVRecord.TO_CURRENCY, ".*");
        validation.addValidation(CSVRecord.TIME_STAMP, ".*");
        validation.addValidation(CSVRecord.DEAL_AMOUNT, "\\d+(\\.\\d+)?");

        CSVRecord record = new CSVRecord("0d92d44f-1d45-4943-a18e-dc1633f0d103,USD,USD,2016/06/27 02:14,31.603193654218074");
        Assert.assertEquals("0d92d44f-1d45-4943-a18e-dc1633f0d103", record.getDealUniqueId());
        Assert.assertEquals("USD", record.getFromCurrency());
        Assert.assertEquals("USD", record.getToCurrency());
        Assert.assertEquals("2016/06/27 02:14", record.getTimeStamp());
        Assert.assertEquals(31.603193654218074, record.getDealAmount());
        Assert.assertEquals(true, record.isValid());

        CSVRecord record2 = new CSVRecord("AAA,LKR,USD,Timestamp,");
        Assert.assertEquals(false, record2.isValid());
    }
}