package backend.controller.instrumentCheck;

import java.util.Date;

import backend.controller.NoQuotationsExistException;
import backend.dao.DAOManager;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.Protocol;

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

        // Confirmations
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckCountingController.checkMoreUpThanDownDays(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckCountingController.checkMoreGoodThanBadCloses(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckPatternController.checkUpOnVolume(startDate, quotations));

        // Violations
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckAverageController.checkCloseBelowSma50(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckAverageController.checkCloseBelowEma21(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckExtremumController.checkLargestDownDay(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckCountingController.checkMoreDownThanUpDays(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckCountingController.checkMoreBadThanGoodCloses(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckPatternController.checkDownOnVolume(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckPatternController.checkHighVolumeReversal(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckCountingController.checkThreeLowerCloses(startDate, quotations));

        // Uncertain
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckExtremumController.checkLargestUpDay(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckExtremumController.checkLargestDailySpread(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckExtremumController.checkLargestDailyVolume(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckPatternController.checkChurning(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckCountingController.checkTimeClimax(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckClimaxController.checkClimaxMoveOneWeek(startDate, quotations));
        protocol.getProtocolEntries()
                .addAll(this.instrumentCheckClimaxController.checkClimaxMoveThreeWeeks(startDate, quotations));

        protocol.sortEntriesByDate();
        protocol.calculatePercentages();

        return protocol;
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
}
