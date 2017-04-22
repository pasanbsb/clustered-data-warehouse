package com.pasanb.cdw.persistence.actor;

import akka.actor.UntypedActor;
import com.pasanb.cdw.core.csv.CSVFile;
import com.pasanb.cdw.core.csv.CSVRecord;
import com.pasanb.cdw.core.util.DBConnection;
import com.pasanb.cdw.persistence.domain.Deal;
import com.pasanb.cdw.persistence.domain.DealCountPerCurrency;
import com.pasanb.cdw.persistence.domain.InvalidDeal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Pasan on 6/26/2016.
 */

/**
 * Responsible for persisting CSV files to database.
 */
public class DBWriterActor extends UntypedActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBWriterActor.class);

    private static final String PERSISTENCE_UNIT_NAME = "cdw_deals";
    private static EntityManagerFactory factory;

    public DBWriterActor(DBConnection dbConnection) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("hibernate.connection.url", dbConnection.getUrl());
        properties.put("hibernate.connection.username", dbConnection.getUserName());
        properties.put("hibernate.connection.password", dbConnection.getPassword());
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof CSVFile) {
            updateDB((CSVFile) message);
        } else {
            unhandled(message);
        }
    }

    private void updateDB(CSVFile csvFile) {

        LOGGER.info("updating db file={}", csvFile.getFileName());
        long start = System.currentTimeMillis();
        upDateValidDeals(csvFile);
        updateInvalidDeals(csvFile);
        updateCountPerCurrencyTable(csvFile);
        long elapsed = (System.currentTimeMillis() - start);

        LOGGER.info("time taken to update the db={}, file={}", elapsed, csvFile.getFileName());
    }

    private void upDateValidDeals(CSVFile csvFile) {
        try {
            EntityManager em = factory.createEntityManager();
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            csvFile.getValidRecords().forEach(csvRecord -> {
                Deal deal = new Deal();
                deal.setDealUniqueId(csvRecord.getDealUniqueId());
                deal.setFromCurrency(csvRecord.getFromCurrency());
                deal.setToCurrency(csvRecord.getToCurrency());
                deal.setTimeStamp(csvRecord.getTimeStamp());
                deal.setDealAmount(csvRecord.getDealAmount());
                deal.setFileName(csvFile.getFileName());
                em.persist(deal);

            });

            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            } else {
                em.flush();
            }

            em.close();

        } catch (Exception e) {
            LOGGER.error("error occurred while persisting valid deals. file={}", csvFile.getFileName(), e);
        }
    }

    private void updateInvalidDeals(CSVFile csvFile) {
        try {
            EntityManager em = factory.createEntityManager();
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            csvFile.getInvalidRecords().forEach(csvRecord -> {
                InvalidDeal deal = new InvalidDeal();
                deal.setDealUniqueId(csvRecord.getDealUniqueId());
                deal.setFromCurrency(csvRecord.getFromCurrency());
                deal.setToCurrency(csvRecord.getToCurrency());
                deal.setTimeStamp(csvRecord.getTimeStamp());
                deal.setDealAmount(csvRecord.getDealAmount());
                deal.setFileName(csvFile.getFileName());
                em.persist(deal);
            });

            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            } else {
                em.flush();
            }

            em.close();

        } catch (Exception e) {
            LOGGER.error("error occurred while persisting invalid deals. file={}", csvFile.getFileName(), e);
        }
    }

    private void updateCountPerCurrencyTable(CSVFile csvFile) {
        try {
            EntityManager em = factory.createEntityManager();
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            Map<String, List<CSVRecord>> fromCurrencyList = csvFile.getValidRecords()
                    .stream()
                    .collect(Collectors.groupingBy(CSVRecord::getFromCurrency));

            fromCurrencyList.keySet().forEach(s ->
            {
                Query q = em.createQuery("select dc from DealCountPerCurrency dc where dc.currency=:currency");
                q.setParameter("currency", s);
                DealCountPerCurrency countPerCurrency = null;
                try {
                    countPerCurrency = (DealCountPerCurrency) q.getSingleResult();
                } catch (Exception e) {

                }

                if (countPerCurrency == null) {
                    DealCountPerCurrency countPerCurrency1 = new DealCountPerCurrency();
                    countPerCurrency1.setCurrency(s);
                    countPerCurrency1.setCount(fromCurrencyList.get(s).size());
                    em.persist(countPerCurrency1);
                } else {
                    countPerCurrency.setCount(countPerCurrency.getCount() + fromCurrencyList.get(s).size());
                    em.persist(countPerCurrency);
                }
            });

            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            } else {
                em.flush();
            }
            em.close();
        } catch (Exception e) {
            LOGGER.error("error occurred while persisting deals per currency. file={}", csvFile.getFileName(), e);
        }
    }
}
