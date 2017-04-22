package com.pasanb.cdw.core.util;

import org.ini4j.Ini;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by Pasan on 6/26/2016.
 */
public class ConfigReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigReader.class);

    Wini ini;
    private static final String CONF_FILE = "conf" + File.separator + "config.ini";
    private static final ConfigReader INSTANCE = new ConfigReader();

    private String sharedFolderLocation = "data";
    private DBConnection dbConnection;

    private ConfigReader(){
        LOGGER.debug("loading configurations...");
        LOGGER.debug("configuration file=" + CONF_FILE);
        try {
            ini = new Wini(new File(CONF_FILE));
            loadMiscConfigs();
            loadDataSource();
            loadValidations();
        } catch (Exception e) {
            LOGGER.error("fatal error occurred while loading configurations.", e);
            System.exit(-1);
        }
    }

    public static ConfigReader getInstance() {
        return INSTANCE;
    }

    private void loadMiscConfigs() {
        Ini.Section misc = ini.get("MISC");
        sharedFolderLocation = misc.get("shared_folder");
    }

    private void loadValidations() throws Exception {
            Ini.Section validations = ini.get("VALIDATIONS");

            Validation validation = Validation.getInstance();
            for (String key : validations.keySet()) {
                String v = validations.get(key);
                LOGGER.debug("loaded validation {} for {}", v, key);
                validation.addValidation(key, v);
            }
    }

    private void loadDataSource() throws Exception{
        Ini.Section mysql_db = ini.get("MYSQL_DB");

        dbConnection = new DBConnection();
        dbConnection.setUrl(mysql_db.get("url"));
        dbConnection.setUserName(mysql_db.get("username"));
        dbConnection.setPassword(mysql_db.get("password"));

    }

    public DBConnection getDbConnection() {
        return dbConnection;
    }

    public void setDbConnection(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public String getSharedFolderLocation() {
        return sharedFolderLocation;
    }
}
