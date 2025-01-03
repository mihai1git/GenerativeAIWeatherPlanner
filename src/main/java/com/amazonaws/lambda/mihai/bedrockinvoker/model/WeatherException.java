package com.amazonaws.lambda.mihai.bedrockinvoker.model;

public class WeatherException extends Exception {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Integer statusCode;

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	
	
}
