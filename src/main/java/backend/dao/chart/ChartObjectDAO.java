package backend.dao.chart;

import java.util.List;

import backend.dao.ObjectUnchangedException;
import backend.model.chart.HorizontalLine;

/**
 * Interface for chart object persistence. A chart object can be a HorizontalLine.
 *
 * @author Michael
 */
public interface ChartObjectDAO {
    /**
     * Inserts a HorizontalLine.
     *
     * @param horizontalLine The HorizontalLine to be inserted.
     * @throws Exception Insertion failed.
     */
    void insertHorizontalLine(HorizontalLine horizontalLine) throws Exception;

    /**
     * Deletes a HorizontalLine.
     *
     * @param horizontalLine The HorizontalLine to be deleted.
     * @throws Exception Deletion failed.
     */
    void deleteHorizontalLine(HorizontalLine horizontalLine) throws Exception;

    /**
     * Gets all horizontal lines of the Instrument with the given ID. If not Instrument ID is provided, all horizontal
     * lines are retrieved.
     *
     * @param instrumentId The ID of the Instrument whose horizontal lines are requested. Null is allowed.
     * @return All horizontal lines.
     * @throws Exception HorizontalLine retrieval failed.
     */
    List<HorizontalLine> getHorizontalLines(Integer instrumentId) throws Exception;

    /**
     * Gets the HorizontalLine with the given id.
     *
     * @param id The id of the HorizontalLine.
     * @return The HorizontalLine with the given id.
     * @throws Exception HorizontalLine retrieval failed.
     */
    HorizontalLine getHorizontalLine(Integer id) throws Exception;

    /**
     * Updates the given HorizontalLine.
     *
     * @param horizontalLine The HorizontalLine to be updated.
     * @throws ObjectUnchangedException In case the data of the HorizontalLine have not been changed.
     * @throws Exception                HorizontalLine update failed.
     */
    void updateHorizontalLine(HorizontalLine horizontalLine) throws ObjectUnchangedException, Exception;
}
