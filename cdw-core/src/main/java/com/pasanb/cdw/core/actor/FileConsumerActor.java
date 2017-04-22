package com.pasanb.cdw.core.actor;

import akka.actor.ActorRef;
import akka.camel.CamelMessage;
import akka.camel.javaapi.UntypedConsumerActor;
import com.pasanb.cdw.core.csv.RawCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pasan on 6/25/2016.
 */

/**
 * Akka untyped actor responsible for reading files in specified path. It will pass the read file to File Processing actor
 * to process the CSV file.
 */
public class FileConsumerActor extends UntypedConsumerActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileConsumerActor.class);

    private String path;
    private ActorRef fileProcessor;

    public FileConsumerActor(String path, ActorRef fileProcessor) {
        this.path = path;
        this.fileProcessor = fileProcessor;
    }

    @Override
    public String getEndpointUri() {
        return "file:" + path;
    }

    /**
     * @param message
     * @throws Exception
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof CamelMessage) {
            CamelMessage camelMessage = (CamelMessage) message;
            String fileName = (String) camelMessage.getHeaders().get("CamelFileName");
            String body = camelMessage.getBodyAs(String.class, getCamelContext());
            LOGGER.info("received file {}. # of records={}", fileName, body.split("\n").length);
            if (body != null && !body.isEmpty()) {
                RawCSV rawCSV = new RawCSV(fileName, body);
                fileProcessor.tell(rawCSV, getSender());
            } else {
                LOGGER.error("invalid file. {}", fileName);
            }
        } else
            unhandled(message);
    }
}

