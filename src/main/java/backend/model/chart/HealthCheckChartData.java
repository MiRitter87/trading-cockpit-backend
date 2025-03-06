package backend.model.chart;

import java.util.List;

import backend.model.instrument.Quotation;
import backend.model.protocol.Protocol;

/**
 * A collection of data that are used to construct a price/volume chart with Instrument health check information.
 *
 * @author Michael
 */
public class HealthCheckChartData {
    /**
     * The quotations.
     */
    private List<Quotation> quotations;

    /**
     * The health check protocol.
     */
    private Protocol protocol;

    /**
     * @return the quotations
     */
    public List<Quotation> getQuotations() {
        return quotations;
    }

    /**
     * @param quotations the quotations to set
     */
    public void setQuotations(final List<Quotation> quotations) {
        this.quotations = quotations;
    }

    /**
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(final Protocol protocol) {
        this.protocol = protocol;
    }

}
