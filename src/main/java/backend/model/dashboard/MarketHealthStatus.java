package backend.model.dashboard;

import java.util.Date;

/**
 * The MarketHealthStatus contains multiple attributes that provide information about the current health of the given
 * market. A market is typically an Industry Group or sector that contains a list of stocks.
 *
 * @author Michael
 */
public class MarketHealthStatus {
    /**
     * The symbol of the Instrument.
     */
    private String symbol;

    /**
     * The name of the Instrument.
     */
    private String name;

    /**
     * The date on which the MarketHealthStatus is based. This is the date of the most recent Quotation of the given
     * symbol.
     */
    private Date date;

    /**
     * The traffic-light status of the Swingtrading Environment.
     */
    private SwingTradingEnvironmentStatus swingTradingEnvironmentStatus;

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * @return the swingTradingEnvironmentStatus
     */
    public SwingTradingEnvironmentStatus getSwingTradingEnvironmentStatus() {
        return swingTradingEnvironmentStatus;
    }

    /**
     * @param swingTradingEnvironmentStatus the swingTradingEnvironmentStatus to set
     */
    public void setSwingTradingEnvironmentStatus(final SwingTradingEnvironmentStatus swingTradingEnvironmentStatus) {
        this.swingTradingEnvironmentStatus = swingTradingEnvironmentStatus;
    }
}
