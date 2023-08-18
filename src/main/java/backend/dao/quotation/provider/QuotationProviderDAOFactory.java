package backend.dao.quotation.provider;

import backend.controller.DataProvider;
import backend.controller.MainController;
import okhttp3.OkHttpClient;

/**
 * Factory class for QuotationProviderDAO implementations.
 *
 * @author Michael
 */
public final class QuotationProviderDAOFactory {
    /**
     * Instance of this class.
     */
    private static QuotationProviderDAOFactory instance;

    /**
     * Client that is used for HTTP queries of third-party WebServices.
     */
    private OkHttpClient okHttpClient;

    /**
     * Provider of quotation data from Yahoo.
     */
    private QuotationProviderYahooDAO yahooDAO;

    /**
     * Provider of quotation data from MarketWatch.
     */
    private QuotationProviderMarketWatchDAO marketWatchDAO;

    /**
     * Provider of quotation data from CNBC.
     */
    private QuotationProviderCNBCDAO cnbcDAO;

    /**
     * Provider of quotation data from Investing.
     */
    private QuotationProviderInvestingDAO investingDAO;

    /**
     * Provider of quotation data from GlobeAndMail.
     */
    private QuotationProviderGlobeAndMailDAO globeAndMailDAO;

    /**
     * Initializes the QuotationProviderDAOFactory.
     */
    private QuotationProviderDAOFactory() {
        this.okHttpClient = MainController.getInstance().getOkHttpClient();
    }

    /**
     * Provides the instance of the QuotationProviderDAOFactory.
     *
     * @return The instance of the QuotationProviderDAOFactory.
     */
    public static QuotationProviderDAOFactory getInstance() {
        if (instance == null) {
            instance = new QuotationProviderDAOFactory();
        }

        return instance;
    }

    /**
     * Returns a QuotationProviderDAO implementation based on the given DataProvider.
     *
     * @param dataProvider The DataProvider for which the QuotationProviderDAO is requested.
     * @return The QuotationProviderDAO for the given DataProvider.
     */
    public QuotationProviderDAO getQuotationProviderDAO(final DataProvider dataProvider) {
        switch (dataProvider) {
        case YAHOO:
            return this.getQuotationProviderYahooDAO();
        case MARKETWATCH:
            return this.getQuotationProviderMarketWatchDAO();
        case CNBC:
            return this.getQuotationProviderCNBCDAO();
        case INVESTING:
            return this.getQuotationProviderInvestingDAO();
        case GLOBEANDMAIL:
            return this.getQuotationProviderGlobeAndMailDAO();
        default:
            return null;
        }
    }

    /**
     * Returns a DAO to access data from Yahoo.
     *
     * @return The QuotationProviderYahooDAO.
     */
    private QuotationProviderYahooDAO getQuotationProviderYahooDAO() {
        if (this.yahooDAO == null) {
            this.yahooDAO = new QuotationProviderYahooDAO(this.okHttpClient);
        }

        return this.yahooDAO;
    }

    /**
     * Returns a DAO to access data from MarketWatch.
     *
     * @return The QuotationProviderMarketWatchDAO.
     */
    private QuotationProviderMarketWatchDAO getQuotationProviderMarketWatchDAO() {
        if (this.marketWatchDAO == null) {
            this.marketWatchDAO = new QuotationProviderMarketWatchDAO(this.okHttpClient);
        }

        return this.marketWatchDAO;
    }

    /**
     * Returns a DAO to access data from CNBC.
     *
     * @return The QuotationProviderMarketWatchDAO.
     */
    private QuotationProviderCNBCDAO getQuotationProviderCNBCDAO() {
        if (this.cnbcDAO == null) {
            this.cnbcDAO = new QuotationProviderCNBCDAO(this.okHttpClient);
        }

        return this.cnbcDAO;
    }

    /**
     * Returns a DAO to access data from Investing.
     *
     * @return The QuotationProviderInvestingDAO.
     */
    private QuotationProviderInvestingDAO getQuotationProviderInvestingDAO() {
        if (this.investingDAO == null) {
            this.investingDAO = new QuotationProviderInvestingDAO();
        }

        return this.investingDAO;
    }

    /**
     * Returns a DAO to access data from GlobeAndMail.
     *
     * @return The QuotationProviderGlobeAndMailDAO.
     */
    private QuotationProviderGlobeAndMailDAO getQuotationProviderGlobeAndMailDAO() {
        if (this.globeAndMailDAO == null) {
            this.globeAndMailDAO = new QuotationProviderGlobeAndMailDAO(this.okHttpClient);
        }

        return this.globeAndMailDAO;
    }
}
