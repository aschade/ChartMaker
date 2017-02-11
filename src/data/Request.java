package data;

/*
 * POJO that builds a request to quandl
 */
public class Request {
	//static values that are used in the request and never change
	private static final String baseURL 	= "https://www.quandl.com/api/v3/datasets/GOOG/";
	private static final String apiKey		= ".json?api_key=HCrdcuts7KAuNZsKitzj";
	private static final String startDate 	= "&start_date=";
	private static final String endDate 	= "&end_date=";
	
	private String tickerAndExchange;
	private String startDateParam;
	private String endDateParam;
	
	//tickerAndExchange is required, but startDataParam and endDataParam are optional based on whether
	//or not the user entered a custom date range.
	public Request(String tickerAndExchange, String startDateParam, String endDateParam) {
		super();
		this.tickerAndExchange = tickerAndExchange;
		this.startDateParam = startDateParam;
		this.endDateParam = endDateParam;
	}

	/*
	 * Custom toString() method to build a URL request
	 * 
	 * @return URL	returns a well formatted URL to Quandl's API
	 */
	@Override
	public String toString() {
		String URL = baseURL + tickerAndExchange + apiKey;
		
		//add date params if provided
		URL += this.startDateParam != null ? startDate + this.startDateParam : "";
		URL += this.endDateParam != null ? endDate + this.endDateParam : "";
						
		return URL;
	}
}
