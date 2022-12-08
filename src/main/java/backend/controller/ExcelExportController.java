package backend.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
		Row tableRow;
		Cell tableCell;
		int rowNumber;
		
		//Define the table header cells.
		tableRow = sheet.createRow(0);
		tableCell = tableRow.createCell(0);
		tableCell.setCellValue(this.resources.getString("instrument.attribute.symbol"));
		tableCell = tableRow.createCell(1);
		tableCell.setCellValue(this.resources.getString("quotation.attribute.date"));
		tableCell = tableRow.createCell(2);
		tableCell.setCellValue(this.resources.getString("quotation.attribute.price"));
		
		//Define the cells containing the Quotation data.
		for(Quotation tempQuotation:quotations) {
			rowNumber = quotations.indexOf(tempQuotation) + 1;
			
			tableRow = sheet.createRow(rowNumber);
			
			//Symbol Cell
			tableCell = tableRow.createCell(0);
			tableCell.setCellValue(tempQuotation.getInstrument().getSymbol());
			
			//Date Cell - Not formatted as Date but as a simple string. It seems the Framework does not support the needed format.
			tableCell = tableRow.createCell(1);
			tableCell.setCellValue(this.getDateAsExcelString(tempQuotation.getDate()));
			
			//Price Cell
			tableCell = tableRow.createCell(2);
			tableCell.setCellValue(tempQuotation.getClose().doubleValue());
		}
		
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
	
	
	/**
	 * Converts the given date to a String in the German format dd.mm.yyyy.
	 * 
	 * @param date The date.
	 * @return The String representation of the date.
	 */
	public String getDateAsExcelString(final Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(calendar.get(Calendar.DAY_OF_MONTH));
		stringBuilder.append(".");
		stringBuilder.append(calendar.get(Calendar.MONTH) + 1);
		stringBuilder.append(".");
		stringBuilder.append(calendar.get(Calendar.YEAR));
		
		return stringBuilder.toString();
	}
}
