package com.pasanb.cdw.manager.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.camel.Camel;
import akka.camel.CamelExtension;
import com.pasanb.cdw.core.actor.FileConsumerActor;
import com.pasanb.cdw.core.actor.FileProcessingActor;
import com.pasanb.cdw.core.util.ConfigReader;
import com.pasanb.cdw.core.util.DBConnection;
import com.pasanb.cdw.manager.ActorManager;
import com.pasanb.cdw.persistence.actor.DBWriterActor;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pasan on 6/25/2016.
 */

/**
 * Starts the Akka actor system.
 */
public class ActorManagerImpl implements ActorManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorManagerImpl.class);
    public static final String ACTOR_SYSTEM = "CDWActorSystem";

    private static final ActorManager INSTANCE = new ActorManagerImpl();

    private ActorManagerImpl() {
    }

    public static ActorManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void start() throws Exception{
        ActorRef fileConsumerActorRef;
        ActorRef fileProcessorActorRef;
        ActorRef dbWriterActorRef;

        ConfigReader configReader = ConfigReader.getInstance();
        try {
            LOGGER.debug("creating actor system... {}", ACTOR_SYSTEM);
            ActorSystem system = ActorSystem.create(ACTOR_SYSTEM);
            Camel camel = CamelExtension.get(system);
            CamelContext camelContext = camel.context();
            camelContext.setStreamCaching(false);

            DBConnection dbConnection = configReader.getDbConnection();
            LOGGER.debug("creating db writer");
            dbWriterActorRef = system.actorOf(Props.create(DBWriterActor.class, dbConnection), "DBWriterActor");
            LOGGER.debug("db writer created for {}", dbConnection.getUrl());

            LOGGER.debug("creating file processor");
            fileProcessorActorRef = system.actorOf(Props.create(FileProcessingActor.class, dbWriterActorRef), "FileProcessorActor");
            LOGGER.debug("file processor created");

            LOGGER.debug("creating file consumer");
            String path = configReader.getSharedFolderLocation();
            fileConsumerActorRef = system.actorOf(Props.create(FileConsumerActor.class, path, fileProcessorActorRef), "FileConsumerActor");
            LOGGER.debug("file consumer created and started consuming files from {}", path);

        } catch (Exception e) {
            throw e;
        }


    }
}
