package com.amazonaws.lambda.mihai.bedrockinvoker.model;

public class WeatherPlanner {

	private WeatherData weatherData;
	
	private String llmResponseTitle;
	private String llmResponseBody;
	
	private String genAiStyle;

	public WeatherData getWeatherData() {
		return weatherData;
	}

	public void setWeatherData(WeatherData weatherData) {
		this.weatherData = weatherData;
	}

	public String getLlmResponseTitle() {
		return llmResponseTitle;
	}

	public void setLlmResponseTitle(String llmResponseTitle) {
		this.llmResponseTitle = llmResponseTitle;
	}

	public String getLlmResponseBody() {
		return llmResponseBody;
	}

	public void setLlmResponseBody(String llmResponseBody) {
		this.llmResponseBody = llmResponseBody;
	}

	public String getGenAiStyle() {
		return genAiStyle;
	}

	public void setGenAiStyle(String genAiStyle) {
		this.genAiStyle = genAiStyle;
	}

	
}
