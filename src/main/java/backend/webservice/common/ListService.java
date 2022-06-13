package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.list.ListDAO;
import backend.model.list.List;
import backend.model.list.ListArray;
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
	 * DAO for list access.
	 */
	private ListDAO listDAO;
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(ListService.class);
	
	
	/**
	 * Initializes the list service.
	 */
	public ListService() {
		this.listDAO = DAOManager.getInstance().getListDAO();
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
			
			if(list != null) {
				//List found
				getListResult.setData(list);
			}
			else {
				//List not found
				getListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("list.notFound"), id)));
			}
		}
		catch (Exception e) {
			getListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("list.getError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("list.getError"), id), e);
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
			getListsResult.addMessage(new WebServiceMessage(
					WebServiceMessageType.E, this.resources.getString("list.getListsError")));
			
			logger.error(this.resources.getString("list.getListsError"), e);
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
		
		//Check if a list with the given id exists.
		try {
			list = this.listDAO.getList(id);
			
			if(list != null) {
				//Delete list if exists.
				this.listDAO.deleteList(list);
				deleteListResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
						MessageFormat.format(this.resources.getString("list.deleteSuccess"), id)));
			}
			else {
				//List not found.
				deleteListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("list.notFound"), id)));
			}
		}
		catch (Exception e) {
			deleteListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("list.deleteError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("list.deleteError"), id), e);
		}
		
		return deleteListResult;
	}
	
	
	/**
	 * Updates an existing list.
	 * 
	 * @param list The list to be updated.
	 * @return The result of the update function.
	 */
	public WebServiceResult updateList(final List list) {
		WebServiceResult updateListResult = new WebServiceResult(null);
		
		//Validation of the given list.
		try {
			list.validate();
		} catch (Exception validationException) {
			updateListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return updateListResult;
		}
		
		//Update list if validation is successful.
		try {
			this.listDAO.updateList(list);
			updateListResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
					MessageFormat.format(this.resources.getString("list.updateSuccess"), list.getId())));
		} 
		catch(ObjectUnchangedException objectUnchangedException) {
			updateListResult.addMessage(new WebServiceMessage(WebServiceMessageType.I, 
					MessageFormat.format(this.resources.getString("list.updateUnchanged"), list.getId())));
		}
		catch (Exception e) {
			updateListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
					MessageFormat.format(this.resources.getString("list.updateError"), list.getId())));
			
			logger.error(MessageFormat.format(this.resources.getString("list.updateError"), list.getId()), e);
		}
		
		return updateListResult;
	}
	
	
	/**
	 * Adds a list.
	 * 
	 * @param list The list to be added.
	 * @return The result of the add function.
	 */
	public WebServiceResult addList(final List list) {
		WebServiceResult addListResult = new WebServiceResult();
		
		//Validate the given list.
		try {
			list.validate();
		} catch (Exception validationException) {
			addListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return addListResult;
		}
		
		//Insert instrument if validation is successful.
		try {
			this.listDAO.insertList(list);
			addListResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, this.resources.getString("list.addSuccess")));
			addListResult.setData(list.getId());
		} 
		catch (Exception e) {
			addListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("list.addError")));
			logger.error(this.resources.getString("list.addError"), e);
		}
		
		return addListResult;
	}
}
