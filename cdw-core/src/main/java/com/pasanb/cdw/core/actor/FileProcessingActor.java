package com.pasanb.cdw.core.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.pasanb.cdw.core.csv.CSVFile;
import com.pasanb.cdw.core.csv.CSVRecord;
import com.pasanb.cdw.core.csv.RawCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pasan on 6/25/2016.
 */

/**
 * Responsible for processing the given CSV file. It will pass the processed CSV file to DB Writer actor
 * for persisting
 */
public class FileProcessingActor extends UntypedActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingActor.class);
    private ActorRef dbWriter;

    public FileProcessingActor(ActorRef dbWriter) {
        this.dbWriter = dbWriter;
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if(message instanceof RawCSV) {
            CSVFile csvFile = processFile(message);
            dbWriter.tell(csvFile, getSender());
        } else {
            unhandled(message);
        }
    }

    /**
     * @param RawCSV file obtained from the FileConsumerActor
     * @return CSV File containing records of valid and invalid CSV records
     */
    private CSVFile processFile(Object message) {

        long start = System.currentTimeMillis();
        RawCSV rawCSV = (RawCSV) message;
        CSVFile csvFile = new CSVFile(rawCSV.getFileName());
        String[] lines = rawCSV.getBody().split("\n");
        for (String line : lines) {
            CSVRecord record = new CSVRecord(line);
            if(record.isValid()) {
                csvFile.addValidRecord(record);
            } else {
                csvFile.addInvalidRecord(record);
            }
        }
        long elapsed = (System.currentTimeMillis() - start);
        LOGGER.info("time taken to process the file={}, # of valid records={}, # of invalid records={}",
                elapsed,
                csvFile.getValidRecords().size(),
                csvFile.getInvalidRecords().size());

        return csvFile;
    }
}
