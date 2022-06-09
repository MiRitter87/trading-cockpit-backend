package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.list.ListDAO;
import backend.model.list.List;
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
//				//Instrument not found
//				getInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
//						MessageFormat.format(this.resources.getString("instrument.notFound"), id)));
			}
		}
		catch (Exception e) {
			getListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("list.getError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("list.getError"), id), e);
		}
		
		return getListResult;
	}
}
