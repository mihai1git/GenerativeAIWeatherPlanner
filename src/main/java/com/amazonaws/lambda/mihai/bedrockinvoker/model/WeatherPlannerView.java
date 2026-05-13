package com.amazonaws.lambda.mihai.bedrockinvoker.model;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class WeatherPlannerView {

	private WeatherPlanner weatherPlanner;
	
	/**
	 * 
	 * @return
	 */
	public String getWeatherPlannerForMCP () {
		
		StringBuilder sb = new StringBuilder();
		
		FilterProvider filters = new SimpleFilterProvider()
				.addFilter("customFilter", SimpleBeanPropertyFilter.serializeAllExcept("jsonHourly"));
		
		sb.append(Utils.getObjectAsJson(this.weatherPlanner, filters));
		
		
		return sb.toString();
	}
	
	public WeatherPlanner getWeatherPlanner() {
		return weatherPlanner;
	}

	public void setWeatherPlanner(WeatherPlanner weatherPlanner) {
		this.weatherPlanner = weatherPlanner;
	}
	
	
}
