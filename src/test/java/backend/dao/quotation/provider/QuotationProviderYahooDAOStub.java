package backend.dao.quotation.provider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the finance API of Yahoo.
 * A local JSON file is used instead of a live query to Yahoo finance.
 * 
 * @author Michael
 *
 */
public class QuotationProviderYahooDAOStub extends QuotationProviderYahooDAO {
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, InstrumentType instrumentType, 
			Integer years) throws Exception {
		
		String jsonPath = "";
		
		if(symbol.equals("DML") && stockExchange.equals(StockExchange.TSX))
			jsonPath = "src/test/resources/yahoo/YahooTSEQuotationHistoryDML.json";
		else if(symbol.equals("BNCH") && stockExchange.equals(StockExchange.TSXV))
			jsonPath = "src/test/resources/yahoo/YahooTSXVQuotationHistoryBNCH.json";
		else if(symbol.equals("RIO") && stockExchange.equals(StockExchange.LSE))
			jsonPath = "src/test/resources/yahoo/YahooLSEQuotationHistoryRIO.json";
		else
			return null;
		
		String quotationHistoryJSON = Files.readString(Paths.get(jsonPath));
		
		return this.convertJSONToQuotations(quotationHistoryJSON);
	}
	
	
	@Override
	public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
		String jsonPath = this.getJsonPathChart(instrument);
		String currentQuotationJSON = Files.readString(Paths.get(jsonPath));
		
		return this.convertChartJSONToQuotation(currentQuotationJSON);
	}
	
	
	/**
	 * Gets the path to the JSON file containing data of the quote API.
	 * 
	 * These files are currently not used because Yahoo has discontinued the quote API.
	 * Instead the chart API is used now.
	 * 
	 * @param instrument The Instrument.
	 * @return The path.
	 */
	@SuppressWarnings("unused")
	private String getJsonPathQuote(final Instrument instrument) {
		if(instrument.getSymbol().equals("DML") && instrument.getStockExchange().equals(StockExchange.TSX))
			return "src/test/resources/Yahoo/yahooTSEQuoteDML.json";
		else if(instrument.getSymbol().equals("RIO") && instrument.getStockExchange().equals(StockExchange.LSE))
			return "src/test/resources/Yahoo/yahooLSEQuoteRIO.json";
		else
			return null;
	}
	
	
	/**
	 * Gets the path to the JSON file containing data of the chart API.
	 * 
	 * @param instrument The Instrument.
	 * @return The path.
	 */
	private String getJsonPathChart(final Instrument instrument) {
		if(instrument.getSymbol().equals("DML") && instrument.getStockExchange().equals(StockExchange.TSX))
			return "src/test/resources/Yahoo/yahooTSEChartDML.json";
		else if(instrument.getSymbol().equals("RIO") && instrument.getStockExchange().equals(StockExchange.LSE))
			return "src/test/resources/Yahoo/yahooLSEChartRIO.json";
		else
			return null;
	}
}
