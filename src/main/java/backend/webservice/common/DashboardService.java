package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.AggregateIndicatorCalculator;
import backend.controller.chart.priceVolume.DistributionDaysChartController;
import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.list.ListDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.dao.statistic.StatisticDAO;
import backend.model.LocalizedException;
import backend.model.dashboard.MarketHealthStatus;
import backend.model.dashboard.SwingTradingEnvironmentStatus;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.webservice.ScanTemplate;

/**
 * Common implementation of the Dashboard service that can be used by multiple service interfaces like SOAP or REST.
 *
 * @author Michael
 */
public class DashboardService {
    /**
     * DAO for Quotation access.
     */
    private QuotationDAO quotationDAO;

    /**
     * DAO for Statistic access.
     */
    private StatisticDAO statisticDAO;

    /**
     * DAO for List access.
     */
    private ListDAO listDAO;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * The Instrument for which the health status is determined. This can either be a sector or an industry group.
     */
    private Instrument instrument;

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(DashboardService.class);

    /**
     * Initializes the DashboardService.
     */
    public DashboardService() {
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
        this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
        this.listDAO = DAOManager.getInstance().getListDAO();
    }

    /**
     * Determines the health status of the given market (sector or industry group).
     *
     * @param instrumentId The ID of the sector or industry group.
     * @param listId       The ID of the list defining the instruments used to calculate % of stocks above SMA(50)
     *                     (optional).
     * @return The health status.
     */
    public WebServiceResult getMarketHealthStatus(final Integer instrumentId, final Integer listId) {
        WebServiceResult getStatusResult = new WebServiceResult(null);
        MarketHealthStatus marketHealthStatus = new MarketHealthStatus();
        backend.model.list.List list = null;

        try {
            this.initializeInstrument(instrumentId);
            this.validateInstrumentType();
        } catch (LocalizedException exception) {
            getStatusResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, exception.getLocalizedMessage()));
            return getStatusResult;
        }

        try {
            if (listId != null) {
                list = this.listDAO.getList(listId);

                if (list == null) {
                    getStatusResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                            MessageFormat.format(this.resources.getString("list.notFound"), listId)));
                    return getStatusResult;
                }
            }

            this.fillBasicData(marketHealthStatus);
            marketHealthStatus.setSwingTradingEnvironmentStatus(this.getSwingTradingEnvironmentStatus(this.instrument));
            marketHealthStatus.setDistributionDaysSum(this.getDistributionDaysSum());
            marketHealthStatus.setNumberNear52wHigh(this.getNumberNear52wHigh(list));
            marketHealthStatus.setNumberNear52wLow(this.getNumberNear52wLow(list));
            marketHealthStatus.setNumberUpOnVolume(this.getNumberUpOnVolume());
            marketHealthStatus.setNumberDownOnVolume(this.getNumberDownOnVolume());
            marketHealthStatus.setAggregateIndicator(this.getAggregateIndicator(listId));
            getStatusResult.setData(marketHealthStatus);
        } catch (LocalizedException localizedException) {
            getStatusResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, localizedException.getLocalizedMessage()));
        } catch (Exception e) {
            getStatusResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    this.resources.getString("dashboard.getMarketHealthStatusError")));
            LOGGER.error(this.resources.getString("dashboard.getMarketHealthStatusError"), e);
        }

        return getStatusResult;
    }

    /**
     * Initializes the Instrument for which the health status is determined.
     *
     * @param instrumentId The id of the Instrument.
     * @throws LocalizedException Initialization failed.
     */
    private void initializeInstrument(final Integer instrumentId) throws LocalizedException {
        InstrumentDAO instrumentDAO = DAOManager.getInstance().getInstrumentDAO();

        try {
            this.instrument = instrumentDAO.getInstrument(instrumentId);
            this.instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
        } catch (Exception e) {
            throw new LocalizedException("instrument.notFound", instrumentId);
        }
    }

    /**
     * Fills the basic data of the MarketHealthStatus. These are data than are directly copied from an Instrument or its
     * referenced Quotation and Indicator.
     *
     * @param marketHealthStatus The MarketHealthStatus whose data are filled.
     */
    private void fillBasicData(final MarketHealthStatus marketHealthStatus) {
        List<Quotation> quotationsSortedByDate = this.instrument.getQuotationsSortedByDate();

        marketHealthStatus.setSymbol(this.instrument.getSymbol());
        marketHealthStatus.setName(this.instrument.getName());

        if (quotationsSortedByDate.size() > 0) {
            marketHealthStatus.setDate(quotationsSortedByDate.get(0).getDate());
            marketHealthStatus
                    .setUpDownVolumeRatio(quotationsSortedByDate.get(0).getIndicator().getUpDownVolumeRatio());
            marketHealthStatus.setRsNumber(quotationsSortedByDate.get(0).getRelativeStrengthData().getRsNumber());
        }
    }

    /**
     * Validates the InstrumentType of the Instrument. The market health status can only be determined for instruments
     * of type sector or industry group.
     *
     * @throws LocalizedException In case the InstrumentType is not allowed.
     */
    private void validateInstrumentType() throws LocalizedException {
        if (this.instrument.getType() != InstrumentType.SECTOR
                && this.instrument.getType() != InstrumentType.IND_GROUP) {
            throw new LocalizedException("dashboard.wrongInstrumentType");
        }
    }

    /**
     * Determine the traffic-light status of the Swingtrading Environment.
     *
     * @param instrumentWithQuotations The Instrument with the Quotation history.
     * @return The SwingTradingEnvironmentStatus.
     */
    public SwingTradingEnvironmentStatus getSwingTradingEnvironmentStatus(final Instrument instrumentWithQuotations) {
        if (this.isStatusGreen(instrumentWithQuotations)) {
            return SwingTradingEnvironmentStatus.GREEN;
        } else if (this.isStatusYellow(instrumentWithQuotations)) {
            return SwingTradingEnvironmentStatus.YELLOW;
        } else if (this.isStatusRed(instrumentWithQuotations)) {
            return SwingTradingEnvironmentStatus.RED;
        }

        return null;
    }

    /**
     * Checks if the SwingTradingEnvironmentStatus of the given Instrument is 'GREEN'.
     *
     * @param instrumentWithQuotations The Instrument with the Quotation history.
     * @return true, if status is 'GREEN'; false, if not.
     */
    private boolean isStatusGreen(final Instrument instrumentWithQuotations) {
        List<Quotation> quotations;
        Quotation actualQuotation;
        Quotation previousQuotation;
        MovingAverageData actualMa;
        MovingAverageData previousMa;

        if (instrumentWithQuotations == null || instrumentWithQuotations.getQuotations() == null
                || instrumentWithQuotations.getQuotations().size() < 2) {
            return false;
        }

        quotations = instrumentWithQuotations.getQuotationsSortedByDate();
        actualQuotation = quotations.get(0);
        previousQuotation = quotations.get(1);
        actualMa = actualQuotation.getMovingAverageData();
        previousMa = previousQuotation.getMovingAverageData();

        if (actualMa == null || previousMa == null) {
            return false;
        }

        if (actualMa.getSma10() <= actualMa.getSma20()) {
            return false;
        }

        if (actualMa.getSma10() <= previousMa.getSma10()) {
            return false;
        }

        if (actualMa.getSma20() <= previousMa.getSma20()) {
            return false;
        }

        if (actualQuotation.getClose().floatValue() <= actualMa.getSma20()) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the SwingTradingEnvironmentStatus of the given Instrument is 'YELLOW'.
     *
     * @param instrumentWithQuotations The Instrument with the Quotation history.
     * @return true, if status is 'YELLOW'; false, if not.
     */
    private boolean isStatusYellow(final Instrument instrumentWithQuotations) {
        List<Quotation> quotations;
        Quotation actualQuotation;
        MovingAverageData actualMa;

        if (instrumentWithQuotations == null || instrumentWithQuotations.getQuotations() == null
                || instrumentWithQuotations.getQuotations().size() < 2) {
            return false;
        }

        quotations = instrumentWithQuotations.getQuotationsSortedByDate();
        actualQuotation = quotations.get(0);
        actualMa = actualQuotation.getMovingAverageData();

        if (actualMa == null) {
            return false;
        }

        if (actualMa.getSma10() <= actualMa.getSma20()) {
            return false;
        }

        if (actualQuotation.getClose().floatValue() <= actualMa.getSma20()) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the SwingTradingEnvironmentStatus of the given Instrument is 'RED'.
     *
     * @param instrumentWithQuotations The Instrument with the Quotation history.
     * @return true, if status is 'RED'; false, if not.
     */
    private boolean isStatusRed(final Instrument instrumentWithQuotations) {
        List<Quotation> quotations;
        Quotation actualQuotation;
        MovingAverageData actualMa;

        if (instrumentWithQuotations == null || instrumentWithQuotations.getQuotations() == null
                || instrumentWithQuotations.getQuotations().size() < 2) {
            return false;
        }

        quotations = instrumentWithQuotations.getQuotationsSortedByDate();
        actualQuotation = quotations.get(0);
        actualMa = actualQuotation.getMovingAverageData();

        if (actualMa == null) {
            return false;
        }

        if (actualQuotation.getClose().floatValue() <= actualMa.getSma20()
                || actualMa.getSma10() <= actualMa.getSma20()) {
            return true;
        }

        return false;
    }

    /**
     * Determines the sum of Distribution Days within the last 25 trading days.
     *
     * @return The sum of Distribution Days.
     */
    private int getDistributionDaysSum() {
        DistributionDaysChartController distributionDaysChartController = new DistributionDaysChartController();
        List<Quotation> quotationsSortedByDate = this.instrument.getQuotationsSortedByDate();

        return distributionDaysChartController.getDistributionDaysSum(quotationsSortedByDate.get(0),
                quotationsSortedByDate);
    }

    /**
     * Determines the number of instruments that trade near the 52-week high. Only those instruments are taken into
     * account where the given Instrument is referenced as sector or industry group.
     *
     * @param list The List used to specify the instruments used for number determination.
     * @return The number of instruments near the 52-week high.
     */
    private int getNumberNear52wHigh(final backend.model.list.List list) {
        List<Quotation> allInstrumentsNear52wHigh;
        int numberNear52wHigh = 0;
        Instrument sector;
        Instrument industryGroup;

        try {
            allInstrumentsNear52wHigh = this.quotationDAO.getQuotationsByTemplate(ScanTemplate.NEAR_52_WEEK_HIGH,
                    InstrumentType.STOCK, null, null, null);

            for (Quotation tempQuotation : allInstrumentsNear52wHigh) {
                sector = tempQuotation.getInstrument().getSector();
                industryGroup = tempQuotation.getInstrument().getIndustryGroup();

                if (industryGroup != null && industryGroup.getId().equals(this.instrument.getId())
                        && this.isInstrumentPartOfList(tempQuotation.getInstrument(), list)) {
                    numberNear52wHigh++;
                } else if (sector != null && sector.getId().equals(this.instrument.getId())
                        && this.isInstrumentPartOfList(tempQuotation.getInstrument(), list)) {
                    numberNear52wHigh++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to determine number of stocks near 52-week high.", e);
        }

        return numberNear52wHigh;
    }

    /**
     * Determines the number of instruments that trade near the 52-week low. Only those instruments are taken into
     * account where the given Instrument is referenced as sector or industry group.
     *
     * @param list The List used to specify the instruments used for number determination.
     * @return The number of instruments near the 52-week low.
     */
    private int getNumberNear52wLow(final backend.model.list.List list) {
        List<Quotation> allInstrumentsNear52wLow;
        int numberNear52wLow = 0;
        Instrument sector;
        Instrument industryGroup;

        try {
            allInstrumentsNear52wLow = this.quotationDAO.getQuotationsByTemplate(ScanTemplate.NEAR_52_WEEK_LOW,
                    InstrumentType.STOCK, null, null, null);

            for (Quotation tempQuotation : allInstrumentsNear52wLow) {
                sector = tempQuotation.getInstrument().getSector();
                industryGroup = tempQuotation.getInstrument().getIndustryGroup();

                if (industryGroup != null && industryGroup.getId().equals(this.instrument.getId())
                        && this.isInstrumentPartOfList(tempQuotation.getInstrument(), list)) {
                    numberNear52wLow++;
                } else if (sector != null && sector.getId().equals(this.instrument.getId())
                        && this.isInstrumentPartOfList(tempQuotation.getInstrument(), list)) {
                    numberNear52wLow++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to determine number of stocks near 52-week low.", e);
        }

        return numberNear52wLow;
    }

    /**
     * Determines the number of instruments that have traded "Up on Volume" during the last 5 trading days. That is at
     * least 3% up on a day on above-average volume. Only those instruments are taken into account where the given
     * Instrument is referenced as sector or industry group.
     *
     * @return The number of instruments trading "Up on Volume".
     */
    private int getNumberUpOnVolume() {
        List<Statistic> statistics = new ArrayList<>();
        Statistic statistic;
        final int numberOfDays = 5;
        int numberUpOnVolume = 0;

        try {
            if (this.instrument.getType() == InstrumentType.SECTOR) {
                statistics = this.statisticDAO.getStatistics(InstrumentType.STOCK, this.instrument.getId(), null);
            } else if (this.instrument.getType() == InstrumentType.IND_GROUP) {
                statistics = this.statisticDAO.getStatistics(InstrumentType.STOCK, null, this.instrument.getId());
            }

            if (statistics.size() == 0) {
                return 0;
            }

            for (int i = 0; i < numberOfDays; i++) {
                statistic = statistics.get(i);
                numberUpOnVolume += statistic.getNumberUpOnVolume();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to determine number of stocks trading Up on Volume.", e);
        }

        return numberUpOnVolume;
    }

    /**
     * Determines the number of instruments that have traded "Down on Volume" during the last 5 trading days. That is at
     * least 3% down on a day on above-average volume. Only those instruments are taken into account where the given
     * Instrument is referenced as sector or industry group.
     *
     * @return The number of instruments trading "Down on Volume".
     */
    private int getNumberDownOnVolume() {
        List<Statistic> statistics = new ArrayList<>();
        Statistic statistic;
        final int numberOfDays = 5;
        int numberDownOnVolume = 0;

        try {
            if (this.instrument.getType() == InstrumentType.SECTOR) {
                statistics = this.statisticDAO.getStatistics(InstrumentType.STOCK, this.instrument.getId(), null);
            } else if (this.instrument.getType() == InstrumentType.IND_GROUP) {
                statistics = this.statisticDAO.getStatistics(InstrumentType.STOCK, null, this.instrument.getId());
            }

            if (statistics.size() == 0) {
                return 0;
            }

            for (int i = 0; i < numberOfDays; i++) {
                statistic = statistics.get(i);
                numberDownOnVolume += statistic.getNumberDownOnVolume();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to determine number of stocks trading Down on Volume.", e);
        }

        return numberDownOnVolume;
    }

    /**
     * Determines the Aggregate Indicator.
     *
     * @param listId The ID of the list defining the instruments used to calculate % of stocks above SMA(50) (optional).
     * @return The value of the Aggregate Indicator.
     * @throws Exception Error during data retrieval.
     */
    private int getAggregateIndicator(final Integer listId) throws Exception {
        AggregateIndicatorCalculator calculator = new AggregateIndicatorCalculator();
        List<Statistic> statistics = calculator.getStatistics(this.instrument, listId);
        List<Quotation> quotationsSortedByDate = this.instrument.getQuotationsSortedByDate();
        Quotation newestQuotation = quotationsSortedByDate.get(0);
        int aggregateIndicator;

        aggregateIndicator = calculator.getAggregateIndicator(quotationsSortedByDate, statistics, newestQuotation,
                this.instrument);

        return aggregateIndicator;
    }

    /**
     * Checks if the given Instrument is part of the given List.
     *
     * @param instrumentToCheck The Instrument to be checked.
     * @param list              The List used for check.
     * @return true if instrument is part of list or if no list is given.
     */
    private boolean isInstrumentPartOfList(final Instrument instrumentToCheck, final backend.model.list.List list) {
        Instrument containedInstrument = null;

        if (list == null) {
            return true;
        }

        containedInstrument = list.getInstrumentWithId(instrumentToCheck.getId());

        if (containedInstrument != null) {
            return true;
        }

        return false;
    }
}
