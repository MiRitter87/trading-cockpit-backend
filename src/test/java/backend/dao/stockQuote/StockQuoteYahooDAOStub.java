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
 * Stub to simulate receiving of stock data using the finance API of Yahoo.
 * A local JSON file is used instead of a live query to Yahoo finance.
 * 
 * @author Michael
 *
 */
public class StockQuoteYahooDAOStub extends StockQuoteYahooDAO {
	@Override
	public StockQuote getStockQuote(String symbol, StockExchange stockExchange) throws Exception {
		StockQuote stockQuote = new StockQuote();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Map<?, ?> map = mapper.readValue(Paths.get("src/test/resources/yahooTSEQuoteDML.json").toFile(), Map.class);
			LinkedHashMap<?, ?> quoteResponse = (LinkedHashMap<?, ?>) map.get("quoteResponse");
			ArrayList<?> result = (ArrayList<?>) quoteResponse.get("result");
			LinkedHashMap<?, ?> resultAttributes = (LinkedHashMap<?, ?>) result.get(0);
			
			stockQuote.setSymbol(this.getSymbol((String) resultAttributes.get("symbol")));
			stockQuote.setStockExchange(this.getExchange((String) resultAttributes.get("exchange")));
			stockQuote.setPrice(this.getPrice((double) resultAttributes.get("regularMarketPrice")));
			stockQuote.setCurrency(this.getCurrency((String) resultAttributes.get("financialCurrency")));
		} catch (JsonParseException e) {
			throw new Exception(e);
		} catch (JsonMappingException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		}
		
		return stockQuote;
	}
}
