package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.NoQuotationsExistException;
import backend.controller.instrumentCheck.InstrumentCheckController;
import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.chart.ChartObjectDAO;
import backend.dao.instrument.DuplicateInstrumentException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.LocalizedException;
import backend.model.ObjectInUseException;
import backend.model.chart.HorizontalLine;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentArray;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.InstrumentWS;
import backend.model.instrument.Quotation;
import backend.model.priceAlert.PriceAlert;
import backend.model.protocol.Protocol;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.DateTools;
import backend.tools.WebServiceTools;

/**
 * Common implementation of the instrument WebService that can be used by multiple service interfaces like SOAP or REST.
 *
 * @author Michael
 */
public class InstrumentService {
    /**
     * DAO for instrument access.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * DAO for Quotation access.
     */
    private QuotationDAO quotationDAO;

    /**
     * DAO for chart object access.
     */
    private ChartObjectDAO chartObjectDAO;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(InstrumentService.class);

    /**
     * Initializes the instrument service.
     */
    public InstrumentService() {
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
        this.chartObjectDAO = DAOManager.getInstance().getChartObjectDAO();
    }

    /**
     * Provides the instrument with the given id.
     *
     * @param id The id of the instrument.
     * @return The instrument with the given id, if found.
     */
    public WebServiceResult getInstrument(final Integer id) {
        Instrument instrument = null;
        WebServiceResult getInstrumentResult = new WebServiceResult(null);

        try {
            instrument = this.instrumentDAO.getInstrument(id);

            if (instrument != null) {
                // Instrument found
                getInstrumentResult.setData(instrument);
            } else {
                // Instrument not found
                getInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                        MessageFormat.format(this.resources.getString("instrument.notFound"), id)));
            }
        } catch (Exception e) {
            getInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("instrument.getError"), id)));

            LOGGER.error(MessageFormat.format(this.resources.getString("instrument.getError"), id), e);
        }

        return getInstrumentResult;
    }

    /**
     * Provides a list of all instruments.
     *
     * @param instrumentType The type of the instruments requested.
     * @return A list of all instruments of the given type.
     */
    public WebServiceResult getInstruments(final InstrumentType instrumentType) {
        InstrumentArray instruments = new InstrumentArray();
        WebServiceResult getInstrumentsResult = new WebServiceResult(null);

        try {
            instruments.setInstruments(this.instrumentDAO.getInstruments(instrumentType));
            getInstrumentsResult.setData(instruments);
        } catch (Exception e) {
            getInstrumentsResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    this.resources.getString("instrument.getInstrumentsError")));

            LOGGER.error(this.resources.getString("instrument.getInstrumentsError"), e);
        }

        return getInstrumentsResult;
    }

    /**
     * Adds an instrument.
     *
     * @param instrument The instrument to be added.
     * @return The result of the add function.
     */
    public WebServiceResult addInstrument(final InstrumentWS instrument) {
        Instrument convertedInstrument = new Instrument();
        WebServiceResult addInstrumentResult = new WebServiceResult();

        // Convert the WebService data transfer object to the internal data model.
        try {
            convertedInstrument = this.convertInstrument(instrument);
        } catch (Exception exception) {
            addInstrumentResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("instrument.addError")));
            LOGGER.error(this.resources.getString("instrument.addError"), exception);
            return addInstrumentResult;
        }

        // Validate the given Instrument.
        this.validateInstrument(convertedInstrument, addInstrumentResult);
        if (WebServiceTools.resultContainsErrorMessage(addInstrumentResult)) {
            return addInstrumentResult;
        }

        // Insert instrument if validation is successful.
        try {
            this.instrumentDAO.insertInstrument(convertedInstrument);
            addInstrumentResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.S, this.resources.getString("instrument.addSuccess")));
            addInstrumentResult.setData(convertedInstrument.getId());
        } catch (DuplicateInstrumentException duplicateInstrumentException) {
            addInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("instrument.createDuplicate"),
                            convertedInstrument.getSymbol(), convertedInstrument.getStockExchange())));
        } catch (Exception e) {
            addInstrumentResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("instrument.addError")));
            LOGGER.error(this.resources.getString("instrument.addError"), e);
        }

        return addInstrumentResult;
    }

    /**
     * Deletes the instrument with the given id.
     *
     * @param id The id of the instrument to be deleted.
     * @return The result of the delete function.
     */
    public WebServiceResult deleteInstrument(final Integer id) {
        WebServiceResult deleteInstrumentResult = new WebServiceResult(null);
        Instrument instrument = null;

        // Check if an instrument with the given id exists.
        try {
            instrument = this.instrumentDAO.getInstrument(id);

            if (instrument != null) {
                this.deleteReferencedObjects(id);

                // Delete instrument if exists.
                this.instrumentDAO.deleteInstrument(instrument);
                deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.S,
                        MessageFormat.format(this.resources.getString("instrument.deleteSuccess"), id)));
            } else {
                // Instrument not found.
                deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                        MessageFormat.format(this.resources.getString("instrument.notFound"), id)));
            }
        } catch (ObjectInUseException objectInUseException) {
            this.handleObjectInUseDeletion(id, objectInUseException, deleteInstrumentResult);
        } catch (Exception e) {
            deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("instrument.deleteError"), id)));

            LOGGER.error(MessageFormat.format(this.resources.getString("instrument.deleteError"), id), e);
        }

        return deleteInstrumentResult;
    }

    /**
     * Updates an existing instrument.
     *
     * @param instrument The instrument to be updated.
     * @return The result of the update function.
     */
    public WebServiceResult updateInstrument(final InstrumentWS instrument) {
        Instrument convertedInstrument = new Instrument();
        WebServiceResult updateInstrumentResult = new WebServiceResult(null);

        // Convert the WebService data transfer object to the internal data model.
        try {
            convertedInstrument = this.convertInstrument(instrument);
        } catch (Exception exception) {
            updateInstrumentResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("instrument.updateError")));
            LOGGER.error(this.resources.getString("instrument.updateError"), exception);
            return updateInstrumentResult;
        }

        // Validation of the given Instrument.
        this.validateInstrument(convertedInstrument, updateInstrumentResult);
        if (WebServiceTools.resultContainsErrorMessage(updateInstrumentResult)) {
            return updateInstrumentResult;
        }

        // Update instrument if validation is successful.
        try {
            this.instrumentDAO.updateInstrument(convertedInstrument);
            updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, MessageFormat
                    .format(this.resources.getString("instrument.updateSuccess"), convertedInstrument.getId())));
        } catch (ObjectUnchangedException objectUnchangedException) {
            updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.I, MessageFormat
                    .format(this.resources.getString("instrument.updateUnchanged"), convertedInstrument.getId())));
        } catch (DuplicateInstrumentException duplicateInstrumentException) {
            updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("instrument.updateDuplicate"),
                            convertedInstrument.getSymbol(), convertedInstrument.getStockExchange())));
        } catch (Exception e) {
            updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, MessageFormat
                    .format(this.resources.getString("instrument.updateError"), convertedInstrument.getId())));

            LOGGER.error(MessageFormat.format(this.resources.getString("instrument.updateError"),
                    convertedInstrument.getId()), e);
        }

        return updateInstrumentResult;
    }

    /**
     * Checks the health of the Instrument with the given id.
     *
     * @param instrumentId The ID of the instrument.
     * @param startDate    The start date for the health check. Format used: yyyy-MM-dd
     * @return A Protocol with health information about the given Instrument.
     */
    public WebServiceResult getInstrumentHealthProtocol(final Integer instrumentId, final String startDate) {
        WebServiceResult getHealthProtocolResult = new WebServiceResult();
        InstrumentCheckController controller = new InstrumentCheckController();
        Protocol protocol;
        Date convertedStartDate;

        try {
            convertedStartDate = DateTools.convertStringToDate(startDate);
            protocol = controller.checkInstrument(instrumentId, convertedStartDate);
            getHealthProtocolResult.setData(protocol);
        } catch (NoQuotationsExistException noQuotationsExistException) {
            getHealthProtocolResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    this.resources.getString("instrument.getHealthNoQuotationsForDate")));
        } catch (Exception e) {
            getHealthProtocolResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    this.resources.getString("instrument.getHealthError")));
            LOGGER.error(this.resources.getString("instrument.getHealthError"), e.getMessage(), e);
        }

        return getHealthProtocolResult;
    }

    /**
     * Converts the lean Instrument representation that is provided by the WebService to the internal data model for
     * further processing.
     *
     * @param instrumentWS The lean Instrument representation provided by the WebService.
     * @return The Instrument model that is used by the backend internally.
     * @throws Exception In case the conversion fails.
     */
    private Instrument convertInstrument(final InstrumentWS instrumentWS) throws Exception {
        Instrument instrument = new Instrument();

        // Simple object attributes.
        instrument.setId(instrumentWS.getId());
        instrument.setSymbol(instrumentWS.getSymbol());
        instrument.setType(instrumentWS.getType());
        instrument.setStockExchange(instrumentWS.getStockExchange());
        instrument.setName(instrumentWS.getName());
        instrument.setCompanyPathInvestingCom(instrumentWS.getCompanyPathInvestingCom());

        // Object references.
        if (instrumentWS.getSectorId() != null) {
            instrument.setSector(this.instrumentDAO.getInstrument(instrumentWS.getSectorId()));
        }

        if (instrumentWS.getIndustryGroupId() != null) {
            instrument.setIndustryGroup(this.instrumentDAO.getInstrument(instrumentWS.getIndustryGroupId()));
        }

        if (instrumentWS.getDividendId() != null) {
            instrument.setDividend(this.instrumentDAO.getInstrument(instrumentWS.getDividendId()));
        }

        if (instrumentWS.getDivisorId() != null) {
            instrument.setDivisor(this.instrumentDAO.getInstrument(instrumentWS.getDivisorId()));
        }

        return instrument;
    }

    /**
     * Validates the given Instrument and adds potential error messages to the given WebServiceResult.
     *
     * @param instrument       The Instrument to be validated.
     * @param webServiceResult The WebServiceResult containing potential validation error messages.
     */
    private void validateInstrument(final Instrument instrument, final WebServiceResult webServiceResult) {
        try {
            instrument.validate();
        } catch (LocalizedException localizedException) {
            webServiceResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, localizedException.getLocalizedMessage()));
        } catch (Exception validationException) {
            webServiceResult
                    .addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
        }
    }

    /**
     * Deletes referenced objects of the Instrument with the given ID.
     *
     * @param id The id of the instrument to be deleted.
     * @throws Exception In case reading or deleting referenced objects failed.
     */
    private void deleteReferencedObjects(final Integer id) throws Exception {
        List<Quotation> quotationsOfInstrument = new ArrayList<>();
        List<HorizontalLine> horizontalLinesOfInstrument = new ArrayList<>();

        // If the Instrument has any quotations referenced, delete those first before deleting the Instrument.
        quotationsOfInstrument = this.quotationDAO.getQuotationsOfInstrument(id);
        if (quotationsOfInstrument.size() > 0) {
            this.quotationDAO.deleteQuotations(quotationsOfInstrument);
        }

        // If the Instrument has any horizontal lines referenced, delete those first before deleting the Instrument.
        horizontalLinesOfInstrument = this.chartObjectDAO.getHorizontalLines(id);
        for (HorizontalLine horizontalLine : horizontalLinesOfInstrument) {
            this.chartObjectDAO.deleteHorizontalLine(horizontalLine);
        }

    }

    /**
     * Handling of the ObjectInUseException during deletion of an Instrument.
     *
     * @param id                     The Instrument id.
     * @param objectInUseException   The ObjectInUseException.
     * @param deleteInstrumentResult The WebServiceResult of the delete operation.
     */
    private void handleObjectInUseDeletion(final Integer id, final ObjectInUseException objectInUseException,
            final WebServiceResult deleteInstrumentResult) {
        if (objectInUseException.getUsedByObject() instanceof backend.model.list.List) {
            deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, MessageFormat.format(
                    this.resources.getString("instrument.deleteUsedInList"), id, objectInUseException.getUsedById())));
        } else if (objectInUseException.getUsedByObject() instanceof PriceAlert) {
            deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("instrument.deleteUsedInPriceAlert"), id,
                            objectInUseException.getUsedById())));
        } else if (objectInUseException.getUsedByObject() instanceof Instrument) {
            deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("instrument.deleteUsedInInstrument"), id,
                            objectInUseException.getUsedById())));
        } else if (objectInUseException.getUsedByObject() instanceof Quotation) {
            deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("instrument.deleteError"), id)));

            // This is an error the user can not fix. Therefore it is logged.
            LOGGER.error(MessageFormat.format(this.resources.getString("instrument.deleteUsedInQuotation"), id));
        } else if (objectInUseException.getUsedByObject() instanceof HorizontalLine) {
            deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("instrument.deleteError"), id)));

            // This is an error the user can not fix. Therefore it is logged.
            LOGGER.error(MessageFormat.format(this.resources.getString("instrument.deleteUsedInHorizontalLine"), id));
        }
    }
}
