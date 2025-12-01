package backend.dao.quotation.persistence;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import backend.model.LocalizedException;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.instrument.RelativeStrengthData;
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
     * Performs manual tasks for the "THREE_WEEKS_TIGHT" ScanTemplate.
     */
    private TemplateThreeWeeksTightProcessor threeWeeksTightProcessor;

    /**
     * Performs manual tasks for the "RS_SINCE_DATE" ScanTemplate.
     */
    private TemplateRsSinceDateProcessor rsSinceDateProcessor;

    /**
     * Performs manual tasks for the "BUYABLE_BASE" ScanTemplate.
     */
    private TemplateBuyableBaseProcessor buyableBaseProcessor;

    /**
     * Constructor.
     *
     * @param quotationHibernateDAO DAO to access Quotation data.
     */
    public ScanTemplateProcessor(final QuotationHibernateDAO quotationHibernateDAO) {
        this.quotationHibernateDAO = quotationHibernateDAO;
        this.rsNearHighProcessor = new TemplateRsNearHighProcessor(quotationHibernateDAO);
        this.swingTradingProcessor = new TemplateSwingTradingProcessor(quotationHibernateDAO);
        this.highTightFlagProcessor = new TemplateHighTightFlagProcessor(quotationHibernateDAO);
        this.threeWeeksTightProcessor = new TemplateThreeWeeksTightProcessor(quotationHibernateDAO);
        this.rsSinceDateProcessor = new TemplateRsSinceDateProcessor(quotationHibernateDAO);
        this.buyableBaseProcessor = new TemplateBuyableBaseProcessor();
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

        this.fillSectorRsNumber(instrumentType, quotations);
        this.fillIndustryGroupRsNumber(instrumentType, quotations);
        this.fillCompositeRsNumberIg(instrumentType, quotations);
    }

    /**
     * Fills the transient attribute: Sector RS number.
     *
     * @param instrumentType The InstrumentType of the given quotations.
     * @param quotations     The quotations with their referenced data whose transient attributes are to be filled.
     * @throws Exception In case an error occurred during data determination.
     */
    private void fillSectorRsNumber(final InstrumentType instrumentType, final List<Quotation> quotations)
            throws Exception {
        QuotationArray sectorQuotations = new QuotationArray();
        Quotation sectorQuotation;

        if (quotations.size() == 0 || instrumentType != InstrumentType.STOCK) {
            return;
        }

        sectorQuotations.setQuotations(this.quotationHibernateDAO.getRecentQuotations(InstrumentType.SECTOR));

        // Determine and set the sector RS number for each quotation.
        for (Quotation quotation : quotations) {
            if (quotation.getInstrument().getSector() != null) {
                sectorQuotation = this.getQuotation(quotation.getInstrument().getSector().getId(), sectorQuotations);

                if (sectorQuotation != null && this.areQuotationsOfSameDay(quotation, sectorQuotation)) {
                    quotation.getRelativeStrengthData()
                            .setRsNumberSector(sectorQuotation.getRelativeStrengthData().getRsNumber());
                }
            }
        }
    }

    /**
     * Fills the transient attribute: Industry Group RS number.
     *
     * @param instrumentType The InstrumentType of the given quotations.
     * @param quotations     The quotations with their referenced data whose transient attributes are to be filled.
     * @throws Exception In case an error occurred during data determination.
     */
    private void fillIndustryGroupRsNumber(final InstrumentType instrumentType, final List<Quotation> quotations)
            throws Exception {
        QuotationArray industryGroupQuotations = new QuotationArray();
        Quotation industryGroupQuotation;

        if (quotations.size() == 0 || instrumentType != InstrumentType.STOCK) {
            return;
        }

        industryGroupQuotations.setQuotations(this.quotationHibernateDAO.getRecentQuotations(InstrumentType.IND_GROUP));

        // Determine and set the industry group RS number for each quotation.
        for (Quotation quotation : quotations) {
            if (quotation.getInstrument().getIndustryGroup() != null) {
                industryGroupQuotation = this.getQuotation(quotation.getInstrument().getIndustryGroup().getId(),
                        industryGroupQuotations);

                if (industryGroupQuotation != null && this.areQuotationsOfSameDay(quotation, industryGroupQuotation)
                        && quotation.getRelativeStrengthData() != null) {
                    quotation.getRelativeStrengthData()
                            .setRsNumberIndustryGroup(industryGroupQuotation.getRelativeStrengthData().getRsNumber());
                }
            }
        }
    }

    /**
     * Fills the transient attribute: Composite RS number of Instrument and its industry group.
     *
     * @param instrumentType The InstrumentType of the given quotations.
     * @param quotations     The quotations with their referenced data whose transient attributes are to be filled.
     * @throws Exception In case an error occurred during data determination.
     */
    private void fillCompositeRsNumberIg(final InstrumentType instrumentType, final List<Quotation> quotations)
            throws Exception {

        QuotationArray industryGroupQuotations = new QuotationArray();
        Quotation industryGroupQuotation;
        RelativeStrengthData quotationRsData;
        int rsNumberSum;
        int compositeRsNumber;
        final int fiveComponents = 5;

        if (quotations.size() == 0 || instrumentType != InstrumentType.STOCK) {
            return;
        }

        industryGroupQuotations.setQuotations(this.quotationHibernateDAO.getRecentQuotations(InstrumentType.IND_GROUP));

        // Determine and set the composite RS number for each quotation.
        for (Quotation quotation : quotations) {
            quotationRsData = quotation.getRelativeStrengthData();

            if (quotationRsData == null) {
                continue;
            }

            if (quotation.getInstrument().getIndustryGroup() != null) {
                industryGroupQuotation = this.getQuotation(quotation.getInstrument().getIndustryGroup().getId(),
                        industryGroupQuotations);

                if (industryGroupQuotation != null && this.areQuotationsOfSameDay(quotation, industryGroupQuotation)) {
                    rsNumberSum = quotationRsData.getRsNumber() * 2;
                    rsNumberSum += industryGroupQuotation.getRelativeStrengthData().getRsNumber();
                    rsNumberSum += quotationRsData.getRsNumberDistance52WeekHigh();
                    rsNumberSum += quotationRsData.getRsNumberUpDownVolumeRatio();
                    compositeRsNumber = (int) Math.ceil((double) rsNumberSum / fiveComponents);
                    quotationRsData.setRsNumberCompositeIg(compositeRsNumber);
                }
            }
        }
    }

    /**
     * Tries to determine a single Quotation from the given QuotationArray that has the given Instrument ID.
     *
     * @param instrumentId The ID of the Instrument whose Quotation is requested.
     * @param quotations   The QuotationArray.
     * @return The Quotation with the given ID, if a distinct entry is found; null otherwise.
     */
    private Quotation getQuotation(final int instrumentId, final QuotationArray quotations) {
        List<Quotation> quotationsOfInstrument;
        Quotation quotation;

        quotationsOfInstrument = quotations.getQuotationsByInstrumentId(instrumentId);

        if (quotationsOfInstrument.size() == 1) {
            quotation = quotationsOfInstrument.get(0);
        } else {
            quotation = null;
        }

        return quotation;
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
            this.rsSinceDateProcessor.postProcessingRsSinceDate(startDateAsString, quotations);
        } else if (scanTemplate == ScanTemplate.THREE_WEEKS_TIGHT) {
            this.threeWeeksTightProcessor.postProcessingThreeWeeksTight(quotations);
        } else if (scanTemplate == ScanTemplate.HIGH_TIGHT_FLAG) {
            this.highTightFlagProcessor.postProcessingHighTightFlag(quotations);
        } else if (scanTemplate == ScanTemplate.SWING_TRADING_ENVIRONMENT) {
            this.swingTradingProcessor.postProcessingSwingTradingEnvironment(quotations);
        } else if (scanTemplate == ScanTemplate.RS_NEAR_HIGH_IG) {
            this.rsNearHighProcessor.postProcessingRsNearHigh(scanTemplate, quotations);
        } else if (scanTemplate == ScanTemplate.BUYABLE_BASE) {
            this.buyableBaseProcessor.postProcessingBuyableBase(quotations);
        }
    }

    /**
     * Sets the dataSourceList attribute of the quotations instruments to null. Those are loaded lazily and not required
     * by the frontend.
     *
     * @param quotations The quotations on which the post processing is performed.
     */
    protected void setDataSourceListNull(final List<Quotation> quotations) {
        for (Quotation quotation : quotations) {
            quotation.getInstrument().setDataSourceList(null);

            if (quotation.getInstrument().getSector() != null) {
                quotation.getInstrument().getSector().setDataSourceList(null);
            }

            if (quotation.getInstrument().getIndustryGroup() != null) {
                quotation.getInstrument().getIndustryGroup().setDataSourceList(null);
            }
        }
    }

    /**
     * Applies the given filters to the given quotations. If a Quotation does not fulfill the given filters, it is
     * removed from the List.
     *
     * @param minLiquidity The minimum trading liquidity that is required. Parameter can be omitted (null).
     * @param minAtrp      The minimum Average True Range Percent that is required. Parameter can be omitted (null).
     * @param quotations   The quotations on which the filtering is performed.
     */
    protected void applyFilters(final Float minLiquidity, final Float minAtrp, final List<Quotation> quotations) {
        this.filterByMinLiquidity(minLiquidity, quotations);
        this.filterByMinAverageTrueRangePercent(minAtrp, quotations);
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
     * Filters the given quotations by the minimum Average True Range Percent.
     *
     * @param minAtrp    The minimum Average True Range Percent that is required. Parameter can be omitted (null).
     * @param quotations The quotations on which the filtering is performed.
     */
    private void filterByMinAverageTrueRangePercent(final Float minAtrp, final List<Quotation> quotations) {
        Iterator<Quotation> quotationIterator = quotations.iterator();
        Quotation currentQuotation;

        if (minAtrp == null) {
            return;
        }

        while (quotationIterator.hasNext()) {
            currentQuotation = quotationIterator.next();

            if (currentQuotation.getIndicator().getAverageTrueRangePercent20() < minAtrp) {
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
