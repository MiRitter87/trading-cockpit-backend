package backend.dao.stockQuote;

import java.io.IOException;
import java.math.BigDecimal;
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
		//double regularMarketPrice;
		String symbolFromAPI = "";
		
		try {
			Map<?, ?> map = mapper.readValue(Paths.get("src/test/resources/yahooTSEQuoteDML.json").toFile(), Map.class);
			LinkedHashMap quoteResponse = (LinkedHashMap) map.get("quoteResponse");
			ArrayList<?> result = (ArrayList<?>) quoteResponse.get("result");
			LinkedHashMap<?, ?> resultAttributes = (LinkedHashMap<?, ?>) result.get(0);
			
			//regularMarketPrice = (double) resultAttributes.get("regularMarketPrice");
			symbolFromAPI = (String) resultAttributes.get("symbol");
			
			stockQuote.setSymbol(symbolFromAPI.split("\\.")[0]);
			//stockQuote.setPrice(BigDecimal.valueOf(regularMarketPrice));
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
		
		//Load JSON file "yahooTSEQuoteDML.json"
		
		
		//Parse content
		//Price is at quoteResponse -> result -> regularMarketPrice
		
		//Create StockQuote object and fill attributes with parsed content
		
		//Return StockQuote object
		
		return stockQuote;
	}
}
