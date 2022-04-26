package backend.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import backend.controller.MainController;

/**
 * Handles initialization and destruction of the application context.
 * 
 * Initialization is performed on application startup.
 * Destruction is performed on application shutdown. 
 * 
 * @author Michael
 */
public class BootstrapContextListener implements ServletContextListener {
	private MainController mainController = MainController.getInstance();
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		this.mainController.applicationStartup();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		this.mainController.applicationShutdown();
	}
}
