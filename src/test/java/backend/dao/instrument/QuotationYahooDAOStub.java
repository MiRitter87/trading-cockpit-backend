package backend.dao.instrument;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the finance API of Yahoo.
 * A local JSON file is used instead of a live query to Yahoo finance.
 * 
 * @author Michael
 *
 */
public class QuotationYahooDAOStub extends QuotationYahooDAO {
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years) throws Exception {
		List<Quotation> quotationHistory = new ArrayList<>();
		Quotation quotation;
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Map<?, ?> map = mapper.readValue(Paths.get("src/test/resources/yahooTSEQuotationHistoryDML.json").toFile(), Map.class);
			LinkedHashMap<?, ?> quoteResponse = (LinkedHashMap<?, ?>) map.get("chart");
			ArrayList<?> result = (ArrayList<?>) quoteResponse.get("result");
			LinkedHashMap<?, ?> resultAttributes = (LinkedHashMap<?, ?>) result.get(0);
			ArrayList<?> timestamps = (ArrayList<?>) resultAttributes.get("timestamp");
			LinkedHashMap<?, ?> metaAttributes = (LinkedHashMap<?, ?>) resultAttributes.get("meta");
			LinkedHashMap<?, ?> indicators = (LinkedHashMap<?, ?>) resultAttributes.get("indicators");
			ArrayList<?> quote = (ArrayList<?>) indicators.get("quote");
			LinkedHashMap<?, ?> quoteAttributes = (LinkedHashMap<?, ?>) quote.get(0);
			ArrayList<?> volume = (ArrayList<?>) quoteAttributes.get("volume");
			
			for(int i = timestamps.size(); i>0; i--) {
				quotation = new Quotation();
				quotation.setDate(this.getDate(timestamps.get(i-1)));
				quotation.setCurrency(this.getCurrency((String) metaAttributes.get("currency")));
				quotation.setVolume(this.getVolumeFromQuotationHistoryResponse(volume, i-1));
				quotationHistory.add(quotation);
			}
			
			//System.out.println("Test");
		} 
		catch (JsonParseException e) {
			throw new Exception(e);
		} 
		catch (JsonMappingException e) {
			throw new Exception(e);
		} 
		catch (IOException e) {
			throw new Exception(e);
		}
		
		return quotationHistory;
	}
}
