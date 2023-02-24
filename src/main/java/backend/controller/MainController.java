package backend.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;


/**
 * The main controller of the web application.
 * 
 * @author Michael
 */
public class MainController {
	/**
	 * File path to configuration properties
	 */
	protected static final String SUBPATH_CONFIGURATION_PROPERTIES = "/conf/tradingCockpitBackend.properties";
	
	/**
	 * Instance of the main controller.
	 */
	private static MainController instance;
	
	/**
	 * Queries stock quotes and triggers stock alerts.
	 */
	private StockAlertController stockAlertController;
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(MainController.class);
	
	
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
		if(instance == null)
			instance = new MainController();
		
		return instance;
	}
	
	
	/**
	 * Performs tasks on application startup.
	 */
	public void applicationStartup() {
		DAOManager.getInstance();
		
		try {
			this.stockAlertController = new StockAlertController();
			this.stockAlertController.start();
		} catch (Exception e) {
			logger.error("The query mechanism for stock alerts failed to start.", e);
		}
		
		System.out.println("Application started.");
	}

	/**
	 * Performs tasks on application shutdown.
	 */
	public void applicationShutdown() {
		try {
			DAOManager.getInstance().close();
			
			if(this.stockAlertController != null)
				this.stockAlertController.stop();
			
			System.out.println("Application stopped");
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
		
		switch(dataProviderAsString) {
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
}
