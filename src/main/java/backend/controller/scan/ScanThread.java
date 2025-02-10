package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.DataProvider;
import backend.controller.DataRetrievalThread;
import backend.controller.RatioCalculator;
import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.scan.ScanDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.scan.Scan;
import backend.model.scan.ScanCompletionStatus;
import backend.model.scan.ScanExecutionStatus;

/**
 * Queries historical stock quotes of instruments that are part of a scan. Furthermore calculates indicators.
 *
 * @author Michael
 */
public class ScanThread extends DataRetrievalThread {
    /**
     * The interval in seconds between queries of historical quotations.
     */
    private int queryInterval;

    /**
     * The scan that is executed.
     */
    private Scan scan;

    /**
     * Indication to only scan incomplete instruments of the scan.
     */
    private boolean scanOnlyIncompleteInstruments;

    /**
     * DAO to access quotations of the database.
     */
    private QuotationDAO quotationDAO;

    /**
     * DAO for scan persistence.
     */
    private ScanDAO scanDAO;

    /**
     * Controller organizing the calculation of indicators.
     */
    private IndicatorCalculationController indicatorCalculator;

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(ScanThread.class);

    /**
     * Initializes the scan thread.
     *
     * @param queryInterval                 The interval in seconds between each historical quotation query.
     * @param dataProviders                 Stock exchanges and their corresponding data providers.
     * @param scan                          The scan that is executed by the thread.
     * @param scanOnlyIncompleteInstruments Indication to only scan incomplete instruments of the scan.
     */
    public ScanThread(final int queryInterval, final Map<StockExchange, DataProvider> dataProviders, final Scan scan,
            final boolean scanOnlyIncompleteInstruments) {

        this.setDataProviders(dataProviders);
        this.queryInterval = queryInterval;
        this.scan = scan;
        this.scanOnlyIncompleteInstruments = scanOnlyIncompleteInstruments;

        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
        this.scanDAO = DAOManager.getInstance().getScanDAO();

        this.indicatorCalculator = new IndicatorCalculationController();
    }

    /**
     * The main method of the thread that is executed.
     */
    @Override
    public void run() {
        Set<Instrument> instruments;
        Iterator<Instrument> instrumentIterator;
        Instrument instrument;
        int instrumentsProcessed = 0;
        final int millisPerSecond = 1000;

        LOGGER.info("Starting execution of scan with ID: " + this.scan.getId());

        instruments = this.getInstrumentsOfScan();
        instrumentIterator = instruments.iterator();

        while (instrumentIterator.hasNext()) {
            instrument = instrumentIterator.next();
            this.updateInstrument(instrument);

            instrumentsProcessed++;
            this.updateScanProgress(instrumentsProcessed, instruments.size());

            try {
                sleep(this.queryInterval * millisPerSecond);
            } catch (InterruptedException e) {
                LOGGER.info("Sleeping scan thread has been interrupted.", e);
            }
        }

        this.updateRSNumbers();
        this.updateStatistics();
        this.setScanToFinished();
        LOGGER.info("Finished execution of scan with ID: " + this.scan.getId());
    }

    /**
     * Provides all instruments that are to be updated during the current scan run.
     *
     * @return The instruments for the current scan run.
     */
    private Set<Instrument> getInstrumentsOfScan() {
        if (this.scanOnlyIncompleteInstruments) {
            Set<Instrument> incompleteInstruments = new HashSet<>();
            // A copy of the scans Set is provided allowing for deletion during iteration.
            incompleteInstruments.addAll(this.scan.getIncompleteInstruments());
            return incompleteInstruments;
        } else {
            return this.scan.getInstrumentsFromScanLists();
        }
    }

    /**
     * Updates quotations and indicators of the given instrument.
     *
     * @param instrument The instrument to be updated.
     */
    private void updateInstrument(final Instrument instrument) {
        this.updateQuotationsOfInstrument(instrument);
        this.updateIndicatorsOfInstrument(instrument);
    }

    /**
     * Updates the quotations of the given Instrument. Persists new quotations.
     *
     * @param instrument The Instrument to be updated.
     */
    private void updateQuotationsOfInstrument(final Instrument instrument) {
        if (instrument.getType() == InstrumentType.RATIO) {
            this.updateQuotationsRatio(instrument);
        } else {
            if (instrument.getDataSourceList() == null) {
                this.updateQuotationsNonRatio(instrument);
            } else {
                this.updateQuotationsFromList(instrument);
            }
        }
    }

    /**
     * Queries a third party WebService to get historical quotations of the given Instrument. Persists new quotations.
     *
     * @param instrument The Instrument to be updated.
     */
    private void updateQuotationsNonRatio(final Instrument instrument) {
        Quotation databaseQuotation;
        List<Quotation> databaseQuotations = new ArrayList<>();
        List<Quotation> newQuotations = new ArrayList<>();
        Set<Quotation> obsoleteQuotations = new HashSet<>();
        QuotationProviderDAO quotationProviderDAO;
        final int thresholdDaysLogQuotationAge = 5;

        try {
            quotationProviderDAO = this.getQuotationProviderDAO(instrument.getStockExchange());
            databaseQuotations.addAll(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));
            instrument.setQuotations(databaseQuotations);
            List<Quotation> wsQuotations = quotationProviderDAO.getQuotationHistory(instrument, 1);

            for (Quotation wsQuotation : wsQuotations) {
                obsoleteQuotations
                        .addAll(instrument.getQuotationArray().getOlderQuotationsOfSameDay(wsQuotation.getDate()));
                databaseQuotation = instrument.getQuotationByDate(wsQuotation.getDate());

                if (databaseQuotation == null) {
                    wsQuotation.setInstrument(instrument);
                    newQuotations.add(wsQuotation);
                }
            }

            if (newQuotations.size() > 0) {
                this.quotationDAO.insertQuotations(newQuotations);
            }

            if (obsoleteQuotations.size() > 0) {
                this.quotationDAO.deleteQuotations(new ArrayList<>(obsoleteQuotations));
            }

            this.checkAgeOfNewestQuotation(instrument.getSymbol(), wsQuotations, thresholdDaysLogQuotationAge);

            this.scan.getIncompleteInstruments().remove(instrument);
        } catch (Exception e) {
            this.scan.addIncompleteInstrument(instrument);
            if (e.getCause() instanceof SocketException) {
                LOGGER.error("Failed to update quotations of instrument with ID " + instrument.getId()
                        + " (Socket Closed).");
            } else {
                LOGGER.error("Failed to update quotations of instrument with ID " + instrument.getId(), e);
            }
        }
    }

    /**
     * Uses existing quotations of instruments to calculate quotations for a ratio. Persists new quotations.
     *
     * @param instrument The Instrument to be updated.
     */
    private void updateQuotationsRatio(final Instrument instrument) {
        Quotation existingQuotation;
        List<Quotation> newQuotations = new ArrayList<>();
        List<Quotation> ratioQuotations = new ArrayList<>();
        RatioCalculator ratioCalculator = new RatioCalculator();

        // 1. Calculate ratio quotations based on dividend and divisor quotations.
        try {
            instrument.getDividend()
                    .setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrument.getDividend().getId()));
            instrument.getDivisor()
                    .setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrument.getDivisor().getId()));
            ratioQuotations = ratioCalculator.getRatios(instrument.getDividend(), instrument.getDivisor());
        } catch (Exception exception) {
            this.scan.addIncompleteInstrument(instrument);
            LOGGER.warn("Could not calculate ratio for instrument with ID " + instrument.getId() + ". "
                    + exception.getMessage());
            return;
        }

        // 2. Update quotations of ratio Instrument.
        try {
            instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));

            for (Quotation ratioQuotation : ratioQuotations) {
                existingQuotation = instrument.getQuotationByDate(ratioQuotation.getDate());

                if (existingQuotation == null) {
                    ratioQuotation.setInstrument(instrument);
                    newQuotations.add(ratioQuotation);
                }
            }

            if (newQuotations.size() > 0) {
                this.quotationDAO.insertQuotations(newQuotations);
            }

            this.scan.getIncompleteInstruments().remove(instrument);
        } catch (Exception exception) {
            this.scan.addIncompleteInstrument(instrument);
            LOGGER.error("Failed to update quotations of instrument with ID " + instrument.getId(), exception);
        }
    }

    /**
     * Uses the quotations of all instruments referenced in the 'dataSourceList' attribute to calculate quotations. No
     * Third-Party WebService is queried.
     *
     * @param instrument The Instrument to be updated.
     */
    private void updateQuotationsFromList(final Instrument instrument) {
        QuotationCalculator calculator = new QuotationCalculator();
        List<Instrument> instruments = new ArrayList<>(instrument.getDataSourceList().getInstruments());
        List<Quotation> calculatedQuotations = new ArrayList<>();
        List<Quotation> newQuotations = new ArrayList<>();
        Quotation existingQuotation;

        // 1. Initialize the instruments with quotations loaded from the database.
        try {
            for (Instrument tempInstrument : instruments) {
                tempInstrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(tempInstrument.getId()));

                if (tempInstrument.getQuotations().size() == 0) {
                    LOGGER.warn("No quotations exist for Instrument with ID " + instrument.getId());
                }
            }
        } catch (Exception exception) {
            this.scan.addIncompleteInstrument(instrument);
            LOGGER.error("Could not load quotations of instrument with ID " + instrument.getId(),
                    exception.getMessage());
            return;
        }

        // 2. Calculate the quotations of the instrument.
        calculatedQuotations = calculator.getCalculatedQuotations(instruments);

        // 3. Update calculated quotations of Instrument.
        try {
            instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));

            for (Quotation calculatedQuotation : calculatedQuotations) {
                existingQuotation = instrument.getQuotationByDate(calculatedQuotation.getDate());

                if (existingQuotation == null) {
                    calculatedQuotation.setInstrument(instrument);
                    newQuotations.add(calculatedQuotation);
                }
            }

            if (newQuotations.size() > 0) {
                this.quotationDAO.insertQuotations(newQuotations);
            }

            this.scan.getIncompleteInstruments().remove(instrument);
        } catch (Exception exception) {
            this.scan.addIncompleteInstrument(instrument);
            LOGGER.error("Failed to update quotations of instrument with ID " + instrument.getId(), exception);
        }
    }

    /**
     * Updates the indicators of the most recent quotation of the given Instrument.
     *
     * @param instrument The Instrument to be updated.
     */
    private void updateIndicatorsOfInstrument(final Instrument instrument) {
        List<Quotation> sortedQuotations;
        List<Quotation> modifiedQuotations = new ArrayList<>();
        List<Quotation> databaseQuotations = new ArrayList<>();
        Quotation quotation;

        try {
            // Read quotations of Instrument from database to get quotations with IDs needed for setting the Indicator
            // ID.
            databaseQuotations.addAll(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));
            instrument.setQuotations(databaseQuotations);
            sortedQuotations = instrument.getQuotationsSortedByDate();

            if (sortedQuotations.size() == 0) {
                return;
            }

            for (int i = 0; i < sortedQuotations.size(); i++) {
                quotation = sortedQuotations.get(i);

                if (i == 0) {
                    quotation = this.indicatorCalculator.calculateIndicators(instrument, quotation, true);
                } else {
                    quotation = this.indicatorCalculator.calculateIndicators(instrument, quotation, false);
                }

                modifiedQuotations.add(quotation);
            }

            this.quotationDAO.updateQuotations(modifiedQuotations);
        } catch (Exception exception) {
            LOGGER.error("Failed to retrieve or update indicators of instrument with ID " + instrument.getId(),
                    exception);
        }
    }

    /**
     * Updates the RS number of all instruments that have quotations and an Indicator for the most recent date defined.
     * Separate RS numbers are calculated based on the InstrumentType.
     */
    private void updateRSNumbers() {
        try {
            List<Quotation> allQuotations = new ArrayList<>();
            List<Quotation> quotationsTypeStock = this.quotationDAO.getRecentQuotations(InstrumentType.STOCK);
            List<Quotation> quotationsTypeETF = this.quotationDAO.getRecentQuotations(InstrumentType.ETF);
            List<Quotation> quotationsTypeSector = this.quotationDAO.getRecentQuotations(InstrumentType.SECTOR);
            List<Quotation> quotationsTypeIndustryGroup = this.quotationDAO
                    .getRecentQuotations(InstrumentType.IND_GROUP);
            List<Quotation> quotationsTypeRatio = this.quotationDAO.getRecentQuotations(InstrumentType.RATIO);

            this.indicatorCalculator.calculateRsNumbers(quotationsTypeStock);
            this.indicatorCalculator.calculateRsNumbers(quotationsTypeETF);
            this.indicatorCalculator.calculateRsNumbers(quotationsTypeSector);
            this.indicatorCalculator.calculateRsNumbers(quotationsTypeIndustryGroup);
            this.indicatorCalculator.calculateRsNumbers(quotationsTypeRatio);

            allQuotations.addAll(quotationsTypeStock);
            allQuotations.addAll(quotationsTypeETF);
            allQuotations.addAll(quotationsTypeSector);
            allQuotations.addAll(quotationsTypeIndustryGroup);
            allQuotations.addAll(quotationsTypeRatio);
            this.quotationDAO.updateQuotations(allQuotations);
        } catch (Exception e) {
            LOGGER.error("Failed to calculate RS numbers.", e);
        }
    }

    /**
     * Updates the status field 'progress' of the running scan.
     *
     * @param numberOfInstrumentsCompleted The number of instruments that already have been scanned.
     * @param totalNumberOfInstruments     The total number of instruments of the scan.
     */
    private void updateScanProgress(final int numberOfInstrumentsCompleted, final int totalNumberOfInstruments) {
        BigDecimal progress;
        BigDecimal instrumentsCompleted;
        BigDecimal numberOfInstruments;
        int roundedProgress = 0;
        final int hundredPercent = 100;

        instrumentsCompleted = BigDecimal.valueOf(numberOfInstrumentsCompleted);
        numberOfInstruments = BigDecimal.valueOf(totalNumberOfInstruments);

        progress = instrumentsCompleted.divide(numberOfInstruments, 2, RoundingMode.HALF_UP);
        progress = progress.multiply(BigDecimal.valueOf(hundredPercent));
        roundedProgress = progress.intValue();

        if (this.scan.getProgress().equals(roundedProgress)) {
            return;
        }

        try {
            this.scan.setProgress(roundedProgress);
            this.scanDAO.updateScan(this.scan);
        } catch (ObjectUnchangedException e) {
            LOGGER.error("Tried to update 'progress' in scan process but value did not change.", e);
        } catch (Exception e) {
            LOGGER.error("Failed to update 'progress' of scan during scan process.", e);
        }
    }

    /**
     * Sets the status of the scan to 'FINISHED' and updates the date of the last scan.
     */
    private void setScanToFinished() {
        try {
            this.scan.setLastScan(new Date());
            this.scan.setExecutionStatus(ScanExecutionStatus.FINISHED);

            if (this.scan.getIncompleteInstruments().size() == 0) {
                this.scan.setCompletionStatus(ScanCompletionStatus.COMPLETE);
            } else {
                this.scan.setCompletionStatus(ScanCompletionStatus.INCOMPLETE);
            }

            this.scanDAO.updateScan(this.scan);
        } catch (ObjectUnchangedException e) {
            LOGGER.error("The scan was executed although being already in status 'FINISHED'.", e);
        } catch (Exception e) {
            LOGGER.error("Failed to update scan status at the end of the scan process.", e);
        }
    }

    /**
     * Updates the statistics.
     */
    private void updateStatistics() {
        StatisticCalculationController statisticCalculationController = new StatisticCalculationController();

        try {
            statisticCalculationController.updateStatistic();
        } catch (Exception e) {
            LOGGER.error("Failed to update the statistics.", e);
        }
    }

    /**
     * Checks the age of the newest Quotation. Logs a message if the newest Quotation is older than dayThreshold days.
     *
     * @param symbol       The symbol of the Instrument.
     * @param quotations   The quotations whose dates are checked.
     * @param dayThreshold The threshold in days used to log.
     */
    private void checkAgeOfNewestQuotation(final String symbol, final List<Quotation> quotations,
            final int dayThreshold) {
        QuotationArray quotationArray = new QuotationArray(quotations);
        long quotationAgeDays;

        quotationAgeDays = quotationArray.getAgeOfNewestQuotationInDays();

        if (quotationAgeDays >= dayThreshold) {
            LOGGER.warn(MessageFormat.format("The newest Quotation data of symbol {0} are {1} days old.", symbol,
                    quotationAgeDays));
        }
    }
}
