package backend.controller;

import java.util.Date;

import backend.model.protocol.Protocol;

/**
 * Performs health checks for an Instrument.
 * 
 * @author Michael
 */
public class InstrumentCheckController {
	/**
	 * Checks the health of the given Instrument beginning at the given start date.
	 * 
	 * @param instrumentId The id of the Instrument.
	 * @param startDate The start date of the health check.
	 * @return A protocol containing the health information from the start date until the most recent quotation.
	 * @throws Exception Health check failed.
	 */
	public Protocol checkInstrument(final Integer instrumentId, final Date startDate) throws Exception {
		return null;
	}
}
