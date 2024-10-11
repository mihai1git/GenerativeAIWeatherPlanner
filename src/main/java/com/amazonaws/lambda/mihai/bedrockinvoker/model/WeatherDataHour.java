package com.amazonaws.lambda.mihai.bedrockinvoker.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WeatherDataHour {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	private static final SimpleDateFormat formatterHour = new SimpleDateFormat("HH");

	private Date hourDate;
	private List<WeatherDataHourDetail> details;
	private Boolean isDay;
	private String temperature;
	private String feelTemperature;
	
	@Override
	public String toString() {
		return " hourDate: " + getHourDateStr()
				+ " isDay: " + isDay
				+ " details: " + ((details==null)?"":Arrays.toString(details.toArray()));
	}
	
	public String getHourDateStr() {
		return formatter.format(hourDate);
	}
	
	public String getHour() {
		return formatterHour.format(hourDate);		
	}
	
	public String getRoundFeelTemp () {
		return String.valueOf(Math.round(Float.valueOf(feelTemperature)));
	}

	public List<WeatherDataHourDetail> getDetails() {
		return details;
	}

	public void setDetails(List<WeatherDataHourDetail> details) {
		this.details = details;
	}

	public Date getHourDate() {
		return hourDate;
	}

	public void setHourDate(Date hourDate) {
		this.hourDate = hourDate;
	}

	public Boolean getIsDay() {
		return isDay;
	}

	public void setIsDay(Boolean isDay) {
		this.isDay = isDay;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getFeelTemperature() {
		return feelTemperature;
	}

	public void setFeelTemperature(String feelTemperature) {
		this.feelTemperature = feelTemperature;
	}

}
