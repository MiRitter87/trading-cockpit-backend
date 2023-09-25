package backend.dao.quotation.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import backend.controller.scan.IndicatorCalculator;
import backend.controller.scan.PerformanceCalculator;
import backend.model.LocalizedException;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.tools.DateTools;
import backend.webservice.ScanTemplate;

/**
 * Performs processing tasks during the determination of scan results based on predefined scan templates.
 * <p>
 *
 * These tasks include:<br>
 * -Post processing activity performed on scan results<br>
 * -Complex scan algorithms that go beyond the scope of a simple database selection based on indicator values<br>
 * -Apply filter criteria to the template-based scan results
 *
 * @author Michael
 */
public class ScanTemplateProcessor {
    /**
     * DAO to access Quotation data.
     */
    private QuotationHibernateDAO quotationHibernateDAO;

    /**
     * Performs manual tasks for the various "RS_NEAR_HIGH" Scan templates.
     */
    private TemplateRsNearHighProcessor rsNearHighProcessor;

    /**
     * Performs manual tasks for the "SWING_TRADING_ENVIRONMENT" ScanTemplate.
     */
    private TemplateSwingTradingProcessor swingTradingProcessor;

    /**
     * Performs manual tasks for the "HIGH_TIGHT_FLAG" ScanTemplate.
     */
    private TemplateHighTightFlagProcessor highTightFlagProcessor;

    /**
     * Performs manual tasks fot the "THREE_WEEKS_TIGHT" ScanTemplate.
     */
    private TemplateThreeWeeksTightProcessor threeWeeksTightProcessor;

    /**
     * Constructor.
     *
     * @param quotationHibernateDAO DAO to access Quotation data.
     */
    public ScanTemplateProcessor(final QuotationHibernateDAO quotationHibernateDAO) {
        this.quotationHibernateDAO = quotationHibernateDAO;
        this.rsNearHighProcessor = new TemplateRsNearHighProcessor(this.quotationHibernateDAO);
        this.swingTradingProcessor = new TemplateSwingTradingProcessor(this.quotationHibernateDAO);
        this.highTightFlagProcessor = new TemplateHighTightFlagProcessor(this.quotationHibernateDAO);
        this.threeWeeksTightProcessor = new TemplateThreeWeeksTightProcessor(this.quotationHibernateDAO);
    }

    /**
     * Fills transient attributes of the given quotations.
     *
     * @param instrumentType The InstrumentType of the given quotations.
     * @param quotations     The quotations with their referenced data whose transient attributes are to be filled.
     * @throws Exception In case an error occurred during data determination.
     */
    protected void fillTransientAttributes(final InstrumentType instrumentType, final List<Quotation> quotations)
            throws Exception {
        QuotationArray sectorQuotations = new QuotationArray();
        QuotationArray industryGroupQuotations = new QuotationArray();
        List<Quotation> quotationsOfInstrument;
        Quotation sectorQuotation;
        Quotation industryGroupQuotation;

        if (quotations.size() == 0 || instrumentType != InstrumentType.STOCK) {
            return;
        }

        sectorQuotations.setQuotations(this.quotationHibernateDAO.getRecentQuotations(InstrumentType.SECTOR));
        industryGroupQuotations.setQuotations(this.quotationHibernateDAO.getRecentQuotations(InstrumentType.IND_GROUP));

        // Determine and set the sector and industry group RS number for each quotation.
        for (Quotation quotation : quotations) {
            if (quotation.getInstrument().getSector() != null) {
                quotationsOfInstrument = sectorQuotations
                        .getQuotationsByInstrumentId(quotation.getInstrument().getSector().getId());

                if (quotationsOfInstrument.size() == 1) {
                    sectorQuotation = quotationsOfInstrument.get(0);
                } else {
                    sectorQuotation = null;
                }

                if (sectorQuotation != null && this.areQuotationsOfSameDay(quotation, sectorQuotation)) {
                    quotation.getIndicator().setRsNumberSector(sectorQuotation.getIndicator().getRsNumber());
                }
            }

            if (quotation.getInstrument().getIndustryGroup() != null) {
                quotationsOfInstrument = industryGroupQuotations
                        .getQuotationsByInstrumentId(quotation.getInstrument().getIndustryGroup().getId());

                if (quotationsOfInstrument.size() == 1) {
                    industryGroupQuotation = quotationsOfInstrument.get(0);
                } else {
                    industryGroupQuotation = null;
                }

                if (industryGroupQuotation != null && this.areQuotationsOfSameDay(quotation, industryGroupQuotation)) {
                    quotation.getIndicator()
                            .setRsNumberIndustryGroup(industryGroupQuotation.getIndicator().getRsNumber());
                }
            }
        }
    }

    /**
     * Performs post processing tasks on the given quotations based on the scan template.
     *
     * @param scanTemplate      The scan template.
     * @param startDateAsString The start date for calculation of the RS number.
     * @param quotations        The quotations on which the post processing is performed.
     * @throws LocalizedException Exception with error message to be displayed to the user.
     * @throws Exception          Post processing failed.
     */
    protected void templateBasedPostProcessing(final ScanTemplate scanTemplate, final String startDateAsString,
            final List<Quotation> quotations) throws LocalizedException, Exception {

        if (scanTemplate == ScanTemplate.RS_SINCE_DATE) {
            this.postProcessingRsSinceDate(startDateAsString, quotations);
        } else if (scanTemplate == ScanTemplate.THREE_WEEKS_TIGHT) {
            this.threeWeeksTightProcessor.postProcessingThreeWeeksTight(quotations);
        } else if (scanTemplate == ScanTemplate.HIGH_TIGHT_FLAG) {
            this.highTightFlagProcessor.postProcessingHighTightFlag(quotations);
        } else if (scanTemplate == ScanTemplate.SWING_TRADING_ENVIRONMENT) {
            this.swingTradingProcessor.postProcessingSwingTradingEnvironment(quotations);
        } else if (scanTemplate == ScanTemplate.RS_NEAR_HIGH_IG) {
            this.rsNearHighProcessor.postProcessingRsNearHigh(scanTemplate, quotations);
        }
    }

    /**
     * Applies the given filters to the given quotations. If a Quotation does not fulfill the given filters, it is
     * removed from the List.
     *
     * @param minLiquidity The minimum trading liquidity that is required. Parameter can be omitted (null).
     * @param quotations   The quotations on which the filtering is performed.
     */
    protected void applyFilters(final Float minLiquidity, final List<Quotation> quotations) {
        this.filterByMinLiquidity(minLiquidity, quotations);
    }

    /**
     * Performs post processing tasks for the given quotations based on the ScanTemplate "RS_SINCE_DATE". The method
     * calculates and sets the RS number beginning from the given date.
     *
     * @param startDateAsString The start date for calculation of the RS number.
     * @param quotations        The quotations on which the post processing is performed.
     * @throws Exception Post processing failed.
     */
    private void postProcessingRsSinceDate(final String startDateAsString, final List<Quotation> quotations)
            throws Exception {

        IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
        PerformanceCalculator performanceCalculator = new PerformanceCalculator();
        Date startDate = DateTools.convertStringToDate(startDateAsString);
        QuotationArray quotationsOfInstrument = new QuotationArray();
        Quotation quotationOfDate;
        int quotationOfDateIndex;
        float rsPercent;

        // Calculate the price performance from the start date to the current date.
        for (Quotation currentQuotation : quotations) {
            quotationsOfInstrument.setQuotations(
                    this.quotationHibernateDAO.getQuotationsOfInstrument(currentQuotation.getInstrument().getId()));
            quotationOfDateIndex = quotationsOfInstrument.getIndexOfQuotationWithDate(startDate);
            quotationOfDate = quotationsOfInstrument.getQuotations().get(quotationOfDateIndex);

            rsPercent = performanceCalculator.getPerformance(currentQuotation, quotationOfDate);
            currentQuotation.getIndicator().setRsPercentSum(rsPercent);
        }

        // Calculate the RS numbers based on the newly calculated performance.
        indicatorCalculator.calculateRsNumbers(quotations);
    }

    /**
     * Filters the given quotations by the minimum liquidity.
     *
     * @param minLiquidity The minimum trading liquidity that is required. Parameter can be omitted (null).
     * @param quotations   The quotations on which the filtering is performed.
     */
    private void filterByMinLiquidity(final Float minLiquidity, final List<Quotation> quotations) {
        Iterator<Quotation> quotationIterator = quotations.iterator();
        Quotation currentQuotation;

        if (minLiquidity == null) {
            return;
        }

        while (quotationIterator.hasNext()) {
            currentQuotation = quotationIterator.next();

            if (currentQuotation.getIndicator().getLiquidity20Days() < minLiquidity) {
                quotationIterator.remove();
            }
        }
    }

    /**
     * Checks if both quotations are of the same day.
     *
     * @param quotation1 The first Quotation.
     * @param quotation2 The second Quotation.
     * @return true, if both quotations are of the same day.
     */
    private boolean areQuotationsOfSameDay(final Quotation quotation1, final Quotation quotation2) {
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.setTimeInMillis(quotation1.getDate().getTime());
        date2.setTimeInMillis(quotation2.getDate().getTime());

        if (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
                && date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
                && date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH)) {

            return true;
        }

        return false;
    }
}
