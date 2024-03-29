package backend.model.instrument;

/**
 * The type of an Instrument.
 *
 * @author Michael
 */
public enum InstrumentType {
    /**
     * A stock.
     */
    STOCK,

    /**
     * An ETF.
     */
    ETF,

    /**
     * A sector.
     */
    SECTOR,

    /**
     * An industry group.
     */
    IND_GROUP,

    /**
     * A ratio in price between two instruments.
     */
    RATIO
}
