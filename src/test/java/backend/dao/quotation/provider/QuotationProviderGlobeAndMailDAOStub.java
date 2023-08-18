package backend.dao.quotation.provider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the theglobeandmail.com website.
 * <p>
 *
 * A local HTML file is used for current quotation instead of a live query to theglobeandmail.com.<br>
 * A local csv file is used for quotation history instead of a live query to theglobeandmail.com.
 *
 * @author Michael
 */
public class QuotationProviderGlobeAndMailDAOStub extends QuotationProviderGlobeAndMailDAO {
    @Override
    public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
        String userPath = System.getProperty("user.dir");
        String htmlPath = "file:\\" + userPath;
        WebClient webClient = new WebClient();
        HtmlPage htmlPage;
        Quotation quotation;

        this.disableHtmlUnitLogging();

        if (instrument.getSymbol().equals("PMET") && instrument.getStockExchange().equals(StockExchange.TSXV))
            htmlPath = htmlPath + "\\src\\test\\resources\\GlobeAndMail\\GlobeAndMailTSXVQuotePMET.htm";

        try {
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            htmlPage = webClient.getPage(htmlPath);

            quotation = this.getQuotationFromHtmlPage(htmlPage, instrument);
        } finally {
            webClient.close();
        }

        return quotation;
    }

    @Override
    public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange,
            InstrumentType instrumentType, Integer years) throws Exception {

        String csvPath = "";

        if (symbol.equals("DML") && stockExchange.equals(StockExchange.TSX))
            csvPath = "src/test/resources/GlobeAndMail/GlobeAndMailTSXHistoryDML.csv";
        else
            return null;

        String quotationHistoryCSV = Files.readString(Paths.get(csvPath));

        return null;
        // return this.convertCSVToQuotations(quotationHistoryCSV, stockExchange);
    }
}
