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

import backend.controller.instrumentCheck.NoQuotationsExistException;
import backend.controller.statistic.AboveSma50ChartController;
import backend.controller.statistic.AdvanceDeclineNumberChartController;
import backend.controller.statistic.DistributionDaysChartController;
import backend.controller.statistic.FollowThroughDaysChartController;
import backend.controller.statistic.PocketPivotChartController;
import backend.controller.statistic.RitterMarketTrendChartController;
import backend.controller.statistic.RitterPatternIndicatorChartController;
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
			case FOLLOW_THROUGH_DAYS:
				return this.getFollowThroughDaysChart(instrumentId);
			case RITTER_MARKET_TREND:
				return this.getRitterMarketTrendChart(listId);
			case RITTER_PATTERN_INDICATOR:
				return this.getRitterPatternIndicatorChart(listId);
			case POCKET_PIVOTS:
				return this.getPocketPivotsChart(instrumentId);
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
		AdvanceDeclineNumberChartController adChartController = new AdvanceDeclineNumberChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = adChartController.getAdvanceDeclineNumberChart(InstrumentType.STOCK, listId);
			
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
		AboveSma50ChartController aboveSma50ChartController = new AboveSma50ChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = aboveSma50ChartController.getInstrumentsAboveSma50Chart(InstrumentType.STOCK, listId);
			
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
		DistributionDaysChartController distributionDaysChartController = new DistributionDaysChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		if(instrumentId == null)
			return Response.status(Status.BAD_REQUEST).build();
		
		try {
			chart = distributionDaysChartController.getDistributionDaysChart(instrumentId);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 1600, 600);
				}
			};
		} 
		catch(NoQuotationsExistException noQuotationsExistException) {
			return Response.status(404, 	//No data found.
					this.resources.getString("statistic.chartDistributionDays.noQuotationsError")).build();
		}
		catch (Exception exception) {
			logger.error(this.resources.getString("statistic.chartDistributionDays.getError"), exception);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(streamingOutput).build();
	}
	
	
	/**
	 * Provides a chart of an Instrument marked with Follow-Through Days.
	 * 
	 * @param instrumentId The ID of the Instrument used for Statistic chart creation.
	 * @return A Response containing the generated chart.
	 */
	private Response getFollowThroughDaysChart(final Integer instrumentId) {
		FollowThroughDaysChartController ftdChartController = new FollowThroughDaysChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		if(instrumentId == null)
			return Response.status(Status.BAD_REQUEST).build();
		
		try {
			chart = ftdChartController.getFollowThroughDaysChart(instrumentId);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 1600, 600);
				}
			};
		} catch (Exception exception) {
			logger.error(this.resources.getString("statistic.chartFollowThroughDays.getError"), exception);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(streamingOutput).build();
	}
	
	
	/**
	 * Provides a chart of the Ritter Market Trend.
	 * 
	 * @param listId The ID of the list defining the instruments used for Statistic chart creation.
	 * @return A Response containing the generated chart.
	 */
	private Response getRitterMarketTrendChart(final Integer listId) {
		RitterMarketTrendChartController rmtChartController = new RitterMarketTrendChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = rmtChartController.getRitterMarketTrendChart(InstrumentType.STOCK, listId);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 800, 600);
				}
			};
		} catch (Exception exception) {
			logger.error(this.resources.getString("statistic.chartRitterMarketTrend.getError"), exception);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(streamingOutput).build();
	}
	
	
	/**
	 * Provides a chart of the Ritter Pattern Indicator.
	 * 
	 * @param listId The ID of the list defining the instruments used for chart creation.
	 * @return A Response containing the generated chart.
	 */
	private Response getRitterPatternIndicatorChart(final Integer listId) {
		RitterPatternIndicatorChartController rpiChartController = new RitterPatternIndicatorChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = rpiChartController.getRitterPatternIndicatorChart(InstrumentType.STOCK, listId);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 800, 600);
				}
			};
		} catch (Exception exception) {
			logger.error(this.resources.getString("statistic.chartRitterPatternIndicator.getError"), exception);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(streamingOutput).build();
	}
	
	
	/**
	 * Provides a chart of an Instrument marked with Pocket Pivots.
	 * 
	 * @param instrumentId The ID of the Instrument used for Statistic chart creation.
	 * @return A Response containing the generated chart.
	 */
	private Response getPocketPivotsChart(final Integer instrumentId) {
		PocketPivotChartController pocketPivotChartController = new PocketPivotChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = pocketPivotChartController.getPocketPivotsChart(instrumentId);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 1600, 600);
				}
			};
		} catch (Exception exception) {
			logger.error(this.resources.getString("statistic.chartPocketPivots.getError"), exception);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(streamingOutput).build();
	}
}
