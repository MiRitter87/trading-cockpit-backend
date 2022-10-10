package backend.controller;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import backend.model.instrument.Quotation;



/**
 * Controller for generation of Excel files.
 * 
 * @author Michael
 */
public class ExcelExportController {
	/**
	 * Generates a Workbook that contains price data of the given quotations.
	 * The workbook contains the following data for each quotation: Symbol, Date, Price.
	 * 
	 * @param quotations A List of quotations.
	 * @return A Workbook containing price data of the given quotations.
	 */
	public Workbook getPriceDataOfQuotations(final List<Quotation> quotations) {
		return null;
	}
}
