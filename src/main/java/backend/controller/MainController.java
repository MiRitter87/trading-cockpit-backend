package backend.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.alert.PriceAlertController;
import backend.dao.DAOManager;
import okhttp3.OkHttpClient;

/**
 * The main controller of the web application.
 *
 * @author Michael
 */
public final class MainController {
    /**
     * File path to configuration properties.
     */
    protected static final String SUBPATH_CONFIGURATION_PROPERTIES = "/conf/tradingCockpitBackend.properties";

    /**
     * Instance of the main controller.
     */
    private static MainController instance;

    /**
     * Queries Instrument quotes and triggers price alerts.
     */
    private PriceAlertController priceAlertController;

    /**
     * Client that is used for HTTP queries of third-party WebServices.
     *
     * The OkHttpClient instance should be shared across the whole application according to the documentation.
     *
     * @see https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/
     */
    private OkHttpClient okHttpClient;

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(MainController.class);

    /**
     * Creates and initializes the main controller.
     */
    private MainController() {

    }

    /**
     * Provides the instance of the MainController.
     *
     * @return The instance of the MainController.
     */
    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }

        return instance;
    }

    /**
     * Performs tasks on application startup.
     */
    public void applicationStartup() {
        DAOManager.getInstance();

        try {
            this.checkConfigFileExisting();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.warn("Application could not be started properly.");
            return;
        }

        try {
            this.okHttpClient = new OkHttpClient();
            this.priceAlertController = new PriceAlertController();
            this.priceAlertController.start();

            LOGGER.info("Application started.");
        } catch (Exception e) {
            LOGGER.error("The query mechanism for price alerts failed to start.", e);
            LOGGER.warn("Application could not be started properly.");
        }
    }

    /**
     * Performs tasks on application shutdown.
     */
    public void applicationShutdown() {
        try {
            DAOManager.getInstance().close();

            if (this.priceAlertController != null) {
                this.priceAlertController.stop();
            }

            LOGGER.info("Application stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the value of the property for the given key.
     *
     * @param propertyKey The key of the property.
     * @return The value of the property.
     * @throws Exception In case the property could not be read.
     */
    public String getConfigurationProperty(final String propertyKey) throws Exception {
        Properties properties = new Properties();
        String value = "";
        FileInputStream input = null;
        String workingDir = System.getProperty("user.dir");

        try {
            input = new FileInputStream(workingDir + SUBPATH_CONFIGURATION_PROPERTIES);
            properties.load(input);
            value = properties.getProperty(propertyKey);
        } catch (FileNotFoundException e) {
            new Exception(e.getMessage());
        } catch (IOException e) {
            new Exception(e.getMessage());
        }

        return value;
    }

    /**
     * Returns a DataProvider based on the given String.
     *
     * @param propertyKey The key of the property.
     * @return The DataProvider that matches the given String.
     * @throws Exception In case the property could not be read.
     */
    public DataProvider getDataProviderForProperty(final String propertyKey) throws Exception {
        String dataProviderAsString = this.getConfigurationProperty(propertyKey);

        if (dataProviderAsString == null) {
            return null;
        }

        switch (dataProviderAsString) {
        case "YAHOO":
            return DataProvider.YAHOO;
        case "MARKETWATCH":
            return DataProvider.MARKETWATCH;
        case "INVESTING":
            return DataProvider.INVESTING;
        case "GLOBEANDMAIL":
            return DataProvider.GLOBEANDMAIL;
        case "CNBC":
            return DataProvider.CNBC;
        default:
            return null;
        }
    }

    /**
     * Gets the OkHttpClient.
     *
     * @return the OkHttpClient.
     */
    public OkHttpClient getOkHttpClient() {
        return this.okHttpClient;
    }

    /**
     * Checks if the configuration file with application properties is existing.
     *
     * @throws Exception If config file could not be found.
     */
    private void checkConfigFileExisting() throws Exception {
        String workingDir = System.getProperty("user.dir");
        String filePath = workingDir + SUBPATH_CONFIGURATION_PROPERTIES;

        File f = new File(filePath);

        if (!f.exists() || f.isDirectory()) {
            throw new Exception("Could not find configuration file using path: " + filePath);
        }
    }
}
