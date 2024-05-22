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
     * @throws Exception                   Insertion failed.
     */
    void insertStatistic(Statistic statistic) throws DuplicateStatisticException, Exception;

    /**
     * Deletes a Statistic.
     *
     * @param statistic The Statistic to be deleted.
     * @throws Exception Deletion failed.
     */
    void deleteStatistic(Statistic statistic) throws Exception;

    /**
     * Gets all statistics of the given InstrumentType. This includes all statistics irrespective of the sector or
     * industry group they belong to.
     *
     * @param instrumentType The type of the statistics requested.
     * @return All statistics.
     * @throws Exception Statistics retrieval failed.
     */
    List<Statistic> getStatisticsOfInstrumentType(InstrumentType instrumentType) throws Exception;

    /**
     * Gets all statistics of the given InstrumentType. By specifying the id of the sector or industry group only those
     * statistics are returned that represent all instruments of the sector or industry group with the given id. If no
     * id of sector or industry group is provided, the general statistics of all instruments are returned.
     *
     * @param instrumentType  The type of the statistics requested.
     * @param sectorId        The id of the sector the statistics belong to (can be null).
     * @param industryGroupId The id of the industry group the statistics belong to (can be null).
     * @return The statistics that match the criteria.
     * @throws Exception Statistics retrieval failed.
     */
    List<Statistic> getStatistics(InstrumentType instrumentType, Integer sectorId, Integer industryGroupId)
            throws Exception;

    /**
     * Gets the Statistic with the given id.
     *
     * @param id The id of the Statistic.
     * @return The Statistic with the given id.
     * @throws Exception Statistic retrieval failed.
     */
    Statistic getStatistic(Integer id) throws Exception;

    /**
     * Updates the given Statistic.
     *
     * @param statistic The Statistic to be updated.
     * @throws ObjectUnchangedException In case the data of the Statistic have not been changed.
     * @throws Exception                Statistic update failed.
     */
    void updateStatistic(Statistic statistic) throws ObjectUnchangedException, Exception;
}
