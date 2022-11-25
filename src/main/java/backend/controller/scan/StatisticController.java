package backend.controller.scan;

import java.util.List;

import backend.dao.DAOManager;
import backend.dao.quotation.QuotationDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;
import backend.model.statistic.StatisticArray;

/**
 * Controls the creation and update of statistical data.
 * 
 * @author Michael
 */
public class StatisticController {
	/**
	 * DAO to access Quotation data.
	 */
	QuotationDAO quotationDAO;
	
	
	/**
	 * Default constructor.
	 */
	public StatisticController() {
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
	}
	
	
	/**
	 * Updates the statistic.
	 * @throws Exception Statistic calculation or database access failed.
	 */
	public void updateStatistic() throws Exception {
		List<Statistic> statisticNew;
		//new Array for Statistic: statisticNew (This Array contains the overall statistic determined during runtime)
		//new Arrays: statisticInsert, statisticUpdate, statisticDelete, statisticDatabase
		
		
		//1. Step: Calculate statistic for all instruments of type stock.
		//Get all instruments of type stock.
		statisticNew = this.calculateStatistics(null);
		
		
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
	
	
	/**
	 * Calculates the statistics based on the given instruments.
	 * 
	 * @param instruments The instruments for which the statistics are to be calculated.
	 * @return The statistics.
	 * @throws Exception Statistic calculation failed.
	 */
	public List<Statistic> calculateStatistics(final List<Instrument> instruments) throws Exception {
		StatisticArray statistics = new StatisticArray();
		Statistic statistic;
		List<Quotation> quotationsSortedByDate;
		Quotation previousQuotation;
		int currentQuotationIndex;
		
		for(Instrument instrument: instruments) {
			instrument.setQuotations(quotationDAO.getQuotationsOfInstrument(instrument.getId()));
			quotationsSortedByDate = instrument.getQuotationsSortedByDate();
			
			for(Quotation currentQuotation: quotationsSortedByDate) {
				currentQuotationIndex = quotationsSortedByDate.indexOf(currentQuotation);
				
				//Stop Statistic calculation for the current Instrument if no previous Quotation exists.
				if(currentQuotationIndex == (quotationsSortedByDate.size() - 1))
					break;
				
				previousQuotation = quotationsSortedByDate.get(currentQuotationIndex + 1);
				statistic = statistics.getStatisticOfDate(currentQuotation.getDate());
				
				if(statistic == null) {
					statistic = new Statistic();
					statistic.setDate(statistics.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
					statistics.addStatistic(statistic);
				}
				
				//Calculate statistical values.
				statistic.setNumberAdvance(statistic.getNumberAdvance() + this.getNumberAdvance(currentQuotation, previousQuotation));
				statistic.setNumberDecline(statistic.getNumberDecline() + this.getNumberDecline(currentQuotation, previousQuotation));
			}
		}
		
		return statistics.getStatistics();
	}
	
	
	/**
	 * Compares the price of the current and previous Quotation.
	 * 
	 * @param currentQuotation The current Quotation.
	 * @param previousQuotation The previous Quotation.
	 * @return 1, if price of current Quotation is bigger than price of previous Quotation. 0 if not.
	 */
	private int getNumberAdvance(final Quotation currentQuotation, final Quotation previousQuotation) {
		if(currentQuotation.getPrice().compareTo(previousQuotation.getPrice()) == 1)
			return 1;
		else
			return 0;
	}
	
	
	/**
	 * Compares the price of the current and previous Quotation.
	 * 
	 * @param currentQuotation The current Quotation.
	 * @param previousQuotation The previous Quotation.
	 * @return 1, if price of current Quotation is smaller than price of previous Quotation. 0 if not.
	 */
	private int getNumberDecline(final Quotation currentQuotation, final Quotation previousQuotation) {
		if(currentQuotation.getPrice().compareTo(previousQuotation.getPrice()) == -1)
			return 1;
		else
			return 0;
	}
	
	
	/**
	 * Persists statistics to the database.
	 * 
	 * @param newStatistics The new statistics that have been calculated during runtime based on the current database state of quotation data.
	 * @throws Exception In case persisting failed.
	 */
	private void persistStatistics(final List <Statistic> newStatistics) throws Exception {
		
	}
	
	
	/**
	 * Compares the database state of the statistics and the newly calculated statistics.
	 * New statistics are returned, that have yet to be persisted at database level.
	 * 
	 * @param newStatistics The new statistics that have been calculated during runtime based on the current database state of quotation data.
	 * @param databaseStatistics The database state of the statistics before the new statistic calculation has been executed.
	 * @return Statistics that have to be inserted into the database.
	 */
	private List<Statistic> getStatisticsForInsertion(final List <Statistic> newStatistics, final List <Statistic> databaseStatistics) {
		return null;
	}
	
	
	/**
	 * Compares the database state of the statistics and the newly calculated statistics.
	 * Obsolete statistics are returned, that have to be deleted from the database.
	 * 
	 * @param newStatistics The new statistics that have been calculated during runtime based on the current database state of quotation data.
	 * @param databaseStatistics The database state of the statistics before the new statistic calculation has been executed.
	 * @return Statistics that have to be deleted from the database.
	 */
	private List<Statistic> getStatisticsForDeletion(final List <Statistic> newStatistics, final List <Statistic> databaseStatistics) {
		return null;
	}
	
	
	/**
	 * Compares the database state of the statistics and the newly calculated statistics.
	 * Those statistics are returned that already existed at the database before but need to be updated with the newest data.
	 * 
	 * @param newStatistics The new statistics that have been calculated during runtime based on the current database state of quotation data.
	 * @param databaseStatistics The database state of the statistics before the new statistic calculation has been executed.
	 * @return Statistics that already exist but need to be updated.
	 */
	private List<Statistic> getStatisticsForUpdate(final List <Statistic> newStatistics, final List <Statistic> databaseStatistics) {
		return null;
	}
}
