package backend.dao.statistic;

import java.util.List;

import backend.dao.ObjectUnchangedException;
import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Interface for Statistic persistence.
 * 
 * @author Michael
 */
public interface StatisticDAO {
	/**
	 * Inserts a Statistic.
	 * 
	 * @param statistic The Statistic to be inserted.
	 * @throws DuplicateStatisticException In case the Statistic already exists.
	 * @throws Exception Insertion failed.
	 */
	void insertStatistic(final Statistic statistic) throws DuplicateStatisticException, Exception;
	
	
	/**
	 * Deletes a Statistic.
	 * 
	 * @param statistic The Statistic to be deleted.
	 * @throws Exception Deletion failed.
	 */
	void deleteStatistic(final Statistic statistic) throws Exception;
	
	
	/**
	 * Gets all statistics of the given InstrumentType.
	 * 
	 * @param instrumentType The type of the statistics requested.
	 * @return All statistics.
	 * @throws Exception Statistic retrieval failed.
	 */
	List<Statistic> getStatistics(final InstrumentType instrumentType) throws Exception;
	
	
	/**
	 * Gets the Statistic with the given id.
	 * 
	 * @param id The id of the Statistic.
	 * @return The Statistic with the given id.
	 * @throws Exception Statistic retrieval failed.
	 */
	Statistic getStatistic(final Integer id) throws Exception;
	
	
	/**
	 * Updates the given Statistic.
	 * 
	 * @param statistic The Statistic to be updated.
	 * @throws ObjectUnchangedException In case the data of the Statistic have not been changed.
	 * @throws Exception Statistic update failed.
	 */
	void updateStatistic(final Statistic statistic) throws ObjectUnchangedException, Exception;
}
