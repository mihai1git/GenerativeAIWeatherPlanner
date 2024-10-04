package com.amazonaws.lambda.mihai.bedrockinvoker.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WeatherDataHour {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	private Date hourDate;
	private List<WeatherDataHourDetail> details;
	private Boolean isDay;
	
	public String toString() {
		return " hourDate: " + getHourDateStr()
				+ " isDay: " + isDay
				+ " details: " + ((details==null)?"":Arrays.toString(details.toArray()));
	}
	
	public String getHourDateStr() {
		return formatter.format(hourDate);
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

}
