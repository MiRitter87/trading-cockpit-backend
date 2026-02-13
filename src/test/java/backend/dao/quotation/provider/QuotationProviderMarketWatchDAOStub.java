package backend.dao.quotation.provider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the MarketWatch CSV downloader. Local CSV files are used instead
 * of a live query to MarketWatch.
 *
 * @author Michael
 */
public class QuotationProviderMarketWatchDAOStub extends QuotationProviderMarketWatchDAO {
    /**
     * Gets the Quotation history.
     */
    @Override
    public List<Quotation> getQuotationHistory(final Instrument instrument, final Integer years) throws Exception {
        String csvPath = "";

        if (instrument.getSymbol().equals("DML") && instrument.getStockExchange().equals(StockExchange.TSX)) {
            csvPath = "src/test/resources/MarketWatch/MarketWatchTSEQuotationHistoryDML.csv";
        } else if (instrument.getSymbol().equals("RIO") && instrument.getStockExchange().equals(StockExchange.LSE)) {
            csvPath = "src/test/resources/MarketWatch/MarketWatchLSEQuotationHistoryRIO.csv";
        } else {
            return null;
        }

        String quotationHistoryCSV = Files.readString(Paths.get(csvPath));

        return this.convertCSVToQuotations(quotationHistoryCSV, instrument.getStockExchange());
    }
}
