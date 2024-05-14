package backend.webservice.common;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.chart.priceVolume.DistributionDaysChartController;
import backend.controller.scan.StatisticCalculationController;
import backend.controller.scan.StochasticCalculator;
import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.LocalizedException;
import backend.model.dashboard.MarketHealthStatus;
import backend.model.dashboard.SwingTradingEnvironmentStatus;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
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
     * DAO to access Instrument data.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * DAO for Quotation access.
     */
    private QuotationDAO quotationDAO;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(DashboardService.class);

    /**
     * Initializes the DashboardService.
     */
    public DashboardService() {
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
    }

    /**
     * Determines the health status of the given market (sector or industry group).
     *
     * @param instrumentId The ID of the sector or industry group.
     * @return The health status.
     */
    public WebServiceResult getMarketHealthStatus(final Integer instrumentId) {
        WebServiceResult getStatusResult = new WebServiceResult(null);
        MarketHealthStatus marketHealthStatus = new MarketHealthStatus();
        Instrument instrument;

        try {
            instrument = this.instrumentDAO.getInstrument(instrumentId);

            if (instrument == null) {
                getStatusResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                        MessageFormat.format(this.resources.getString("instrument.notFound"), instrumentId)));
                return getStatusResult;
            }

            this.validateInstrumentType(instrument);
            instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
            this.fillBasicData(instrument, marketHealthStatus);
            marketHealthStatus.setSwingTradingEnvironmentStatus(this.getSwingTradingEnvironmentStatus(instrument));
            marketHealthStatus.setDistributionDaysSum(this.getDistributionDaysSum(instrument));
            marketHealthStatus.setNumberNear52wHigh(this.getNumberNear52wHigh(instrument));
            marketHealthStatus.setNumberNear52wLow(this.getNumberNear52wLow(instrument));
            marketHealthStatus.setNumberUpOnVolume(this.getNumberUpOnVolume(instrument));
            marketHealthStatus.setNumberDownOnVolume(this.getNumberDownOnVolume(instrument));
            marketHealthStatus.setAggregateIndicator(this.getAggregateIndicator(instrument));
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
     * Fills the basic data of the MarketHealthStatus. These are data than are directly copied from an Instrument or its
     * referenced Quotation and Indicator.
     *
     * @param instrument         The Instrument with the Quotation history.
     * @param marketHealthStatus The MarketHealthStatus whose data are filled.
     */
    private void fillBasicData(final Instrument instrument, final MarketHealthStatus marketHealthStatus) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();

        marketHealthStatus.setSymbol(instrument.getSymbol());
        marketHealthStatus.setName(instrument.getName());

        if (quotationsSortedByDate.size() > 0) {
            marketHealthStatus.setDate(quotationsSortedByDate.get(0).getDate());
            marketHealthStatus
                    .setUpDownVolumeRatio(quotationsSortedByDate.get(0).getIndicator().getUpDownVolumeRatio());
            marketHealthStatus.setRsNumber(quotationsSortedByDate.get(0).getRelativeStrengthData().getRsNumber());
        }
    }

    /**
     * Validates the InstrumentType of the given Instrument. The market health status can only be determined for
     * instruments of type sector or industry group.
     *
     * @param instrument The Instrument.
     * @throws LocalizedException In case the InstrumentType is not allowed.
     */
    private void validateInstrumentType(final Instrument instrument) throws LocalizedException {
        if (instrument.getType() != InstrumentType.SECTOR && instrument.getType() != InstrumentType.IND_GROUP) {
            throw new LocalizedException("dashboard.wrongInstrumentType");
        }
    }

    /**
     * Determine the traffic-light status of the Swingtrading Environment.
     *
     * @param instrument The Instrument with the Quotation history.
     * @return The SwingTradingEnvironmentStatus.
     */
    public SwingTradingEnvironmentStatus getSwingTradingEnvironmentStatus(final Instrument instrument) {
        if (this.isStatusGreen(instrument)) {
            return SwingTradingEnvironmentStatus.GREEN;
        } else if (this.isStatusYellow(instrument)) {
            return SwingTradingEnvironmentStatus.YELLOW;
        } else if (this.isStatusRed(instrument)) {
            return SwingTradingEnvironmentStatus.RED;
        }

        return null;
    }

    /**
     * Checks if the SwingTradingEnvironmentStatus of the given Instrument is 'GREEN'.
     *
     * @param instrument The Instrument with the Quotation history.
     * @return true, if status is 'GREEN'; false, if not.
     */
    private boolean isStatusGreen(final Instrument instrument) {
        List<Quotation> quotations;
        Quotation actualQuotation;
        Quotation previousQuotation;
        MovingAverageData actualMa;
        MovingAverageData previousMa;

        if (instrument == null || instrument.getQuotations() == null || instrument.getQuotations().size() < 2) {
            return false;
        }

        quotations = instrument.getQuotationsSortedByDate();
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
     * @param instrument The Instrument with the Quotation history.
     * @return true, if status is 'YELLOW'; false, if not.
     */
    private boolean isStatusYellow(final Instrument instrument) {
        List<Quotation> quotations;
        Quotation actualQuotation;
        MovingAverageData actualMa;

        if (instrument == null || instrument.getQuotations() == null || instrument.getQuotations().size() < 2) {
            return false;
        }

        quotations = instrument.getQuotationsSortedByDate();
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
     * @param instrument The Instrument with the Quotation history.
     * @return true, if status is 'RED'; false, if not.
     */
    private boolean isStatusRed(final Instrument instrument) {
        List<Quotation> quotations;
        Quotation actualQuotation;
        MovingAverageData actualMa;

        if (instrument == null || instrument.getQuotations() == null || instrument.getQuotations().size() < 2) {
            return false;
        }

        quotations = instrument.getQuotationsSortedByDate();
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
     * @param instrument The Instrument.
     * @return The sum of Distribution Days.
     */
    private int getDistributionDaysSum(final Instrument instrument) {
        DistributionDaysChartController distributionDaysChartController = new DistributionDaysChartController();
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();

        return distributionDaysChartController.getDistributionDaysSum(quotationsSortedByDate.get(0),
                quotationsSortedByDate);
    }

    /**
     * Determines the number of instruments that trade near the 52-week high. Only those instruments are taken into
     * account where the given Instrument is referenced as sector or industry group.
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The number of instruments near the 52-week high.
     */
    private int getNumberNear52wHigh(final Instrument instrument) {
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

                if (industryGroup != null && industryGroup.getId().equals(instrument.getId())) {
                    numberNear52wHigh++;
                } else if (sector != null && sector.getId().equals(instrument.getId())) {
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
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The number of instruments near the 52-week low.
     */
    private int getNumberNear52wLow(final Instrument instrument) {
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

                if (industryGroup != null && industryGroup.getId().equals(instrument.getId())) {
                    numberNear52wLow++;
                } else if (sector != null && sector.getId().equals(instrument.getId())) {
                    numberNear52wLow++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to determine number of stocks near 52-week low.", e);
        }

        return numberNear52wLow;
    }

    /**
     * Determines the number of instruments that trade "Up on Volume". That is at least 10% up over a 5-day period with
     * a volume that is at least 25% above the 30-day average. Only those instruments are taken into account where the
     * given Instrument is referenced as sector or industry group.
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The number of instruments trading "Up on Volume".
     */
    private int getNumberUpOnVolume(final Instrument instrument) {
        List<Quotation> allInstrumentsUpOnVolume;
        int numberUpOnVolume = 0;
        Instrument sector;
        Instrument industryGroup;

        try {
            allInstrumentsUpOnVolume = this.quotationDAO.getQuotationsByTemplate(ScanTemplate.UP_ON_VOLUME,
                    InstrumentType.STOCK, null, null, null);

            for (Quotation tempQuotation : allInstrumentsUpOnVolume) {
                sector = tempQuotation.getInstrument().getSector();
                industryGroup = tempQuotation.getInstrument().getIndustryGroup();

                if (industryGroup != null && industryGroup.getId().equals(instrument.getId())) {
                    numberUpOnVolume++;
                } else if (sector != null && sector.getId().equals(instrument.getId())) {
                    numberUpOnVolume++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to determine number of stocks trading Up on Volume.", e);
        }

        return numberUpOnVolume;
    }

    /**
     * Determines the number of instruments that trade "Down on Volume". That is at least -10% up over a 5-day period
     * with a volume that is at least 25% above the 30-day average. Only those instruments are taken into account where
     * the given Instrument is referenced as sector or industry group.
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The number of instruments trading "Down on Volume".
     */
    private int getNumberDownOnVolume(final Instrument instrument) {
        List<Quotation> allInstrumentsDownOnVolume;
        int numberDownOnVolume = 0;
        Instrument sector;
        Instrument industryGroup;

        try {
            allInstrumentsDownOnVolume = this.quotationDAO.getQuotationsByTemplate(ScanTemplate.DOWN_ON_VOLUME,
                    InstrumentType.STOCK, null, null, null);

            for (Quotation tempQuotation : allInstrumentsDownOnVolume) {
                sector = tempQuotation.getInstrument().getSector();
                industryGroup = tempQuotation.getInstrument().getIndustryGroup();

                if (industryGroup != null && industryGroup.getId().equals(instrument.getId())) {
                    numberDownOnVolume++;
                } else if (sector != null && sector.getId().equals(instrument.getId())) {
                    numberDownOnVolume++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to determine number of stocks trading Down on Volume.", e);
        }

        return numberDownOnVolume;
    }

    /**
     * Determines the aggregate indicator.
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The value of the aggregate indicator.
     * @throws Exception Error during data retrieval.
     */
    private int getAggregateIndicator(final Instrument instrument) throws Exception {
        float slowStochasticDaily = this.getSlowStochasticDaily(instrument);
        float slowStochasticWeekly = this.getSlowStochasticWeekly(instrument);
        int percentAboveSma50 = this.getSma10OfPercentAboveSma50(instrument);
        float aggregateIndicator;

        if (slowStochasticDaily == 0 || slowStochasticWeekly == 0) {
            return 0;
        }

        aggregateIndicator = slowStochasticDaily + slowStochasticWeekly;
        aggregateIndicator = aggregateIndicator / 2;

        return Math.round(aggregateIndicator);
    }

    /**
     * Determines the daily Slow Stochastic.
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The daily Slow Stochastic.
     */
    private float getSlowStochasticDaily(final Instrument instrument) {
        QuotationArray quotations = instrument.getQuotationArray();
        StochasticCalculator stochasticCalculator = new StochasticCalculator();
        final int slowStochasticPeriodDays = 14;
        final int smoothingPeriodDays = 3;
        float slowStochasticDaily;

        quotations.sortQuotationsByDate();
        slowStochasticDaily = stochasticCalculator.getSlowStochastic(slowStochasticPeriodDays, smoothingPeriodDays,
                quotations.getQuotations().get(0), quotations);

        return slowStochasticDaily;
    }

    /**
     * Determines the weekly Slow Stochastic.
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The weekly Slow Stochastic.
     */
    private float getSlowStochasticWeekly(final Instrument instrument) {
        QuotationArray quotations = instrument.getQuotationArray();
        QuotationArray weeklyQuotations = new QuotationArray(quotations.getWeeklyQuotations());
        StochasticCalculator stochasticCalculator = new StochasticCalculator();
        final int slowStochasticPeriodWeeks = 14;
        final int smoothingPeriodWeeks = 3;
        float slowStochasticWeekly;

        weeklyQuotations.sortQuotationsByDate();
        slowStochasticWeekly = stochasticCalculator.getSlowStochastic(slowStochasticPeriodWeeks, smoothingPeriodWeeks,
                weeklyQuotations.getQuotations().get(0), weeklyQuotations);

        return slowStochasticWeekly;
    }

    /**
     * Determines the SMA(10) of percentage of instruments above SMA(50).
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The SMA(10) of percentage of instruments above SMA(50).
     * @throws Exception Error during data retrieval.
     */
    private int getSma10OfPercentAboveSma50(final Instrument instrument) throws Exception {
        List<Instrument> instruments;
        List<Statistic> statistics;
        StatisticCalculationController statisticCalculationController = new StatisticCalculationController();

        instruments = this.getInstrumentsOfSectorOrIg(instrument);
        statistics = statisticCalculationController.calculateStatistics(instruments);

        // calculate SMA(10) of the latest instruments above SMA(50) metric.

        return 0;
    }

    /**
     * Determines all instruments that belong to the given sector or industry group.
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return A List of instruments.
     * @throws Exception Error during data retrieval.
     */
    private List<Instrument> getInstrumentsOfSectorOrIg(final Instrument instrument) throws Exception {
        List<Instrument> instruments;
        Iterator<Instrument> iterator;
        Instrument currentInstrument;

        instruments = this.instrumentDAO.getInstruments(InstrumentType.STOCK);

        iterator = instruments.iterator();
        while (iterator.hasNext()) {
            currentInstrument = iterator.next();

            if (instrument.getType() == InstrumentType.IND_GROUP && currentInstrument.getIndustryGroup() == null) {
                iterator.remove();
                continue;
            }

            if (instrument.getType() == InstrumentType.SECTOR && currentInstrument.getSector() == null) {
                iterator.remove();
                continue;
            }

            if (instrument.getType() == InstrumentType.IND_GROUP && currentInstrument.getIndustryGroup() != null
                    && !currentInstrument.getIndustryGroup().getId().equals(instrument.getId())) {
                iterator.remove();
                continue;
            }

            if (instrument.getType() == InstrumentType.SECTOR && currentInstrument.getIndustryGroup() != null
                    && !currentInstrument.getSector().getId().equals(instrument.getId())) {
                iterator.remove();
                continue;
            }
        }

        return instruments;
    }
}
