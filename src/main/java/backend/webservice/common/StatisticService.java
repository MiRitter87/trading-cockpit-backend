package backend.webservice.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import backend.controller.StatisticChartController;
import backend.dao.DAOManager;
import backend.dao.statistic.StatisticDAO;
import backend.model.instrument.InstrumentType;
import backend.model.statistic.StatisticArray;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.webservice.ChartType;

/**
 * Common implementation of the Statistic WebService that can be used by multiple service interfaces like SOAP or REST.
 * 
 * @author Michael
 */
public class StatisticService {
	/**
	 * DAO for Statistic access.
	 */
	private StatisticDAO statisticDAO;
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(StatisticService.class);
	
	
	/**
	 * Initializes the StatisticService.
	 */
	public StatisticService() {
		this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
	}
	
	
	/**
	 * Provides a list of all statistics of the given InstrumentType.
	 * 
	 * @param instrumentType The InstrumentType.
	 * @return A list of all statistics of the given InstrumentType.
	 */
	public WebServiceResult getStatistics(final InstrumentType instrumentType) {
		StatisticArray statistics = new StatisticArray();
		WebServiceResult getStatisticsResult = new WebServiceResult(null);
		
		try {
			statistics.setStatistics(this.statisticDAO.getStatistics(instrumentType));
			getStatisticsResult.setData(statistics);
		} catch (Exception e) {
			getStatisticsResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("statistic.getStatisticsError")));
			logger.error(this.resources.getString("statistic.getStatisticsError"), e);
		}
		
		return getStatisticsResult;
	}
	
	
	/**
	 * Provides a chart of the requested statistical data.
	 * 
	 * @param chartType The type of the requested chart.
	 * @param listId The ID of the list defining the instruments used for Statistic chart creation.
	 * @param instrumentId The ID of the Instrument used for Statistic chart creation.
	 * @return A Response containing the generated chart.
	 */
	public Response getStatisticsChart(final ChartType chartType, final Integer listId, final Integer instrumentId) {
		if(chartType == null)
			return Response.status(404).build();
		
		switch(chartType) {
			case ADVANCE_DECLINE_NUMBER:
				return this.getAdvanceDeclineNumberChart(listId);
			case INSTRUMENTS_ABOVE_SMA50:
				return this.getInstrumentsAboveSma50Chart(listId);
			case DISTRIBUTION_DAYS:
				return this.getDistributionDaysChart(instrumentId);
			default:
				return Response.status(404).build();				
		}
		
	}
	
	
	/**
	 * Provides a chart with the cumulative Advance Decline Number.
	 * 
	 * @param listId The ID of the list defining the instruments used for Statistic chart creation.
	 * @return A Response containing the generated chart.
	 */
	private Response getAdvanceDeclineNumberChart(final Integer listId) {
		StatisticChartController statisticChartController = new StatisticChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = statisticChartController.getAdvanceDeclineNumberChart(InstrumentType.STOCK, listId);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 800, 600);
				}
			};
		} catch (Exception exception) {
			logger.error(this.resources.getString("statistic.chartADNumber.getError"), exception);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(streamingOutput).build();
	}
	
	
	/**
	 * Provides a chart with the percentage of instruments trading above their SMA(50).
	 * 
	 * @param listId The ID of the list defining the instruments used for Statistic chart creation.
	 * @return A Response containing the generated chart.
	 */
	private Response getInstrumentsAboveSma50Chart(final Integer listId) {
		StatisticChartController statisticChartController = new StatisticChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = statisticChartController.getInstrumentsAboveSma50Chart(InstrumentType.STOCK, listId);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 800, 600);
				}
			};
		} catch (Exception exception) {
			logger.error(this.resources.getString("statistic.chartAboveSma50.getError"), exception);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(streamingOutput).build();
	}
	
	
	/**
	 * Provides a chart of an Instrument marked with Distribution Days.
	 * 
	 * @param instrumentId The ID of the Instrument used for Statistic chart creation.
	 * @return A Response containing the generated chart.
	 */
	private Response getDistributionDaysChart(final Integer instrumentId) {
		StatisticChartController statisticChartController = new StatisticChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = statisticChartController.getDistributionDaysChart(instrumentId);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 800, 600);
				}
			};
		} catch (Exception exception) {
			logger.error(this.resources.getString("statistic.chartDistributionDays.getError"), exception);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(streamingOutput).build();
	}
}
