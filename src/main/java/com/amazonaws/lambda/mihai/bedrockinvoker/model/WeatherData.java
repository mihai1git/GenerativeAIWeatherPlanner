package com.amazonaws.lambda.mihai.bedrockinvoker.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WeatherData {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	private String jsonHourly;
	
	private String lat;
	private String lon;
	private String timezone;
	private Long timezoneOffset;
	private Date serverTime;
	private Date sunrise;
	private Date sunset;
	private List<WeatherDataHour> hours;
	
	@Override
	public String toString() {
		return "lat: " + lat
				+ " lon: " + lon
				+ " timezone: " + timezone
				+ " serverTime: " + getServerTimeStr()
				+ " sunrise: " + getSunriseStr()
				+ " sunset: " + getSunsetStr()
				+ " hours: " + ((hours == null)?"":Arrays.toString(hours.toArray()));
	}
	
	public String getServerTimeStr() {
		return formatter.format(serverTime);
	}
	public String getSunriseStr() {
		return formatter.format(sunrise);
	}
	public String getSunsetStr() {
		return formatter.format(sunset);
	}
		
	public Long getTimezoneOffset() {
		return timezoneOffset;
	}

	public void setTimezoneOffset(Long timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}

	public List<WeatherDataHour> getHours() {
		return hours;
	}

	public void setHours(List<WeatherDataHour> hours) {
		this.hours = hours;
	}

	public String getJsonHourly() {
		return jsonHourly;
	}
	public void setJsonHourly(String jsonHourly) {
		this.jsonHourly = jsonHourly;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Date getServerTime() {
		return serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}

	public Date getSunrise() {
		return sunrise;
	}

	public void setSunrise(Date sunrise) {
		this.sunrise = sunrise;
	}

	public Date getSunset() {
		return sunset;
	}

	public void setSunset(Date sunset) {
		this.sunset = sunset;
	}
	
}
