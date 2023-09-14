package backend.webservice.common;

import java.math.BigDecimal;
import java.util.Date;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.PriceAlertWS;

/**
 * Helper class of the PriceAlertServiceTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class PriceAlertServiceFixture {
    /**
     * Gets the Instrument of the Apple stock.
     *
     * @return The Instrument of the Apple stock.
     */
    public Instrument getAppleInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setName("Apple");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Microsoft stock.
     *
     * @return The Instrument of the Microsoft stock.
     */
    public Instrument getMicrosoftInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("MSFT");
        instrument.setName("Microsoft");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Netflix stock.
     *
     * @return The Instrument of the Netflix stock.
     */
    public Instrument getNetflixInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("NFLX");
        instrument.setName("Netflix");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the NVidia stock.
     *
     * @return The Instrument of the NVidia stock.
     */
    public Instrument getNvidiaInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("NVDA");
        instrument.setName("NVidia");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets a price alert for the Apple stock.
     *
     * @param instrument The Instrument of the PriceAlert.
     * @return A price alert for the Apple stock.
     */
    public PriceAlert getAppleAlert(final Instrument instrument) {
        PriceAlert alert = new PriceAlert();

        alert.setInstrument(instrument);
        alert.setAlertType(PriceAlertType.GREATER_OR_EQUAL);
        alert.setPrice(BigDecimal.valueOf(185.50));
        alert.setCurrency(Currency.USD);

        return alert;
    }

    /**
     * Gets a price alert for the Microsoft stock.
     *
     * @param instrument The Instrument of the PriceAlert.
     * @return A price alert for the Microsoft stock.
     */
    public PriceAlert getMicrosoftAlert(final Instrument instrument) {
        PriceAlert alert = new PriceAlert();

        alert.setInstrument(instrument);
        alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
        alert.setPrice(BigDecimal.valueOf(250.00));
        alert.setCurrency(Currency.USD);

        return alert;
    }

    /**
     * Gets a price alert for the Netflix stock.
     *
     * @param instrument The Instrument of the PriceAlert.
     * @return A price alert for the Netflix stock.
     */
    public PriceAlert getNetflixAlert(final Instrument instrument) {
        PriceAlert alert = new PriceAlert();

        alert = new PriceAlert();
        alert.setInstrument(instrument);
        alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
        alert.setPrice(BigDecimal.valueOf(199.99));
        alert.setCurrency(Currency.USD);
        alert.setTriggerTime(new Date());
        alert.setConfirmationTime(null);
        alert.setSendMail(true);
        alert.setAlertMailAddress("max.mustermann@muster.de");
        alert.setMailTransmissionTime(new Date());

        return alert;
    }

    /**
     * Gets a price alert for the Nvidia stock.
     *
     * @param instrument The Instrument of the PriceAlert.
     * @return A price alert for the Nvidia stock.
     */
    public PriceAlert getNvidiaAlert(final Instrument instrument) {
        PriceAlert alert = new PriceAlert();

        alert = new PriceAlert();
        alert.setInstrument(instrument);
        alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
        alert.setPrice(BigDecimal.valueOf(180.00));
        alert.setCurrency(Currency.USD);
        alert.setTriggerTime(new Date());
        alert.setConfirmationTime(new Date());

        return alert;
    }

    /**
     * Converts a PriceAlert to the lean WebService representation.
     *
     * @param priceAlert The PriceAlert to be converted.
     * @return The lean WebService representation of the PriceAlert.
     */
    public PriceAlertWS convertToWsPriceAlert(final PriceAlert priceAlert) {
        PriceAlertWS priceAlertWS = new PriceAlertWS();

        // Simple attributes.
        priceAlertWS.setId(priceAlert.getId());
        priceAlertWS.setAlertType(priceAlert.getAlertType());
        priceAlertWS.setPrice(priceAlert.getPrice());
        priceAlertWS.setCurrency(priceAlert.getCurrency());
        priceAlertWS.setTriggerDistancePercent(priceAlert.getTriggerDistancePercent());
        priceAlertWS.setConfirmationTime(priceAlert.getConfirmationTime());
        priceAlertWS.setTriggerTime(priceAlert.getTriggerTime());
        priceAlertWS.setLastStockQuoteTime(priceAlert.getLastStockQuoteTime());
        priceAlertWS.setSendMail(priceAlert.isSendMail());
        priceAlertWS.setAlertMailAddress(priceAlert.getAlertMailAddress());
        priceAlertWS.setMailTransmissionTime(priceAlert.getMailTransmissionTime());

        // Object references.
        if (priceAlert.getInstrument() != null)
            priceAlertWS.setInstrumentId(priceAlert.getInstrument().getId());

        return priceAlertWS;
    }
}
