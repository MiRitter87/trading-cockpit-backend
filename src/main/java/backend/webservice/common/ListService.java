package backend.webservice.common;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import backend.controller.ExcelExportController;
import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.list.ListDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.dao.scan.ScanDAO;
import backend.model.ObjectInUseException;
import backend.model.instrument.Instrument;
import backend.model.instrument.QuotationArray;
import backend.model.list.List;
import backend.model.list.ListArray;
import backend.model.list.ListWS;
import backend.model.scan.Scan;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;

/**
 * Common implementation of the list WebService that can be used by multiple service interfaces like SOAP or REST.
 *
 * @author Michael
 */
public class ListService {
    /**
     * DAO for List access.
     */
    private ListDAO listDAO;

    /**
     * DAO for Instrument access.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * DAO for Scan access.
     */
    private ScanDAO scanDAO;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(ListService.class);

    /**
     * Initializes the list service.
     */
    public ListService() {
        this.listDAO = DAOManager.getInstance().getListDAO();
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        this.scanDAO = DAOManager.getInstance().getScanDAO();
    }

    /**
     * Provides the list with the given id.
     *
     * @param id The id of the list.
     * @return The list with the given id, if found.
     */
    public WebServiceResult getList(final Integer id) {
        List list = null;
        WebServiceResult getListResult = new WebServiceResult(null);

        try {
            list = this.listDAO.getList(id);

            if (list != null) {
                // List found
                getListResult.setData(list);
            } else {
                // List not found
                getListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                        MessageFormat.format(this.resources.getString("list.notFound"), id)));
            }
        } catch (Exception e) {
            getListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("list.getError"), id)));

            LOGGER.error(MessageFormat.format(this.resources.getString("list.getError"), id), e);
        }

        return getListResult;
    }

    /**
     * Provides a list of all lists.
     *
     * @return A list of all lists.
     */
    public WebServiceResult getLists() {
        ListArray lists = new ListArray();
        WebServiceResult getListsResult = new WebServiceResult(null);

        try {
            lists.setLists(this.listDAO.getLists());
            getListsResult.setData(lists);
        } catch (Exception e) {
            getListsResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("list.getListsError")));

            LOGGER.error(this.resources.getString("list.getListsError"), e);
        }

        return getListsResult;
    }

    /**
     * Deletes the list with the given id.
     *
     * @param id The id of the list to be deleted.
     * @return The result of the delete function.
     */
    public WebServiceResult deleteList(final Integer id) {
        WebServiceResult deleteListResult = new WebServiceResult(null);
        List list = null;

        // Check if a list with the given id exists.
        try {
            list = this.listDAO.getList(id);

            if (list != null) {
                // Delete list if exists.
                this.listDAO.deleteList(list);
                deleteListResult.addMessage(new WebServiceMessage(WebServiceMessageType.S,
                        MessageFormat.format(this.resources.getString("list.deleteSuccess"), id)));
            } else {
                // List not found.
                deleteListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                        MessageFormat.format(this.resources.getString("list.notFound"), id)));
            }
        } catch (ObjectInUseException objectInUseException) {
            deleteListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, MessageFormat.format(
                    this.resources.getString("list.deleteUsedInScan"), id, objectInUseException.getUsedById())));
        } catch (Exception e) {
            deleteListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("list.deleteError"), id)));

            LOGGER.error(MessageFormat.format(this.resources.getString("list.deleteError"), id), e);
        }

        return deleteListResult;
    }

    /**
     * Updates an existing list.
     *
     * @param list The list to be updated.
     * @return The result of the update function.
     */
    public WebServiceResult updateList(final ListWS list) {
        List convertedList = new List();
        WebServiceResult updateListResult = new WebServiceResult(null);

        // Convert the WebService data transfer object to the internal data model.
        try {
            convertedList = this.convertList(list);
        } catch (Exception exception) {
            updateListResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("list.updateError")));
            LOGGER.error(this.resources.getString("list.updateError"), exception);
            return updateListResult;
        }

        // Validation of the given list.
        try {
            convertedList.validate();
        } catch (Exception validationException) {
            updateListResult
                    .addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
            return updateListResult;
        }

        // Update list if validation is successful.
        updateListResult = this.update(convertedList);

        return updateListResult;
    }

    /**
     * Adds a list.
     *
     * @param list The list to be added.
     * @return The result of the add function.
     */
    public WebServiceResult addList(final ListWS list) {
        List convertedList = new List();
        WebServiceResult addListResult = new WebServiceResult();

        // Convert the WebService data transfer object to the internal data model.
        try {
            convertedList = this.convertList(list);
        } catch (Exception exception) {
            addListResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("list.addError")));
            LOGGER.error(this.resources.getString("list.addError"), exception);
            return addListResult;
        }

        // Validate the given list.
        try {
            convertedList.validate();
        } catch (Exception validationException) {
            addListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
            return addListResult;
        }

        // Insert list if validation is successful.
        try {
            this.listDAO.insertList(convertedList);
            addListResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.S, this.resources.getString("list.addSuccess")));
            addListResult.setData(convertedList.getId());
        } catch (Exception e) {
            addListResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("list.addError")));
            LOGGER.error(this.resources.getString("list.addError"), e);
        }

        return addListResult;
    }

    /**
     * Determines the most recent Quotation of each Instrument contained in the List with the given id. An Excel file is
     * generated that contains Symbol, Date, Price and RS Number of each Quotation.
     *
     * @param id The id of the List.
     * @return A Response containing the generated Excel file.
     */
    public Response getRecentPricesOfListAsExcel(final Integer id) {
        QuotationDAO quotationDAO = DAOManager.getInstance().getQuotationDAO();
        List list;
        ExcelExportController excelExportController = new ExcelExportController();
        Workbook workbook;
        StreamingOutput streamingOutput;
        QuotationArray quotationsOfList = new QuotationArray();

        try {
            // Get the List with the given ID and get the most recent Quotation of each Instrument.
            list = listDAO.getList(id);
            quotationsOfList.setQuotations(quotationDAO.getRecentQuotationsForList(list));
            quotationsOfList.sortQuotationsBySymbol();

            // Generate the Excel workbook with Quotation data.
            workbook = excelExportController.getQuotationDataWorkbook(quotationsOfList.getQuotations());

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    workbook.write(output);
                    output.flush();
                    workbook.close();
                }
            };
        } catch (Exception exception) {
            LOGGER.error(MessageFormat.format(this.resources.getString("list.getExcelError"), id), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM).build();
    }

    /**
     * Converts the lean List representation that is provided by the WebService to the internal data model for further
     * processing.
     *
     * @param listWS The lean list representation provided by the WebService.
     * @return The List model that is used by the backend internally.
     * @throws Exception In case the conversion fails.
     */
    private List convertList(final ListWS listWS) throws Exception {
        List list = new List();
        Instrument instrument;

        // Simple object attributes.
        list.setId(listWS.getId());
        list.setName(listWS.getName());
        list.setDescription(listWS.getDescription());

        // Convert the instrument IDs into instrument objects.
        for (Integer instrumentId : listWS.getInstrumentIds()) {
            instrument = this.instrumentDAO.getInstrument(instrumentId);
            list.addInstrument(instrument);
        }

        return list;
    }

    /**
     * Updates the given List.
     *
     * @param list The List to be updated.
     * @return The result of the update function.
     */
    private WebServiceResult update(final List list) {
        WebServiceResult updateListResult = new WebServiceResult();

        try {
            this.listDAO.updateList(list);
            this.updateRelatedScans(list);
            updateListResult.addMessage(new WebServiceMessage(WebServiceMessageType.S,
                    MessageFormat.format(this.resources.getString("list.updateSuccess"), list.getId())));
        } catch (ObjectUnchangedException objectUnchangedException) {
            updateListResult.addMessage(new WebServiceMessage(WebServiceMessageType.I,
                    MessageFormat.format(this.resources.getString("list.updateUnchanged"), list.getId())));
        } catch (Exception e) {
            updateListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    MessageFormat.format(this.resources.getString("list.updateError"), list.getId())));

            LOGGER.error(MessageFormat.format(this.resources.getString("list.updateError"), list.getId()), e);
        }

        return updateListResult;
    }

    /**
     * Updates scans that are related to the given List.
     *
     * Instruments being removed from a List can affect the incomplete instruments of a Scan. Therefore all scans have
     * to be updated that use the given List.
     *
     * @param list The List.
     * @throws Exception The update of related scans failed.
     */
    private void updateRelatedScans(final List list) throws Exception {
        java.util.List<Scan> scans = this.scanDAO.getScans();

        for (Scan tempScan : scans) {
            if (tempScan.hasListWithId(list.getId())) {
                try {
                    this.scanDAO.updateScan(tempScan);
                } catch (ObjectUnchangedException objectUnchangedException) {
                    // Nothing to do here.
                    // This might be the case if an Instrument is removed from a List but the same Instrument is part of
                    // the Scan via another list.
                    // So no changes of the incomplete instruments take place.
                }
            }
        }
    }
}
