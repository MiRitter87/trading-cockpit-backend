package backend.model.chart;

import java.util.Map;

import backend.model.instrument.QuotationArray;
import backend.model.protocol.DateBasedProtocolArray;

/**
 * A collection of data that are used to construct a price/volume chart with Instrument health check information.
 *
 * @author Michael
 */
public class HealthCheckChartData {
    /**
     * The quotations.
     */
    private QuotationArray quotations;

    /**
     * The health check protocol.
     */
    private DateBasedProtocolArray protocol;

    /**
     * A Map of dates with a sum of positive and negative health events for each date.
     */
    private Map<Long, Integer> healthEvents;

    /**
     * @return the quotations
     */
    public QuotationArray getQuotations() {
        return quotations;
    }

    /**
     * @param quotations the quotations to set
     */
    public void setQuotations(final QuotationArray quotations) {
        this.quotations = quotations;
    }

    /**
     * @return the protocol
     */
    public DateBasedProtocolArray getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(final DateBasedProtocolArray protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the healthEvents
     */
    public Map<Long, Integer> getHealthEvents() {
        return healthEvents;
    }

    /**
     * @param healthEvents the healthEvents to set
     */
    public void setHealthEvents(final Map<Long, Integer> healthEvents) {
        this.healthEvents = healthEvents;
    }
}
