package backend.dao.stockQuote;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.model.StockExchange;
import backend.model.stockQuote.StockQuote;

/**
 * Retrieves stock data using the finance API of Yahoo.
 * 
 * @author Michael
 *
 */
public class StockQuoteYahooDAOStub extends StockQuoteYahooDAO {
	@Override
	public StockQuote getStockQuote(String symbol, StockExchange stockExchange) {
		StockQuote stockQuote = new StockQuote();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Map<?, ?> map = mapper.readValue(Paths.get("src/test/resources/yahooTSEQuoteDML.json").toFile(), Map.class);
			LinkedHashMap<?, ?> quoteResponse = (LinkedHashMap) map.get("quoteResponse");
			ArrayList<?> result = (ArrayList<?>) quoteResponse.get("result");
			LinkedHashMap<?, ?> resultAttributes = (LinkedHashMap<?, ?>) result.get(0);
			
			stockQuote.setSymbol(this.getSymbol((String) resultAttributes.get("symbol")));
			stockQuote.setStockExchange(this.getExchange((String) resultAttributes.get("exchange")));
			stockQuote.setPrice(this.getPrice((double) resultAttributes.get("regularMarketPrice")));
			stockQuote.setCurrency(this.getCurrency((String) resultAttributes.get("financialCurrency")));
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stockQuote;
	}
}
