package backend.controller.chart.data;

import backend.dao.DAOManager;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.QuotationArray;

/**
 * Controller to provide data for the construction of a price/volume chart.
 *
 * @author Michael
 */
public class PriceVolumeDataController {
    /**
     * DAO for Quotation access.
     */
    private QuotationDAO quotationDAO;

    /**
     * Initializes the PriceVolumeDataController.
     */
    public PriceVolumeDataController() {
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
    }

    /**
     * Provides price and volume data as well as indicators to construct a price/volume chart.
     *
     * @param instrumentId The ID of the Instrument used for chart data creation.
     * @return The price/volume chart data.
     * @throws Exception Chart data generation failed.
     */
    public QuotationArray getPriceVolumeData(final Integer instrumentId) throws Exception {
        QuotationArray quotations = new QuotationArray(this.quotationDAO.getQuotationsOfInstrument(instrumentId));

        quotations.sortQuotationsByDate();

        // Calculate BBW(10,2)

        // Calculate SlowSto(14,3)

        return quotations;
    }
}
