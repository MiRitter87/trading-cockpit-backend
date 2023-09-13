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
     * Gets historical quotations of Denison Mines stock. The quotations of the three most recent trading days are
     * provided.
     *
     * @return Historical quotations of Denison Mines stock
     */
    public List<Quotation> getDenisonMinesQuotationHistory() {
        List<Quotation> historicalQuotations = new ArrayList<>();
        Quotation quotation = new Quotation();
        long secondsSince1970;

        secondsSince1970 = 1658496600;
        quotation.setDate(new Date(secondsSince1970 * 1000));
        quotation.setOpen(BigDecimal.valueOf(1.45));
        quotation.setHigh(BigDecimal.valueOf(1.48));
        quotation.setLow(BigDecimal.valueOf(1.35));
        quotation.setClose(BigDecimal.valueOf(1.36));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(1793300);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        secondsSince1970 = 1658410200;
        quotation.setDate(new Date(secondsSince1970 * 1000));
        quotation.setOpen(BigDecimal.valueOf(1.50));
        quotation.setHigh(BigDecimal.valueOf(1.52));
        quotation.setLow(BigDecimal.valueOf(1.44));
        quotation.setClose(BigDecimal.valueOf(1.46));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(1450900);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        secondsSince1970 = 1658323800;
        quotation.setDate(new Date(secondsSince1970 * 1000));
        quotation.setOpen(BigDecimal.valueOf(1.49));
        quotation.setHigh(BigDecimal.valueOf(1.54));
        quotation.setLow(BigDecimal.valueOf(1.46));
        quotation.setClose(BigDecimal.valueOf(1.53));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(1534800);
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
        long secondsSince1970;

        secondsSince1970 = 1672823859;
        quotation.setDate(new Date(secondsSince1970 * 1000));
        quotation.setOpen(BigDecimal.valueOf(5910));
        quotation.setHigh(BigDecimal.valueOf(5941));
        quotation.setLow(BigDecimal.valueOf(5834));
        quotation.setClose(BigDecimal.valueOf(5835));
        quotation.setCurrency(Currency.GBP);
        quotation.setVolume(243671);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        secondsSince1970 = 1672732800;
        quotation.setDate(new Date(secondsSince1970 * 1000));
        quotation.setOpen(BigDecimal.valueOf(5818));
        quotation.setHigh(BigDecimal.valueOf(5905));
        quotation.setLow(BigDecimal.valueOf(5810));
        quotation.setClose(BigDecimal.valueOf(5839));
        quotation.setCurrency(Currency.GBP);
        quotation.setVolume(2112533);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        secondsSince1970 = 1672387200;
        quotation.setDate(new Date(secondsSince1970 * 1000));
        quotation.setOpen(BigDecimal.valueOf(5803));
        quotation.setHigh(BigDecimal.valueOf(5846));
        quotation.setLow(BigDecimal.valueOf(5787));
        quotation.setClose(BigDecimal.valueOf(5798));
        quotation.setCurrency(Currency.GBP);
        quotation.setVolume(588428);
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

        quotation.setClose(BigDecimal.valueOf(1.39));
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

        quotation.setClose(BigDecimal.valueOf(4821.5));
        quotation.setCurrency(Currency.GBP);

        return quotation;
    }
}
