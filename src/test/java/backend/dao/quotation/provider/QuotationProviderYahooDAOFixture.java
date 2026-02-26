package backend.dao.quotation.provider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import backend.model.Currency;
import backend.model.instrument.Quotation;

/**
 * Helper class of the QuotationProviderYahooDAOTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class QuotationProviderYahooDAOFixture {
    /**
     * Milliseconds per second.
     */
    private final int millisPerSecond = 1000;

    /**
     * Gets historical quotations of Denison Mines stock. The quotations of the three most recent trading days are
     * provided.
     *
     * @return Historical quotations of Denison Mines stock
     */
    public List<Quotation> getDenisonMinesQuotationHistory() {
        List<Quotation> historicalQuotations = new ArrayList<>();
        Quotation quotation = new Quotation();
        final long seconds1 = 1658496600;
        final long seconds2 = 1658410200;
        final long seconds3 = 1658323800;
        final long volume1 = 1793300;
        final long volume2 = 1450900;
        final long volume3 = 1534800;

        quotation.setDate(new Date(seconds1 * millisPerSecond));
        quotation.setOpen(new BigDecimal("1.45"));
        quotation.setHigh(new BigDecimal("1.48"));
        quotation.setLow(new BigDecimal("1.35"));
        quotation.setClose(new BigDecimal("1.36"));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(volume1);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        quotation.setDate(new Date(seconds2 * millisPerSecond));
        quotation.setOpen(new BigDecimal("1.50"));
        quotation.setHigh(new BigDecimal("1.52"));
        quotation.setLow(new BigDecimal("1.44"));
        quotation.setClose(new BigDecimal("1.46"));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(volume2);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        quotation.setDate(new Date(seconds3 * millisPerSecond));
        quotation.setOpen(new BigDecimal("1.49"));
        quotation.setHigh(new BigDecimal("1.54"));
        quotation.setLow(new BigDecimal("1.46"));
        quotation.setClose(new BigDecimal("1.53"));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(volume3);
        historicalQuotations.add(quotation);

        return historicalQuotations;
    }

    /**
     * Gets historical quotations of Rio Tinto stock. The quotations of the three most recent trading days are provided.
     *
     * @return Historical quotations of Rio Tinto stock
     */
    public List<Quotation> getRioTintoQuotationHistory() {
        List<Quotation> historicalQuotations = new ArrayList<>();
        Quotation quotation = new Quotation();
        final long seconds1 = 1672823859;
        final long seconds2 = 1672732800;
        final long seconds3 = 1672387200;
        final long volume1 = 243671;
        final long volume2 = 2112533;
        final long volume3 = 588428;

        quotation.setDate(new Date(seconds1 * millisPerSecond));
        quotation.setOpen(new BigDecimal("5910"));
        quotation.setHigh(new BigDecimal("5941"));
        quotation.setLow(new BigDecimal("5834"));
        quotation.setClose(new BigDecimal("5835"));
        quotation.setCurrency(Currency.GBP);
        quotation.setVolume(volume1);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        quotation.setDate(new Date(seconds2 * millisPerSecond));
        quotation.setOpen(new BigDecimal("5818"));
        quotation.setHigh(new BigDecimal("5905"));
        quotation.setLow(new BigDecimal("5810"));
        quotation.setClose(new BigDecimal("5839"));
        quotation.setCurrency(Currency.GBP);
        quotation.setVolume(volume2);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        quotation.setDate(new Date(seconds3 * millisPerSecond));
        quotation.setOpen(new BigDecimal("5803"));
        quotation.setHigh(new BigDecimal("5846"));
        quotation.setLow(new BigDecimal("5787"));
        quotation.setClose(new BigDecimal("5798"));
        quotation.setCurrency(Currency.GBP);
        quotation.setVolume(volume3);
        historicalQuotations.add(quotation);

        return historicalQuotations;
    }

    /**
     * Gets a Quotation as expected from the Yahoo service.
     *
     * @return A Quotation.
     */
    public Quotation getDenisonMinesQuotation() {
        Quotation quotation = new Quotation();

        quotation.setClose(new BigDecimal("1.39"));
        quotation.setCurrency(Currency.CAD);

        return quotation;
    }

    /**
     * Gets a Quotation as expected from the Yahoo service.
     *
     * @return A Quotation.
     */
    public Quotation getRioTintoQuotation() {
        Quotation quotation = new Quotation();

        quotation.setClose(new BigDecimal("4821.5"));
        quotation.setCurrency(Currency.GBP);

        return quotation;
    }
}
