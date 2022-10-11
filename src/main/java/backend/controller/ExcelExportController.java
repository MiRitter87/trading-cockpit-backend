package backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import backend.model.instrument.Quotation;



/**
 * Controller for generation of Excel files.
 * 
 * @author Michael
 */
public class ExcelExportController {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	
	/**
	 * Generates a Workbook that contains price data of the given quotations.
	 * The workbook contains the following data for each quotation: Symbol, Date, Price.
	 * 
	 * @param quotations A List of quotations.
	 * @return A Workbook containing price data of the given quotations.
	 */
	public Workbook getPriceDataOfQuotations(final List<Quotation> quotations) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row header = sheet.createRow(0);
		Cell headerCell = header.createCell(0);
		
		headerCell.setCellValue(this.resources.getString("instrument.attribute.symbol"));
		
		return workbook;
	}
	
	
	/**
	 * Reads the given Workbook and returns its content.
	 * 
	 * @param workbook The Workbook to read.
	 * @return The content.
	 */
	public Map<Integer, List<String>> readWorkbook(final Workbook workbook) {
		Sheet sheet = workbook.getSheetAt(0);
		Map<Integer, List<String>> data = new HashMap<>();
		int i = 0;
		
		for (Row row:sheet) {
			data.put(i, new ArrayList<String>());
			
			for (Cell cell : row) {
				switch (cell.getCellType()) {
					case STRING:
						data.get(i).add(cell.getRichStringCellValue().getString());
						break;
					case NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
						    data.get(i).add(cell.getDateCellValue() + "");
						} else {
						    data.get(i).add(cell.getNumericCellValue() + "");
						}
						break;
					default: 
						data.get(i).add(" ");
				}
			}
			
			i++;
		}
		
		return data;
	}
}
