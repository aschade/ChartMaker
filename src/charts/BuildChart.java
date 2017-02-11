package charts;

import java.io.File;


import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


import data.DailyValue;
import data.GetJSON;
import data.Request;

/*
 * Uses data package to format the returned data and then build a 
 * JPEG that contains the chart which shows the daily return for 
 * the portfolio and the SPY ETF
 */
public class BuildChart {
	
	//Chart titles
	public static final String chartTitle 	= "Daily return of portfolio versus S&P 500 index";
	public static final String xAxisTitle 	= "Month-Year";
	public static final String yAxisTitle 	= "Daily Return (Dollars)";
	
	//optional params to be added from main method as passed arguments
	public static String startDate 			= "2010-01-01";
	public static String endDate			= null;
	public static String extraTicker 		= null;
	
	//This is the format that Quandl uses for dates
	public static final DateFormat df 		= new SimpleDateFormat("yyyy-MM-dd"); 
	
	//predefined exchange and ticker symbols
	public static final String verizon 		= "NYSE_VZ";
	public static final String boeing 		= "NYSE_BA";
	public static final String ford 		= "NYSE_F";
	public static final String google 		= "NASDAQ_GOOG";
	public static final String geElectric	= "NYSE_GE";
	public static final String spy 			= "NYSE_SPY";
	
	//Amount of money that is owned and can be invested
	public static final int money 			= 1000000;
	
	//tracked value to be used when calculating daily return for the portfolio
	public static int numTickers			= 0;
	
	
	
	/*	Takes the list of tickers and passes it to the data package
	 * 
	 * @return  map		a map with a key that's a ticker and the 
	 * 					value is the list of days which contain the 
	 * 					corresponding values.
	 */
	private static Map<String, DailyValue[]> getData() {
		
		Map<String, DailyValue[]> map = new HashMap<String, DailyValue[]>();
		List<String> tickers = new ArrayList<String>();
		if (extraTicker != null) {
			tickers = Arrays.asList(verizon, boeing, ford, google, geElectric, spy, extraTicker);
		} else {
			tickers = Arrays.asList(verizon, boeing, ford, google, geElectric, spy);
		}
		
		//uses each ticker and retrieves it's DailyValue[] object and puts it in the hash map
		for (String ticker : tickers) {
			map.put(ticker, GetJSON.getJSONFromQuandl(new Request(ticker, startDate, endDate)));
			if (map.get(ticker).length > 0) {
				System.out.println("Data retrieved for: " + ticker);
				numTickers++;
			} else {
				System.err.print("Error when retrieving data for: " + ticker);
			}
		}
		return map;
	}
	
	/*
	 * This method uses the retrieved data from the Quandl API to plot the daily 
	 * return at each instance in time.
	 * 
	 * @return dataSet	TimeSeriesCollection object that contains the formatted data to 
	 * 					build the double line chart off of.
	 */
	private static TimeSeriesCollection calculateData() throws ParseException {
		Map<String, DailyValue[]> map = getData();
		
		TimeSeriesCollection dataSet = new TimeSeriesCollection ();
		
		TimeSeries seriesSP = new TimeSeries("S&P 500");
		TimeSeries seriesPortfolio = new TimeSeries("Portfolio");
		
		//This is the last day that will be shown on the graph
		Day latestToShow = getLastDayToShow(map);
		
		//Calculates daily return for S&P 500 for each day in the data, and then adds it to the XYSeries object
		for (String key : map.keySet()) {
			
			for (int i = 0; i < map.get(key).length; i++) {
				DailyValue curr = map.get(key)[i];
				Double startingPrice = map.get(key)[0].getClose();
				Double dailyDelta = (curr.getClose() - startingPrice) / startingPrice;
				
				Date currDate = df.parse(curr.getDate());
				
				Day currDay = new Day(currDate);
								
				if (key.equals(spy)) {
					
					//this calculates the new total compared to the initial investment
					seriesSP.add(currDay, money + (dailyDelta * money));
				} else if (latestToShow.getFirstMillisecond() >= currDay.getFirstMillisecond()) {
					
					//Current return is the value that's currently on the graph if there has been one calculated previously
					Double currentReturn = (Double) seriesPortfolio.getValue(currDay);
					Double newReturn = (dailyDelta / (numTickers - 1)) * money;
					//If first time adding value, add original amount
					if (currentReturn == null) {
						newReturn += money;
					}
					//add in the previously entered return if it exists in the time series already
					seriesPortfolio.addOrUpdate(currDay, newReturn + (currentReturn != null ? currentReturn : 0));
				}
			}
			
		}
		
		//adds both lines that we calculated above and puts them into the data set
		dataSet.addSeries(seriesSP);
		dataSet.addSeries(seriesPortfolio);
		
		
		return dataSet;
	}
	
	/*
	 * @param map				Hash map of tickers and their daily retrieved values
	 * @return latestToShow		returns the latest day to show on the created graph
	 */
	private static Day getLastDayToShow(Map<String, DailyValue[]> map) throws ParseException {
		
		Day latestToShow = null;
		
		for (String key : map.keySet()) {
			int size = map.get(key).length;
			String lastAvailableDate = map.get(key)[size - 1].getDate();
			Day last = new Day(df.parse(lastAvailableDate));
			//update value if latestToShow is null or if last is before the previously seen value
			latestToShow = latestToShow == null 
						   ? last : last.getFirstMillisecond() < latestToShow.getFirstMillisecond() 
						   ? last : latestToShow;
		}

		return latestToShow;
	}
	
	/*
	 * Retrieves formatted data and then builds a chart using jFreeChart
	 * 
	 * @return chart	chart that will be displayed after having all of 
	 * 					the information calculated
	 */
	private static JFreeChart buildChart() throws ParseException {
		TimeSeriesCollection dataSet = calculateData();
		
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				chartTitle, 
				xAxisTitle, 
				yAxisTitle, 
				dataSet, 
				true, 
				true, 
				false);
		
		return chart;
	}

	/*
	 * Main method
	 * 
	 * @param args[0]	optional start date if available (must be yyyy-dd-MM)
	 * @param args[1]	optional end date if available (must be yyyy-dd-MM)
	 * @param args[2]	optional ticker if provided. Format: EXCHANGE_TICKER
	 */
	public static void main(String[] args) throws ParseException  {
		
		if (args.length >= 1) {
			startDate = args[0];
		}
		
		if (args.length >= 2) {
			endDate = args[1];
		}

		if (args.length >= 3) {
			extraTicker = args[2];
		}

		JFreeChart chart = buildChart();
				
		//save chart to specified path and open, change the location if it's not valid
		try {
			ChartUtilities.saveChartAsJPEG(new File("/Users/Alex/chart.jpg"), chart, 1000, 600);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		
		//opens up graph JPEG for viewing
		JLabel label = new JLabel(new ImageIcon("/Users/Alex/chart.jpg"));
		JFrame frame = new JFrame();
		frame.add(label);
		frame.setSize(1000, 700);
		frame.setVisible(true);
		frame.setLocation(290, 90);
	}

}
