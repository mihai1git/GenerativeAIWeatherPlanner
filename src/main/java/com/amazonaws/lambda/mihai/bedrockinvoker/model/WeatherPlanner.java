package com.amazonaws.lambda.mihai.bedrockinvoker.model;

public class WeatherPlanner {

	private WeatherData weatherData;
	
	private String bedrockResponse;

	public WeatherData getWeatherData() {
		return weatherData;
	}

	public void setWeatherData(WeatherData weatherData) {
		this.weatherData = weatherData;
	}

	public String getBedrockResponse() {
		return bedrockResponse;
	}

	public void setBedrockResponse(String bedrockResponse) {
		this.bedrockResponse = bedrockResponse;
	}
	
	
}
