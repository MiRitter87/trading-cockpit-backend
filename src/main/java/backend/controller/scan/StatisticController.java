package backend.controller.scan;

/**
 * Controls the creation and update of statistical data.
 * 
 * @author Michael
 */
public class StatisticController {
	/**
	 * Updates the statistic.
	 */
	public void updateStatistic() {
		//new Array for Statistic: statisticNew (This Array contains the overall statistic determined during runtime)
		//new Arrays: statisticInsert, statisticUpdate, statisticDelete, statisticDatabase
		
		
		//1. Step: Calculate statistic for all instruments of type stock.
		//Get all instruments of type stock.
		//loop through all instruments. For each instrument do the following:
		//-Get all quotations of instrument
		//-Order quotations by date
		
		//-Loop through all Quotations of the Instrument. For each Quotation do the following:
		//--Check if Statistic object exists for the current Quotations date in statisticNew. If so, use it. If not, create Statistic object.
		//--Determine the 1 day performance
		//--Add advance or decline based on performance
		
		
		//2. Step: Persist new and updates statistic to database:
		//Get the whole existing statistic for instruments typed STOCK into statisticDatabase
		//Loop through statisticNew
		//-check if statisticDatabase has entry with same date and type
		//-if yes, update advance and decline numbers and add Statistic to statisticUpdate (exists at database and needs to be updated)
		//-if no, add Statistic to statisticInsert (does not exist yet at database)
		//perform DAOs methods update and insert
		
		
		//3. Step: Remove obsolete statistics from database:
		//Loop through statisticDatabase
		//-Check if statisticNew contains object with same date and type
		//-if no, add Statistic to statisticDelete (this Statistic is old and not relevant anymore)
		//perform DAOs method delete
	}
}
