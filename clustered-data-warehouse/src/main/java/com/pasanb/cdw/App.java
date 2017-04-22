package com.pasanb.cdw;

import com.pasanb.cdw.manager.ActorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("starting clustered-data-warehouse...");

        ActorManager actorManager  = ActorManager.getInstance();
        try {
            actorManager.start();
        } catch (Exception e) {
           LOGGER.error("fatal error occurred while starting clustered data warehouse. exiting...", e);
            System.exit(-1);
        }

    }
}
