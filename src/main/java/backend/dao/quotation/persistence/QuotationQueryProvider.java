package backend.dao.quotation.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;

/**
 * Provides Quotation queries.
 *
 * @author Michael
 */
public class QuotationQueryProvider {
    /**
     * Interface used to interact with the persistence context.
     */
    private EntityManager entityManager;

    /**
     * Initializes the QuotationQueryProvider.
     *
     * @param entityManager The EntityManager to be used for queries.
     */
    public QuotationQueryProvider(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Provides the Query for the "Minervini Trend Template".
     *
     * @return The Query.
     */
    public Query getQueryForMinerviniTrendTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL " + "AND q.close > r.sma50 "
                        + "AND r.sma50 > r.sma150 " + "AND r.sma150 > r.sma200 " + "AND r.distanceTo52WeekLow >= 30 "
                        + "AND r.distanceTo52WeekHigh >= -25 " + "AND r.rsNumber >= 70");
    }

    /**
     * Provides the Query for the "Breakout Candidates" Template.
     *
     * @return The Query.
     */
    public Query getQueryForBreakoutCandidatesTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL "
                        + "AND r.volumeDifferential10Days < 0" + "AND r.bollingerBandWidth < 10 "
                        + "AND r.baseLengthWeeks >= 3" + "AND r.distanceTo52WeekHigh >= -10");
    }

    /**
     * Provides the Query for the "Volatility Contraction" Template.
     *
     * @return The Query.
     */
    public Query getQueryForVolatilityContractionTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL "
                        + "AND r.volumeDifferential10Days < 0" + "AND r.bollingerBandWidth < 10");
    }

    /**
     * Provides the Query for the "Up on Volume" Template.
     *
     * @return The Query.
     */
    public Query getQueryForUpOnVolumeTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL "
                        + "AND r.volumeDifferential5Days >= 25" + "AND r.performance5Days >= 10");
    }

    /**
     * Provides the Query for the "Down on Volume" Template.
     *
     * @return The Query.
     */
    public Query getQueryForDownOnVolumeTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL "
                        + "AND r.volumeDifferential5Days >= 25" + "AND r.performance5Days <= -10");
    }

    /**
     * Provides the Query for the "Near 52-week High" Template.
     *
     * @return The Query.
     */
    public Query getQueryForNear52WeekHighTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL "
                        + "AND r.distanceTo52WeekHigh >= -5 ");
    }

    /**
     * Provides the Query for the "Near 52-week Low" Template.
     *
     * @return The Query.
     */
    public Query getQueryForNear52WeekLowTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL "
                        + "AND r.distanceTo52WeekLow <= 5 ");
    }

    /**
     * Provides the Query for the "High Tight Flag" Template.
     *
     * @return The Query.
     */
    public Query getQueryForHighTightFlagTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL "
                        + "AND r.distanceTo52WeekHigh >= -25 ");
    }

    /**
     * Provides the Query for the "Swing Trading Environment" Template.
     *
     * @return The Query.
     */
    public Query getQueryForSwingTradingEnvironmentTemplate() {
        return this.entityManager
                .createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
                        + "quotation_id IN :quotationIds " + "AND q.indicator IS NOT NULL " + "AND q.close > r.sma20 "
                        + "AND r.sma10 > r.sma20 ");
    }

    /**
     * Provides a Query that determines all quotations with their referenced Instrument (and Indicator) based on the
     * given Quotation IDs.
     *
     * @param withIndicatorNotNull Only those quotations are fetched that have Indicator data referenced, if set to
     *                             true.
     * @return The Query.
     */
    public Query getQueryForQuotationsWithInstrument(final boolean withIndicatorNotNull) {
        Query query;

        if (withIndicatorNotNull) {
            query = this.entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i "
                    + "WHERE quotation_id IN :quotationIds AND q.indicator IS NOT NULL");
        } else {
            query = this.entityManager.createQuery(
                    "SELECT q FROM Quotation q JOIN FETCH q.instrument i WHERE quotation_id IN :quotationIds");
        }

        return query;
    }

    /**
     * Provides a native Query that determines the IDs of the quotations with the newest date for each Instrument. Only
     * those instruments are taken into account that match the given InstrumentType.
     *
     * @param instrumentType The InstrumentType.
     * @return The Query.
     */
    @SuppressWarnings("unchecked")
    public Query getQueryForQuotationIdsWithMaxDate(final InstrumentType instrumentType) {
        List<Object> instrumentIdsOfGivenInstrumentType;
        Query query;

        // Get the IDs of all instruments of the given type.
        query = this.entityManager.createQuery("SELECT id FROM Instrument i WHERE i.type = :instrumentType");
        query.setParameter("instrumentType", instrumentType);
        instrumentIdsOfGivenInstrumentType = query.getResultList();

        // Get the IDs of all Quotations with the newest date for each Instrument.
        query = this.entityManager.createNativeQuery("SELECT quotation_id FROM Quotation q INNER JOIN "
                + "(SELECT max(date) AS maxdate, instrument_id FROM Quotation GROUP BY instrument_id) jointable "
                + "ON q.instrument_id = jointable.instrument_id AND q.date = jointable.maxdate "
                + "WHERE q.instrument_id IN :instrumentIds");
        query.setParameter("instrumentIds", instrumentIdsOfGivenInstrumentType);

        return query;
    }

    /**
     * Provides a native Query that determines the IDs of the quotations with the newest date for each Instrument of the
     * given List.
     *
     * @param list The List defining all instruments for which the quotations have to be determined.
     * @return The Query.
     */
    public Query getQueryForQuotationIdsWithMaxDateForList(final backend.model.list.List list) {
        List<Object> instrumentIds = new ArrayList<>();
        Iterator<Instrument> instrumentIterator;
        Instrument instrument;
        Query query;

        instrumentIterator = list.getInstruments().iterator();

        // Get the IDs of all instruments of the given List.
        while (instrumentIterator.hasNext()) {
            instrument = instrumentIterator.next();
            instrumentIds.add(instrument.getId());
        }

        // Prepare Query for most recent Quotation of each Instrument.
        query = this.entityManager.createNativeQuery("SELECT quotation_id FROM Quotation q INNER JOIN "
                + "(SELECT max(date) AS maxdate, instrument_id FROM Quotation GROUP BY instrument_id) jointable "
                + "ON q.instrument_id = jointable.instrument_id AND q.date = jointable.maxdate "
                + "AND jointable.instrument_id IN :instrumentIds");

        query.setParameter("instrumentIds", instrumentIds);

        return query;
    }
}
