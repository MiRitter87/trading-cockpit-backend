package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import backend.controller.scan.IndicatorCalculator;
import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Helper class of the QuotationServiceTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class QuotationServiceFixture {
    /**
     * Gets the Instrument of the Industrial Sector.
     *
     * @return The Instrument of the Industrial Sector.
     */
    public Instrument getXliSector() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLI");
        instrument.setName("Industrial Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.SECTOR);

        return instrument;
    }

    /**
     * Gets the Instrument of the Copper Miners Industry Group.
     *
     * @return The Instrument of the Copper Miners Industry Group.
     */
    public Instrument getCopperIndustryGroup() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("COPX");
        instrument.setName("Global X Copper Miners ETF");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.IND_GROUP);

        return instrument;
    }

    /**
     * Gets the Instrument of the Apple stock.
     *
     * @param sector        The sector Instrument.
     * @param industryGroup The industry group Instrument.
     * @return The Instrument of the Apple stock.
     */
    public Instrument getAppleStock(final Instrument sector, final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setName("Apple");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);
        instrument.setSector(sector);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the Instrument of the Microsoft stock.
     *
     * @param sector        The sector Instrument.
     * @param industryGroup The industry group Instrument.
     * @return The Instrument of the Microsoft stock.
     */
    public Instrument getMicrosoftStock(final Instrument sector, final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("MSFT");
        instrument.setName("Microsoft");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);
        instrument.setSector(sector);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the Instrument of the Ford stock.
     *
     * @param sector        The sector Instrument.
     * @param industryGroup The industry group Instrument.
     * @return The Instrument of the Ford stock.
     */
    public Instrument getFordStock(final Instrument sector, final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("F");
        instrument.setName("Ford Motor Company");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.STOCK);
        instrument.setSector(sector);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the Instrument of the Denison Mines stock.
     *
     * @param sector        The sector Instrument.
     * @param industryGroup The industry group Instrument.
     * @return The Instrument of the Denison Mines stock.
     */
    public Instrument getDenisonMinesStock(final Instrument sector, final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("DML");
        instrument.setStockExchange(StockExchange.TSX);
        instrument.setType(InstrumentType.STOCK);
        instrument.setName("Denison Mines");
        instrument.setSector(sector);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the Instrument of the XLE ETF.
     *
     * @param sector        The sector Instrument.
     * @param industryGroup The industry group Instrument.
     * @return The Instrument of the XLE ETF.
     */
    public Instrument getXleEtf(final Instrument sector, final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLE");
        instrument.setName("Energy Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.ETF);
        instrument.setSector(sector);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the Instrument of the XLB ETF.
     *
     * @param sector        The sector Instrument.
     * @param industryGroup The industry group Instrument.
     * @return The Instrument of the XLB ETF.
     */
    public Instrument getXlbEtf(final Instrument sector, final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLB");
        instrument.setName("Materials Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.ETF);
        instrument.setSector(sector);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the Instrument of the XLF ETF.
     *
     * @param sector        The sector Instrument.
     * @param industryGroup The industry group Instrument.
     * @return The Instrument of the XLF ETF.
     */
    public Instrument getXlfEtf(final Instrument sector, final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLF");
        instrument.setName("Financial Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.ETF);
        instrument.setSector(sector);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the Quotation 1 of the Apple stock.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation 1 of the Apple stock.
     */
    public Quotation getAppleQuotation1(final Instrument instrument) {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(78.54));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(28973654);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets the Quotation 2 of the Apple stock.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation 2 of the Apple stock.
     */
    public Quotation getAppleQuotation2(final Instrument instrument) {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(77.52));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(12373654);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the Microsoft stock.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation 1 of the Microsoft stock.
     */
    public Quotation getMicrosoftQuotation1(final Instrument instrument) {
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        quotation.setClose(BigDecimal.valueOf(124.07));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(13973124);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the Ford stock.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation 1 of the Ford stock.
     */
    public Quotation getFordQuotation1(final Instrument instrument) {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(15.88));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(48600000);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the XLE ETF.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation 1 of the XLE ETF.
     */
    public Quotation getXleQuotation1(final Instrument instrument) {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(81.28));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(18994000);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets the Quotation 2 of the XLE ETF.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation 2 of the XLE ETF.
     */
    public Quotation getXleQuotation2(final Instrument instrument) {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(81.99));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(25187000);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the XLB ETF.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation 1 of the XLB ETF.
     */
    public Quotation getXlbQuotation1(final Instrument instrument) {
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        quotation.setClose(BigDecimal.valueOf(71.25));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(79794000);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the XLF ETF.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation 1 of the XLF ETF.
     */
    public Quotation getXlfQuotation1(final Instrument instrument) {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(32.30));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(48148000);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets quotations of the Denison Mines stock.
     *
     * @param instrument The referenced Instrument.
     * @return A List of quotations.
     */
    public List<Quotation> getDenisonMinesQuotationsWithoutIndicators(final Instrument instrument) {
        QuotationProviderDAO quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
        List<Quotation> quotationsWithoutIndicators = new ArrayList<>();

        try {
            quotationsWithoutIndicators.addAll(
                    quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1));

            for (Quotation tempQuotation : quotationsWithoutIndicators) {
                tempQuotation.setInstrument(instrument);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }

        return quotationsWithoutIndicators;
    }

    /**
     * Gets the appleQuotation2Indicator.
     *
     * @return The appleQuotation2Indicator.
     */
    public Indicator getAppleQuotation2Indicator() {
        Indicator indicator = new Indicator(true);

        indicator.setStage(2);
        indicator.getMovingAverageData().setSma200(60);
        indicator.getMovingAverageData().setSma150((float) 63.45);
        indicator.getMovingAverageData().setSma50((float) 69.24);
        indicator.setPerformance5Days((float) 12.44);
        indicator.setDistanceTo52WeekHigh((float) -4.4);
        indicator.setDistanceTo52WeekLow((float) 78.81);
        indicator.setBollingerBandWidth((float) 8.71);
        indicator.setVolumeDifferential5Days((float) 47.18);
        indicator.setVolumeDifferential10Days((float) 19.34);
        indicator.setBaseLengthWeeks(32);

        return indicator;
    }

    /**
     * Gets the fordQuotation1Indicator.
     *
     * @return The fordQuotation1Indicator.
     */
    public Indicator getFordQuotation1Indicator() {
        Indicator indicator = new Indicator(true);

        indicator.setStage(3);
        indicator.getMovingAverageData().setSma200((float) 16.36);
        indicator.getMovingAverageData().setSma150((float) 15.08);
        indicator.getMovingAverageData().setSma50((float) 13.07);
        indicator.setPerformance5Days((float) -10.21);
        indicator.setDistanceTo52WeekHigh((float) -9.41);
        indicator.setDistanceTo52WeekLow((float) 48.81);
        indicator.setBollingerBandWidth((float) 4.11);
        indicator.setVolumeDifferential5Days((float) 25.55);
        indicator.setVolumeDifferential10Days((float) -9.67);
        indicator.setBaseLengthWeeks(3);

        return indicator;
    }

    /**
     * Gets the xleQuotation2Indicator.
     *
     * @return The xleQuotation2Indicator.
     */
    public Indicator getXleQuotation2Indicator() {
        Indicator indicator = new Indicator(true);

        indicator.setStage(2);
        indicator.getMovingAverageData().setSma200((float) 74.02);
        indicator.getMovingAverageData().setSma150((float) 76.84);
        indicator.getMovingAverageData().setSma50((float) 78.15);
        indicator.setPerformance5Days((float) 3.17);
        indicator.setDistanceTo52WeekHigh((float) -21.4);
        indicator.setDistanceTo52WeekLow((float) 78.81);
        indicator.setBollingerBandWidth((float) 8.71);
        indicator.setVolumeDifferential5Days((float) 12.12);
        indicator.setVolumeDifferential10Days((float) 19.34);
        indicator.setBaseLengthWeeks(32);

        return indicator;
    }

    /**
     * Gets the xlbQuotation1Indicator.
     *
     * @return The xlbQuotation1Indicator.
     */
    public Indicator getXlbQuotation1Indicator() {
        Indicator indicator = new Indicator(true);

        indicator.setStage(4);
        indicator.getMovingAverageData().setSma200((float) 79.83);
        indicator.getMovingAverageData().setSma150((float) 78.64);
        indicator.getMovingAverageData().setSma50((float) 74.01);
        indicator.setPerformance5Days((float) -6.70);
        indicator.setDistanceTo52WeekHigh((float) -9.41);
        indicator.setDistanceTo52WeekLow((float) 0.81);
        indicator.setBollingerBandWidth((float) 4.11);
        indicator.setVolumeDifferential5Days((float) 21.89);
        indicator.setVolumeDifferential10Days((float) -9.67);
        indicator.setBaseLengthWeeks(3);

        return indicator;
    }

    /**
     * Calculates indicators and adds them to the DML quotations.
     *
     * @param instrument The DML instrument.
     * @param quotations The DML quotations without indicators.
     * @return A List of quotations with indicators.
     */
    public List<Quotation> getDenisonMinesQuotationsWithIndicators(final Instrument instrument,
            final List<Quotation> quotations) {
        Quotation quotation;
        List<Quotation> quotationsWithIndicators = new ArrayList<>();
        IndicatorCalculator indicatorCalculator = new IndicatorCalculator();

        // Quotations of Instrument are needed for indicator calculation.
        instrument.setQuotations(quotations);

        for (int i = 0; i < quotations.size(); i++) {
            quotation = quotations.get(i);

            if (i == 0)
                quotation = indicatorCalculator.calculateIndicators(instrument, quotation, true);
            else
                quotation = indicatorCalculator.calculateIndicators(instrument, quotation, false);

            quotationsWithIndicators.add(quotation);
        }

        return quotationsWithIndicators;
    }
}
