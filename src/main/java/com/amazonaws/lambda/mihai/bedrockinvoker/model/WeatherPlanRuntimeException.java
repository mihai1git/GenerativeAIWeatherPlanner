package com.amazonaws.lambda.mihai.bedrockinvoker.model;

public class WeatherPlanRuntimeException extends RuntimeException {

	public WeatherPlanRuntimeException(Exception e) {
		super(e);
	}

	public WeatherPlanRuntimeException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
