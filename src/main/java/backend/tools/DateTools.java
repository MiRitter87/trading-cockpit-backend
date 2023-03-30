package backend.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Collection of tools for working with dates.
 * 
 * @author Michael
 */
public class DateTools {	
	/**
	 * Converts the given date instance into a date object that has no hours, minutes, seconds and milliseconds defined.
	 * 
	 * @param date The date.
	 * @return The date without intraday attributes.
	 */
	public static Date getDateWithoutIntradayAttributes(final Date date) {
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	
	/**
	 * Converts the given String into a Date object.
	 * 
	 * @param dateAsString A date in the format yyyy-MM-dd.
	 * @return A Date object.
	 * @throws ParseException Date formatting failed.
	 */
	public static Date convertStringToDate(final String dateAsString) throws ParseException {
		Date date;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		date = formatter.parse(dateAsString);
		
		return date;
	}
}
