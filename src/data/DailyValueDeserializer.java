package data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DailyValueDeserializer extends JsonDeserializer<Object> {
	
	/*
	 * This method tells Jackson how to handle the Day object that we are constructing
	 * from the JSON that comes from Quandl
	 * 
	 * @param arg0	Contains the JSON which this method is to parse
	 */
	@Override
	public DailyValue deserialize(JsonParser arg0, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		String date = arg0.nextTextValue();
		arg0.nextToken();
		Double open = arg0.getValueAsDouble();
		arg0.nextToken();
		Double high = arg0.getValueAsDouble();
		arg0.nextToken();
		Double low = arg0.getValueAsDouble();
		arg0.nextToken();
		Double close = arg0.getValueAsDouble();
		arg0.nextToken();
		Double volume = arg0.getValueAsDouble();
		arg0.nextToken();

		return new DailyValue(date, open, high, low, close, volume);
	}		
	
}
