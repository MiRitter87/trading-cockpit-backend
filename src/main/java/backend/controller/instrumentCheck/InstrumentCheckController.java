package backend.controller.instrumentCheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import backend.controller.NoQuotationsExistException;
import backend.dao.DAOManager;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.Protocol;
import backend.model.protocol.ProtocolEntry;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks.
 *
 * @author Michael
 */
public class InstrumentCheckController {
    /**
     * DAO to access Quotation data of Instrument.
     */
    private QuotationDAO quotationDAO;

    /**
     * Controller used for counting-related Instrument health checks.
     */
    private InstrumentCheckCountingController instrumentCheckCountingController;

    /**
     * Controller used to detect extreme daily price and volume behavior of an Instrument.
     */
    private InstrumentCheckExtremumController instrumentCheckExtremumController;

    /**
     * Controller used to detect price and volume patterns of an Instrument.
     */
    private InstrumentCheckPatternController instrumentCheckPatternController;

    /**
     * Controller used for moving average-related Instrument health checks.
     */
    private InstrumentCheckAverageController instrumentCheckAverageController;

    /**
     * Controller for climax-related Instrument health checks.
     */
    private InstrumentCheckClimaxController instrumentCheckClimaxController;

    /**
     * Default constructor.
     */
    public InstrumentCheckController() {
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();

        this.instrumentCheckCountingController = new InstrumentCheckCountingController();
        this.instrumentCheckExtremumController = new InstrumentCheckExtremumController();
        this.instrumentCheckPatternController = new InstrumentCheckPatternController();
        this.instrumentCheckAverageController = new InstrumentCheckAverageController();
        this.instrumentCheckClimaxController = new InstrumentCheckClimaxController();
    }

    /**
     * Checks the health of the given Instrument beginning at the given start date.
     *
     * @param instrumentId The id of the Instrument.
     * @param startDate    The start date of the health check.
     * @return A protocol containing the health information from the start date until the most recent quotation.
     * @throws NoQuotationsExistException Exception indicating no Quotations exist at and after given start date.
     * @throws Exception                  Health check failed.
     */
    public Protocol checkInstrument(final Integer instrumentId, final Date startDate)
            throws NoQuotationsExistException, Exception {
        QuotationArray quotations = new QuotationArray(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
        Protocol protocol = new Protocol();

        quotations.sortQuotationsByDate();
        this.checkQuotationsExistAfterStartDate(startDate, quotations);

        this.checkConfirmations(startDate, quotations, protocol);
        this.checkSellingIntoWeakness(startDate, quotations, protocol);
        this.checkSellingIntoStrength(startDate, quotations, protocol);

        protocol.sortEntriesByDate();
        protocol.calculatePercentages();

        return protocol;
    }

    /**
     * Checks the health of the given Instrument beginning at the given start date. The executed health check methods
     * depend on the given HealthCheckProfile.
     *
     * @param instrumentId The id of the Instrument.
     * @param startDate    The start date of the health check.
     * @param profile      The HealthCheckProfile that is used.
     * @return A protocol containing the health information from the start date until the most recent quotation.
     * @throws NoQuotationsExistException Exception indicating no Quotations exist at and after given start date.
     * @throws Exception                  Health check failed.
     */
    public Protocol checkInstrumentWithProfile(final Integer instrumentId, final Date startDate,
            final HealthCheckProfile profile) throws NoQuotationsExistException, Exception {

        QuotationArray quotations = new QuotationArray(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
        Protocol protocol = new Protocol();

        quotations.sortQuotationsByDate();
        this.checkQuotationsExistAfterStartDate(startDate, quotations);

        switch (profile) {
        case CONFIRMATIONS:
            this.checkConfirmations(startDate, quotations, protocol);
            break;
        case SELLING_INTO_STRENGTH:
            this.checkSellingIntoStrength(startDate, quotations, protocol);
            break;
        case SELLING_INTO_WEAKNESS:
            this.checkSellingIntoWeakness(startDate, quotations, protocol);
            break;
        default:
            break;
        }

        protocol.sortEntriesByDate();

        return protocol;
    }

    /**
     * Checks for price and volume action that confirms an up-trend.
     *
     * @param startDate  The start date of the health check.
     * @param quotations The quotations that build the trading history of an Instrument.
     * @param protocol   The Protocol to which possible events are added.
     * @throws Exception Health check failed.
     */
    private void checkConfirmations(final Date startDate, final QuotationArray quotations, final Protocol protocol)
            throws Exception {

        List<ProtocolEntry> confirmations = new ArrayList<>();

        confirmations.addAll(this.instrumentCheckCountingController.checkMoreUpThanDownDays(startDate, quotations));
        confirmations.addAll(this.instrumentCheckCountingController.checkMoreGoodThanBadCloses(startDate, quotations));
        confirmations.addAll(this.instrumentCheckPatternController.checkUpOnVolume(startDate, quotations));

        this.setProfile(confirmations, HealthCheckProfile.CONFIRMATIONS);
        protocol.getProtocolEntries().addAll(confirmations);
    }

    /**
     * Checks for price and volume action that advises to sell into strength.
     *
     * @param startDate  The start date of the health check.
     * @param quotations The quotations that build the trading history of an Instrument.
     * @param protocol   The Protocol to which possible events are added.
     * @throws Exception Health check failed.
     */
    private void checkSellingIntoStrength(final Date startDate, final QuotationArray quotations,
            final Protocol protocol) throws Exception {

        List<ProtocolEntry> sellingIntoStrength = new ArrayList<>();

        sellingIntoStrength.addAll(this.instrumentCheckExtremumController.checkLargestUpDay(startDate, quotations));
        sellingIntoStrength
                .addAll(this.instrumentCheckExtremumController.checkLargestDailySpread(startDate, quotations));
        sellingIntoStrength
                .addAll(this.instrumentCheckExtremumController.checkLargestDailyVolume(startDate, quotations));
        sellingIntoStrength.addAll(this.instrumentCheckPatternController.checkChurning(startDate, quotations));
        sellingIntoStrength.addAll(this.instrumentCheckClimaxController.checkTimeClimax(startDate, quotations));
        sellingIntoStrength.addAll(this.instrumentCheckClimaxController.checkClimaxMoveOneWeek(startDate, quotations));
        sellingIntoStrength
                .addAll(this.instrumentCheckClimaxController.checkClimaxMoveThreeWeeks(startDate, quotations));
        sellingIntoStrength
                .addAll(this.instrumentCheckAverageController.checkExtendedAboveSma200(startDate, quotations));
        sellingIntoStrength.addAll(this.instrumentCheckPatternController.checkGapUp(startDate, quotations));

        this.setProfile(sellingIntoStrength, HealthCheckProfile.SELLING_INTO_STRENGTH);
        protocol.getProtocolEntries().addAll(sellingIntoStrength);
    }

    /**
     * Checks for price and volume action that advises to sell into weakness.
     *
     * @param startDate  The start date of the health check.
     * @param quotations The quotations that build the trading history of an Instrument.
     * @param protocol   The Protocol to which possible events are added.
     * @throws Exception Health check failed.
     */
    private void checkSellingIntoWeakness(final Date startDate, final QuotationArray quotations,
            final Protocol protocol) throws Exception {

        List<ProtocolEntry> sellingIntoWeakness = new ArrayList<>();

        sellingIntoWeakness.addAll(this.instrumentCheckAverageController.checkCloseBelowSma50(startDate, quotations));
        sellingIntoWeakness.addAll(this.instrumentCheckAverageController.checkCloseBelowEma21(startDate, quotations));
        sellingIntoWeakness.addAll(this.instrumentCheckExtremumController.checkLargestDownDay(startDate, quotations));
        sellingIntoWeakness
                .addAll(this.instrumentCheckCountingController.checkMoreDownThanUpDays(startDate, quotations));
        sellingIntoWeakness
                .addAll(this.instrumentCheckCountingController.checkMoreBadThanGoodCloses(startDate, quotations));
        sellingIntoWeakness.addAll(this.instrumentCheckPatternController.checkDownOnVolume(startDate, quotations));
        sellingIntoWeakness
                .addAll(this.instrumentCheckPatternController.checkHighVolumeReversal(startDate, quotations));
        sellingIntoWeakness.addAll(this.instrumentCheckCountingController.checkThreeLowerCloses(startDate, quotations));

        this.setProfile(sellingIntoWeakness, HealthCheckProfile.SELLING_INTO_WEAKNESS);
        protocol.getProtocolEntries().addAll(sellingIntoWeakness);
    }

    /**
     * Checks if quotations exist at and after the given start date.
     *
     * @param startDate  The start date.
     * @param quotations A list of quotations.
     * @throws NoQuotationsExistException Exception indicating no Quotations exist at and after given start date.
     */
    public void checkQuotationsExistAfterStartDate(final Date startDate, final QuotationArray quotations)
            throws NoQuotationsExistException {
        int indexOfQuotationWithDate = quotations.getIndexOfQuotationWithDate(startDate);

        if (indexOfQuotationWithDate == -1) {
            throw new NoQuotationsExistException(startDate);
        }
    }

    /**
     * Determines the start date for Instrument health checks.
     *
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @param quotations     The list of quotations.
     * @return The start date.
     */
    public Date getStartDate(final Integer lookbackPeriod, final QuotationArray quotations) {
        Date date;
        Quotation quotation;
        int sizeOfQuotations = quotations.getQuotations().size();

        if (sizeOfQuotations == 0) {
            return null;
        }

        quotations.sortQuotationsByDate();

        if (sizeOfQuotations < lookbackPeriod) {
            quotation = quotations.getQuotations().get(sizeOfQuotations - 1);
        } else {
            quotation = quotations.getQuotations().get(lookbackPeriod - 1);
        }

        date = DateTools.getDateWithoutIntradayAttributes(quotation.getDate());

        return date;
    }

    /**
     * Sets the given HealthCheckProfile in all protocol entries.
     *
     * @param protocolEntries A List of protocol entries.
     * @param profile         The HealthCheckProfile to be set in each ProtocolEntry.
     */
    private void setProfile(final List<ProtocolEntry> protocolEntries, final HealthCheckProfile profile) {
        for (ProtocolEntry protocolEntry : protocolEntries) {
            protocolEntry.setProfile(profile);
        }
    }
}
