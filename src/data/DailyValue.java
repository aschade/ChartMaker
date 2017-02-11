package data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/*
 * Each DailyValue represents a day and the corresponding values 
 * associated with that array.
 */
@JsonDeserialize(using = DailyValueDeserializer.class)
public class DailyValue {
			
	private String date;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Double volume;
	
	
	public DailyValue(String date, Double open, Double high, Double low, Double close, Double volume) {
		super();
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Double getOpen() {
		return open;
	}
	public void setOpen(Double open) {
		this.open = open;
	}
	public Double getHigh() {
		return high;
	}
	public void setHigh(Double high) {
		this.high = high;
	}
	public Double getLow() {
		return low;
	}
	public void setLow(Double low) {
		this.low = low;
	}
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
	
}
