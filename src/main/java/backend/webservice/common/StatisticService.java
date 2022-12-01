package backend.webservice.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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
	 * @return The chart.
	 */
	public Response getStatisticsChart(final ChartType chartType) {
		StatisticChartController statisticChartController = new StatisticChartController();
		JFreeChart chart;
		StreamingOutput streamingOutput = null;
		
		try {
			chart = statisticChartController.getAdvanceDeclineNumberChart(InstrumentType.STOCK);
			
			streamingOutput = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					ChartUtils.writeChartAsPNG(output, chart, 800, 600);
				}
			};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.ok(streamingOutput).build();
	}
}
