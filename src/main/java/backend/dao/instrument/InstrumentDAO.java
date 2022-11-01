package backend.dao.instrument;

import java.util.List;

import backend.dao.ObjectUnchangedException;
import backend.model.ObjectInUseException;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;

/**
 * Interface for instrument persistence.
 * 
 * @author Michael
 */
public interface InstrumentDAO {
	/**
	 * Inserts an instrument.
	 * 
	 * @param intrument The instrument to be inserted.
	 * @throws DuplicateInstrumentException In case the instrument already exists.
	 * @throws Exception Insertion failed.
	 */
	void insertInstrument(final Instrument instrument) throws DuplicateInstrumentException, Exception;
	
	
	/**
	 * Deletes an instrument.
	 * 
	 * @param instrument The instrument to be deleted.
	 * @throws ObjectInUseException The Instrument is still used by other objects and can't be deleted.
	 * @throws Exception Deletion failed.
	 */
	void deleteInstrument(final Instrument instrument) throws ObjectInUseException, Exception;
	
	
	/**
	 * Gets all instruments of the given InstrumentType.
	 * 
	 * @param instrumentType The type of the instruments requested.
	 * @return All instruments.
	 * @throws Exception Instrument retrieval failed.
	 */
	List<Instrument> getInstruments(final InstrumentType instrumentType) throws Exception;
	
	
	/**
	 * Gets the instrument with the given id.
	 * 
	 * @param id The id of the instrument.
	 * @return The instrument with the given id.
	 * @throws Exception Instrument retrieval failed.
	 */
	Instrument getInstrument(final Integer id) throws Exception;
	
	
	/**
	 * Updates the given instrument.
	 * 
	 * @param instrument The instrument to be updated.
	 * @throws ObjectUnchangedException In case the data of the instrument have not been changed.
	 * @throws DuplicateInstrumentException In case the instrument already exists.
	 * @throws Exception Instrument update failed.
	 */
	void updateInstrument(final Instrument instrument) throws ObjectUnchangedException, DuplicateInstrumentException, Exception;
}
