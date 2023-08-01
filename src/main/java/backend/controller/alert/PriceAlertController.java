package backend.controller.alert;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.DataProvider;
import backend.controller.MainController;
import backend.model.StockExchange;

/**
 * Controls the process, that cyclically queries Instrument quotes and updates
 * the alerts accordingly.
 *
 * @author Michael
 */
public class PriceAlertController {
    /**
     * Property Key: Query interval.
     */
    protected static final String PROPERTY_QUERY_INTERVAL = "queryInterval.priceAlert";

    /**
     * Property Key: Start Time - Hour.
     */
    protected static final String PROPERTY_START_TIME_HOUR = "startTime.hour";

    /**
     * Property Key: Start Time - Minute.
     */
    protected static final String PROPERTY_START_TIME_MINUTE = "startTime.minute";

    /**
     * Property Key: Start Time - Hour.
     */
    protected static final String PROPERTY_END_TIME_HOUR = "endTime.hour";

    /**
     * Property Key: Start Time - Minute.
     */
    protected static final String PROPERTY_END_TIME_MINUTE = "endTime.minute";

    /**
     * Property Key: Data Provider for stock exchange NYSE.
     */
    protected static final String PROPERTY_DATA_PROVIDER_NYSE = "dataProvider.priceAlert.nyse";

    /**
     * Property Key: Data Provider for stock exchange Nasdaq.
     */
    protected static final String PROPERTY_DATA_PROVIDER_NASDAQ = "dataProvider.priceAlert.ndq";

    /**
     * Property Key: Data Provider for stock exchange TSX.
     */
    protected static final String PROPERTY_DATA_PROVIDER_TSX = "dataProvider.priceAlert.tsx";

    /**
     * Property Key: Data Provider for stock exchange TSX/V.
     */
    protected static final String PROPERTY_DATA_PROVIDER_TSXV = "dataProvider.priceAlert.tsxv";

    /**
     * Property Key: Data Provider for stock exchange CSE.
     */
    protected static final String PROPERTY_DATA_PROVIDER_CSE = "dataProvider.priceAlert.cse";

    /**
     * Property Key: Data Provider for stock exchange LSE.
     */
    protected static final String PROPERTY_DATA_PROVIDER_LSE = "dataProvider.priceAlert.lse";

    /**
     * The interval in seconds between each Instrument quote query.
     */
    private int queryInterval;

    /**
     * The start time of the trading session.
     */
    private LocalTime startTime;

    /**
     * The end time of the trading session.
     */
    private LocalTime endTime;

    /**
     * A Map of stock exchanges and their corresponding data providers.
     */
    private Map<StockExchange, DataProvider> dataProviders;

    /**
     * Executes threads cyclically.
     */
    private ScheduledExecutorService executorService;

    /**
     * Application logging.
     */
    public static final Logger logger = LogManager.getLogger(PriceAlertController.class);

    /**
     * Initialization.
     *
     * @throws Exception In case the initialization failed.
     */
    public PriceAlertController() throws Exception {
        this.initializeQueryInterval();
        this.initializeStartTime();
        this.initializeEndTime();
        this.initializeDataProviders();
    }

    /**
     * @return the queryInterval
     */
    public int getQueryInterval() {
        return queryInterval;
    }

    /**
     * @return the startTime
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * @return the dataProviders
     */
    public Map<StockExchange, DataProvider> getDataProviders() {
        return this.dataProviders;
    }

    /**
     * Starts the query and update process.
     * 
     * @throws Exception Failed to start executor of PriceAlertThread.
     */
    public void start() throws Exception {
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleWithFixedDelay(new PriceAlertThread(this.startTime, this.endTime, this.dataProviders),
                0, this.getQueryInterval(), TimeUnit.SECONDS);
    }

    /**
     * Stops the query and update process.
     */
    public void stop() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Failed to orderly shutdown Thread Executor Service.", e);
        }
    }

    /**
     * Initializes the query interval.
     *
     * @Throws Exception In case the property could not be read or initialized.
     */
    private void initializeQueryInterval() throws Exception {
        String queryInterval = MainController.getInstance().getConfigurationProperty(PROPERTY_QUERY_INTERVAL);
        this.queryInterval = Integer.valueOf(queryInterval);
    }

    /**
     * Initializes the start time.
     *
     * @Throws Exception In case the property could not be read or initialized.
     */
    private void initializeStartTime() throws Exception {
        String startTimeHour, startTimeMinute;

        startTimeHour = MainController.getInstance().getConfigurationProperty(PROPERTY_START_TIME_HOUR);
        startTimeMinute = MainController.getInstance().getConfigurationProperty(PROPERTY_START_TIME_MINUTE);

        this.startTime = LocalTime.of(Integer.valueOf(startTimeHour), Integer.valueOf(startTimeMinute));
    }

    /**
     * Initializes the end time.
     *
     * @Throws Exception In case the property could not be read or initialized.
     */
    private void initializeEndTime() throws Exception {
        String endTimeHour, endTimeMinute;

        endTimeHour = MainController.getInstance().getConfigurationProperty(PROPERTY_END_TIME_HOUR);
        endTimeMinute = MainController.getInstance().getConfigurationProperty(PROPERTY_END_TIME_MINUTE);

        this.endTime = LocalTime.of(Integer.valueOf(endTimeHour), Integer.valueOf(endTimeMinute));
    }

    /**
     * Initializes the relations between stock exchanges and their corresponding
     * data providers.
     *
     * @throws Exception In case a property could not be read or initialized.
     */
    private void initializeDataProviders() throws Exception {
        this.dataProviders = new HashMap<>();

        this.dataProviders.put(StockExchange.NYSE,
                MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_NYSE));
        this.dataProviders.put(StockExchange.NDQ,
                MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_NASDAQ));
        this.dataProviders.put(StockExchange.TSX,
                MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_TSX));
        this.dataProviders.put(StockExchange.TSXV,
                MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_TSXV));
        this.dataProviders.put(StockExchange.CSE,
                MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_CSE));
        this.dataProviders.put(StockExchange.LSE,
                MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_LSE));
    }
}
