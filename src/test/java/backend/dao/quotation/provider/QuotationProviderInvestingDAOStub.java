package backend.dao.quotation.provider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the investing.com website. A local HTML file is used instead of a
 * live query to investing.com.
 *
 * @author Michael
 */
public class QuotationProviderInvestingDAOStub extends QuotationProviderInvestingDAO {
    /**
     * Gets the current Quotation of the given Instrument.
     */
    @Override
    public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
        String jsonPath;
        String quotationHistoryJSON;

        if (instrument.getSymbol().equals("AMZN") && instrument.getStockExchange().equals(StockExchange.NDQ)) {
            jsonPath = "src/test/resources/Investing/investingCurlResultAAPL.json";
        } else {
            return null;
        }

        quotationHistoryJSON = Files.readString(Paths.get(jsonPath));

        return this.convertJSONToCurrentQuotation(quotationHistoryJSON, instrument);
    }

    /**
     * Gets the Quotation history.
     */
    @Override
    public List<Quotation> getQuotationHistory(final Instrument instrument, final Integer years) throws Exception {
        String jsonPath = "";
        String quotationHistoryJSON;

        if (instrument.getSymbol().equals("DML") && instrument.getStockExchange().equals(StockExchange.TSX)) {
            jsonPath = "src/test/resources/Investing/investingTSXHistoryDML.json";
        } else {
            return null;
        }

        quotationHistoryJSON = Files.readString(Paths.get(jsonPath));

        return this.convertJSONToQuotationHistory(quotationHistoryJSON, instrument);
    }
}
