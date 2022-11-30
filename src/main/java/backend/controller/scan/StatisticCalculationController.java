package backend.controller.scan;

import java.util.ArrayList;
import java.util.List;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.QuotationDAO;
import backend.dao.statistic.StatisticDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;
import backend.model.statistic.StatisticArray;

/**
 * Controls the creation and update of statistical data.
 * 
 * @author Michael
 */
public class StatisticCalculationController {
	/**
	 * DAO to access Instrument data.
	 */
	InstrumentDAO instrumentDAO;
	
	/**
	 * DAO to access Quotation data.
	 */
	QuotationDAO quotationDAO;
	
	/**
	 * DAO to access Statistic data.
	 */
	StatisticDAO statisticDAO;
	
	
	/**
	 * Default constructor.
	 */
	public StatisticCalculationController() {
		this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
		this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
	}
	
	
	/**
	 * Updates the statistic.
	 * 
	 * @throws Exception Statistic calculation or database access failed.
	 */
	public void updateStatistic() throws Exception {
		List<Instrument> stocks;
		List<Statistic> statisticNew;

		stocks = instrumentDAO.getInstruments(InstrumentType.STOCK);
		statisticNew = this.calculateStatistics(stocks);
		this.persistStatistics(statisticNew);
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
					statistic.setInstrumentType(instrument.getType());
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
	 * Compares the price of the current and previous Quotation and checks if the Instrument has advanced.
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
	 * Compares the price of the current and previous Quotation and checks if the Instrument has declined.
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
	private void persistStatistics(final List<Statistic> newStatistics) throws Exception {
		List<Statistic> statisticsDatabase, statisticsForInsertion, statisticsForUpdate, statisticsForDeletion;
		
		statisticsDatabase = statisticDAO.getStatistics(InstrumentType.STOCK);
		
		statisticsForInsertion = this.getStatisticsForInsertion(newStatistics, statisticsDatabase);
		statisticsForDeletion = this.getStatisticsForDeletion(newStatistics, statisticsDatabase);
		statisticsForUpdate = this.getStatisticsForUpdate(newStatistics, statisticsDatabase);
		
		for(Statistic insertStatistic: statisticsForInsertion)
			statisticDAO.insertStatistic(insertStatistic);
		
		for(Statistic deleteStatistic: statisticsForDeletion)
			statisticDAO.deleteStatistic(deleteStatistic);
		
		for(Statistic updateStatistic: statisticsForUpdate)
			statisticDAO.updateStatistic(updateStatistic);
	}
	
	
	/**
	 * Compares the database state of the statistics and the newly calculated statistics.
	 * New statistics are returned, that have yet to be persisted to the database.
	 * 
	 * @param newStatistics The new statistics that have been calculated during runtime based on the current database state of quotation data.
	 * @param databaseStatistics The database state of the statistics before the new statistic calculation has been executed.
	 * @return Statistics that have to be inserted into the database.
	 */
	private List<Statistic> getStatisticsForInsertion(final List <Statistic> newStatistics, final List <Statistic> databaseStatistics) {
		List<Statistic> statisticInsert = new ArrayList<>();
		StatisticArray databaseStatisticArray = new StatisticArray(databaseStatistics);
		Statistic databaseStatistic;
		
		for(Statistic newStatistic: newStatistics) {
			databaseStatistic = databaseStatisticArray.getStatisticOfDate(newStatistic.getDate());
			
			if(databaseStatistic == null)
				statisticInsert.add(newStatistic);
		}
		
		return statisticInsert;
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
		List<Statistic> statisticDelete = new ArrayList<>();
		StatisticArray newStatisticsArray = new StatisticArray(newStatistics);
		Statistic newStatistic;
		
		for(Statistic databaseStatistic: databaseStatistics) {
			newStatistic = newStatisticsArray.getStatisticOfDate(databaseStatistic.getDate());
			
			if(newStatistic == null)
				statisticDelete.add(databaseStatistic);
		}
		
		return statisticDelete;
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
		List<Statistic> statisticUpdate = new ArrayList<>();
		StatisticArray databaseStatisticArray = new StatisticArray(databaseStatistics);
		Statistic databaseStatistic;
		
		for(Statistic newStatistic: newStatistics) {
			databaseStatistic = databaseStatisticArray.getStatisticOfDate(newStatistic.getDate());
			
			if(databaseStatistic != null) {
				databaseStatistic.setNumberAdvance(newStatistic.getNumberAdvance());
				databaseStatistic.setNumberDecline(newStatistic.getNumberDecline());
				statisticUpdate.add(databaseStatistic);
			}
		}
		
		return statisticUpdate;
	}
}