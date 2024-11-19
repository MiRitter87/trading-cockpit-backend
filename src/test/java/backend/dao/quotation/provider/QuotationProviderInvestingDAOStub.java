package backend.dao.quotation.provider;

import java.nio.file.Files;
import java.nio.file.Paths;

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

        return this.convertJSONtoCurrentQuotation(quotationHistoryJSON, instrument);
    }
}
