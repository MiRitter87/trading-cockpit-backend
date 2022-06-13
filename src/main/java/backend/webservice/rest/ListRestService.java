package backend.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import backend.model.list.List;
import backend.model.webservice.WebServiceResult;
import backend.webservice.common.ListService;

/**
 * WebService for list access using REST technology.
 * 
 * @author Michael
 */
@Path("/lists")
public class ListRestService {
	/**
	 * Provides the list with the given ID.
	 * 
	 * @param id The ID of the list.
	 * @return The list with the given ID.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult getList(@PathParam("id") final Integer id) {
		ListService listService = new ListService();
		return listService.getList(id);
	}
	
	
	/**
	 * Provides a list of all lists.
	 * 
	 * @return A list of all lists.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult getLists() {
		ListService listService = new ListService();
		return listService.getLists();
	}
	
	
	/**
	 * Adds a list.
	 * 
	 * @param list The list to be added.
	 * @return The result of the add function.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult addList(final List list) {
		ListService listService = new ListService();
		return listService.addList(list);
	}
	
	
	/**
	 * Updates an existing list.
	 * 
	 * @param list The list to be updated.
	 * @return The result of the update function.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult updateList(final List list) {
		ListService listService = new ListService();
		return listService.updateList(list);
	}
	
	
	/**
	 * Deletes the list with the given id.
	 * 
	 * @param id The id of the list to be deleted.
	 * @return The result of the delete function.
	 */
	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult deleteList(@PathParam("id") final Integer id) {
		ListService listService = new ListService();
		return listService.deleteList(id);
	}
}
