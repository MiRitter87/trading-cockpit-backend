package backend.servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import backend.controller.MainController;

/**
 * Handles initialization and destruction of the application context.
 *
 * Initialization is performed on application startup. Destruction is performed on application shutdown.
 *
 * @author Michael
 */
public class BootstrapContextListener implements ServletContextListener {
    /**
     * The MainController of the application.
     */
    private MainController mainController = MainController.getInstance();

    /**
     * Performs application startup routines once the context has been initialized.
     */
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        this.mainController.applicationStartup();
    }

    /**
     * Performs application shutdown routines once the context has been destroyed.
     */
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        this.mainController.applicationShutdown();
    }
}
