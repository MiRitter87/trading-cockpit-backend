package backend.model.statistic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An array of statistics.
 * 
 * @author Michael
 */
public class StatisticArray {
	/**
	 * The statistics.
	 */
	private List<Statistic> statistics;
	
	
	/**
	 * Default constructor.
	 */
	public StatisticArray() {
		this.statistics = new ArrayList<>();
	}
	
	
	/** 
	 * Initializes the StatisticArray with the given statistics.
	 * 
	 * @param statistics The statistics with which the array is initialized.
	 */
	public StatisticArray(final List<Statistic> statistics) {
		this.statistics = statistics;
	}


	/**
	 * @return the statistics
	 */
	public List<Statistic> getStatistics() {
		return statistics;
	}


	/**
	 * @param statistics the statistics to set
	 */
	public void setStatistics(List<Statistic> statistics) {
		this.statistics = statistics;
	}
	
	
	/**
	 * Determines the statistic with the given date.
	 * Only day, month and year are taken into account for date lookup.
	 * 
	 * @param date The date for which the Statistic is requested.
	 * @return The Statistic of the given date.
	 */
	public Statistic getStatisticOfDate(final Date date) {
		Date requestDate, statisticDate;
		
		requestDate = this.getDateWithoutIntradayAttributes(date);
		
		for(Statistic statistic: this.statistics) {
			statisticDate = this.getDateWithoutIntradayAttributes(statistic.getDate());
			
			if(statisticDate.getTime() == requestDate.getTime())
				return statistic;
		}
		
		return null;
	}
	
	
	/**
	 * Converts the given date instance into a date object that has no hours, minutes, seconds and milliseconds defined.
	 * 
	 * @param date The date.
	 * @return The date without intraday attributes.
	 */
	public Date getDateWithoutIntradayAttributes(final Date date) {
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
}
