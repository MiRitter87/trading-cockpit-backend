package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.list.ListDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.list.List;
import backend.model.list.ListArray;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;

/**
 * Tests the list service.
 * 
 * @author Michael
 */
public class ListServiceTest {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");	
	
	/**
	 * DAO to access instrument data.
	 */
	private static InstrumentDAO instrumentDAO;
	
	/**
	 * DAO to access list data.
	 */
	private static ListDAO listDAO;	
	
	/**
	 * The stock of Microsoft.
	 */
	private Instrument microsoftStock;
	
	/**
	 * The stock of Amazon.
	 */
	private Instrument amazonStock;
	
	/**
	 * A list containing a single instrument.
	 */
	private List singleInstrumentList;
	
	/**
	 * A list containing multiple instruments.
	 */
	private List multiInstrumentList;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
		listDAO = DAOManager.getInstance().getListDAO();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		try {
			DAOManager.getInstance().close();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		this.createDummyInstruments();
		this.createDummyLists();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.deleteDummyLists();
		this.deleteDummyInstruments();
	}
	
	
	/**
	 * Initializes the database with dummy instruments.
	 */
	private void createDummyInstruments() {
		this.microsoftStock = this.getMicrosoftStock();
		this.amazonStock = this.getAmazonStock();
		
		try {
			instrumentDAO.insertInstrument(this.microsoftStock);
			instrumentDAO.insertInstrument(this.amazonStock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy instruments from the database.
	 */
	private void deleteDummyInstruments() {
		try {
			instrumentDAO.deleteInstrument(this.amazonStock);
			instrumentDAO.deleteInstrument(this.microsoftStock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Initializes the database with dummy lists.
	 */
	private void createDummyLists() {
		this.singleInstrumentList = this.getSingleInstrumentList();
		this.multiInstrumentList = this.getMultipleInstrumentList();
		
		try {
			listDAO.insertList(this.singleInstrumentList);
			listDAO.insertList(this.multiInstrumentList);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy lists from the database.
	 */
	private void deleteDummyLists() {
		try {
			listDAO.deleteList(this.multiInstrumentList);
			listDAO.deleteList(this.singleInstrumentList);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Gets the instrument of the Microsoft stock.
	 * 
	 * @return The instrument of the Microsoft stock.
	 */
	private Instrument getMicrosoftStock() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("MSFT");
		instrument.setName("Microsoft");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets the instrument of the Amazon stock.
	 * 
	 * @return The instrument of the Amazon stock.
	 */
	private Instrument getAmazonStock() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("AMZN");
		instrument.setName("Amazon");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets a list containing a single instrument.
	 * 
	 * @return A list containing a single instrument.
	 */
	private List getSingleInstrumentList() {
		List list = new List();
		
		list.setName("Single instrument");
		list.setDescription("Contains a single instrument.");
		list.addInstrument(this.amazonStock);
		
		return list;
	}
	
	
	/**
	 * Gets a list containing multiple instruments.
	 * 
	 * @return A list containing multiple instruments.
	 */
	private List getMultipleInstrumentList() {
		List list = new List();
		
		list.setName("Multiple instruments");
		list.setDescription("Contains multiple instruments.");
		list.addInstrument(this.amazonStock);
		list.addInstrument(this.microsoftStock);
		
		return list;
	}
	
	
	@Test
	/**
	 * Tests the retrieval of a list.
	 */
	public void testGetList() {
		WebServiceResult getListResult;
		List list;
		Instrument instrument;
		Iterator<Instrument> instrumentIterator;
		
		//Get the list.
		ListService service = new ListService();
		getListResult = service.getList(this.multiInstrumentList.getId());
		
		//Assure no error message exists.
		assertTrue(WebServiceTools.resultContainsErrorMessage(getListResult) == false);
		
		//Assure that a list is returned.
		assertTrue(getListResult.getData() instanceof List);
		
		list = (List) getListResult.getData();
		
		//Check each attribute of the list.
		assertEquals(this.multiInstrumentList.getId(), list.getId());
		assertEquals(this.multiInstrumentList.getName(), list.getName());
		assertEquals(this.multiInstrumentList.getDescription(), list.getDescription());
		
		//The returned list should have two instruments.
		assertEquals(this.multiInstrumentList.getInstruments().size(), list.getInstruments().size());
		
		//Check the attributes of the instruments.
		instrumentIterator = list.getInstruments().iterator();
		while(instrumentIterator.hasNext()) {
			instrument = instrumentIterator.next();
			
			if(instrument.getId().equals(this.amazonStock.getId())) {
				assertEquals(this.amazonStock.getId(), instrument.getId());
				assertEquals(this.amazonStock.getSymbol(), instrument.getSymbol());
				assertEquals(this.amazonStock.getName(), instrument.getName());
				assertEquals(this.amazonStock.getStockExchange(), instrument.getStockExchange());
				assertEquals(this.amazonStock.getType(), instrument.getType());
			}
			else if(instrument.getId().equals(this.microsoftStock.getId())) {
				assertEquals(this.microsoftStock.getId(), instrument.getId());
				assertEquals(this.microsoftStock.getSymbol(), instrument.getSymbol());
				assertEquals(this.microsoftStock.getName(), instrument.getName());
				assertEquals(this.microsoftStock.getStockExchange(), instrument.getStockExchange());
				assertEquals(this.microsoftStock.getType(), instrument.getType());
			}
			else {
				fail("The list contains an unrelated instrument.");
			}
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of a list with an id that is unknown.
	 */
	public void testGetListWithUnknownId() {
		WebServiceResult getListResult;
		final Integer unknownListId = 0;
		String expectedErrorMessage, actualErrorMessage;
		
		//Get the list.
		ListService service = new ListService();
		getListResult = service.getList(unknownListId);
		
		//Assure that no list is returned
		assertNull(getListResult.getData());
				
		//There should be a return message of type E.
		assertTrue(getListResult.getMessages().size() == 1);
		assertTrue(getListResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//Verify the expected error message.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("list.notFound"), unknownListId);
		actualErrorMessage = getListResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all lists.
	 */
	public void testGetAllLists() {
		WebServiceResult getListsResult;
		ListArray lists;
		List list;
		Instrument instrument;
		Iterator<Instrument> instrumentIterator;
		
		//Get the lists.
		ListService service = new ListService();
		getListsResult = service.getLists();
		lists = (ListArray) getListsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getListsResult) == false);
		
		//Check if two lists are returned.
		assertEquals(2, lists.getLists().size());
		
		//Check all lists by each attribute.
		//First list
		list = lists.getLists().get(0);
		assertEquals(this.singleInstrumentList.getId(), list.getId());
		assertEquals(this.singleInstrumentList.getName(), list.getName());
		assertEquals(this.singleInstrumentList.getDescription(), list.getDescription());
		
		//The list should have one instrument.
		assertEquals(this.singleInstrumentList.getInstruments().size(), list.getInstruments().size());
		
		//Check the attributes of the instrument.
		instrument = list.getInstruments().iterator().next();
		assertEquals(this.amazonStock.getId(), instrument.getId());
		assertEquals(this.amazonStock.getSymbol(), instrument.getSymbol());
		assertEquals(this.amazonStock.getName(), instrument.getName());
		assertEquals(this.amazonStock.getStockExchange(), instrument.getStockExchange());
		assertEquals(this.amazonStock.getType(), instrument.getType());
		
		//Second list
		list = lists.getLists().get(1);
		assertEquals(this.multiInstrumentList.getId(), list.getId());
		assertEquals(this.multiInstrumentList.getName(), list.getName());
		assertEquals(this.multiInstrumentList.getDescription(), list.getDescription());
		
		//The list should have two instruments.
		assertEquals(this.multiInstrumentList.getInstruments().size(), list.getInstruments().size());
		
		instrumentIterator = list.getInstruments().iterator();
		while(instrumentIterator.hasNext()) {
			instrument = instrumentIterator.next();
			
			if(instrument.getId().equals(this.amazonStock.getId())) {
				assertEquals(this.amazonStock.getId(), instrument.getId());
				assertEquals(this.amazonStock.getSymbol(), instrument.getSymbol());
				assertEquals(this.amazonStock.getName(), instrument.getName());
				assertEquals(this.amazonStock.getStockExchange(), instrument.getStockExchange());
				assertEquals(this.amazonStock.getType(), instrument.getType());
			}
			else if(instrument.getId().equals(this.microsoftStock.getId())) {
				assertEquals(this.microsoftStock.getId(), instrument.getId());
				assertEquals(this.microsoftStock.getSymbol(), instrument.getSymbol());
				assertEquals(this.microsoftStock.getName(), instrument.getName());
				assertEquals(this.microsoftStock.getStockExchange(), instrument.getStockExchange());
				assertEquals(this.microsoftStock.getType(), instrument.getType());
			}
			else {
				fail("The list contains an unrelated instrument.");
			}
		}
	}
	
	
	@Test
	/**
	 * Tests deletion of a list.
	 */
	public void testDeleteList() {
		WebServiceResult deleteListResult;
		List deletedList;
		
		try {
			//Delete list using the service.
			ListService service = new ListService();
			deleteListResult = service.deleteList(this.singleInstrumentList.getId());
			
			//There should be no error messages
			assertTrue(WebServiceTools.resultContainsErrorMessage(deleteListResult) == false);
			
			//There should be a success message
			assertTrue(deleteListResult.getMessages().size() == 1);
			assertTrue(deleteListResult.getMessages().get(0).getType() == WebServiceMessageType.S);
			
			//Check if the list is missing using the DAO.
			deletedList = listDAO.getList(this.singleInstrumentList.getId());
			
			if(deletedList != null)
				fail("The single instrument list is still persisted but should have been deleted by the WebService operation 'deleteList'.");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Restore old database state by adding the list that has been deleted previously.
			try {
				this.singleInstrumentList = this.getSingleInstrumentList();
				listDAO.insertList(this.singleInstrumentList);
			} 
			catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
	
	
	@Test
	/**
	 * Tests deletion of a list with an unknown ID.
	 */
	public void testeDeleteListWithUnknownId() {
		WebServiceResult deleteListResult;
		final Integer unknownListId = 0;
		String expectedErrorMessage, actualErrorMessage;
		
		//Delete the list.
		ListService service = new ListService();
		deleteListResult = service.deleteList(unknownListId);
		
		//There should be a return message of type E.
		assertTrue(deleteListResult.getMessages().size() == 1);
		assertTrue(deleteListResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//Verify the expected error message.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("list.notFound"), unknownListId);
		actualErrorMessage = deleteListResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests updating a list with valid data.
	 */
	public void testUpdateValidList() {
		WebServiceResult updateListResult;
		List updatedList;
		ListService service = new ListService();
		
		//Update the name.
		this.singleInstrumentList.setName("Single instrument - Updated name");
		updateListResult = service.updateList(this.singleInstrumentList);
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(updateListResult) == false);
		
		//There should be a success message
		assertTrue(updateListResult.getMessages().size() == 1);
		assertTrue(updateListResult.getMessages().get(0).getType() == WebServiceMessageType.S);
		
		//Retrieve the updated list and check if the changes have been persisted.
		try {
			updatedList = listDAO.getList(this.singleInstrumentList.getId());
			assertEquals(this.singleInstrumentList.getName(), updatedList.getName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
