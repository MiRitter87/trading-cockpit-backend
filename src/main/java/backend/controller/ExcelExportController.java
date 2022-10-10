package backend.controller;

import org.apache.poi.ss.usermodel.Workbook;

import backend.model.list.List;

/**
 * Controller for generation of Excel files.
 * 
 * @author Michael
 */
public class ExcelExportController {
	/**
	 * Generates a Workbook that contains the most recent price of each Instrument of the given List.
	 * 
	 * @param list The List of instruments.
	 * @return A Workbook containing the most recent price of each Instrument.
	 */
	public Workbook getRecentPricesOfList(final List list) {
		return null;
	}
}
