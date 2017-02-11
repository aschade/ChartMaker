package data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/*
 * This class uses a request object to retrieve JSON from quandl,
 * and then serializes it into a GraphData object
 */
public class GetJSON {

	/*
	 * Retrieves JSON from Quandl and then maps it to a DailyValue[] POJO
	 * 
	 * @param req	 Contains the Strings that create the URL request to 
	 * 				 get the JSON
	 * @return data	 DailyValue[] for a particular stock
	 * 
	 */
	public static DailyValue[] getJSONFromQuandl(Request req) {
		URL url = null;
		
		try {
			url = new URL(req.toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		ObjectMapper mapper = new ObjectMapper();

		DailyValue[] data = null;
		
		if (url != null) {
			try {
				//Maps JSON to DailyValue[] object after it unwraps the "dataset" and "data" keys
				JsonNode object = mapper.readTree(url).path("dataset").path("data");
				data = mapper.treeToValue(object, DailyValue[].class);
				//reverse the array because it comes in from latest to earliest and we need the reverse
				Collections.reverse(Arrays.asList(data));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return data;
	}
}
