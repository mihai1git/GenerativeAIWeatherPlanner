package com.amazonaws.lambda.mihai.bedrockinvoker.model;

public class WeatherDataHourDetail {

	private String main;
	private String description;
	private String icon;
	
	@Override
	public String toString() {
		return 		" main: " + main
				+ 	" description: " + description
				+ 	" icon: " + icon;
	}
	
	public String getIconURL() {
		return (icon == null)?"":String.format("http://openweathermap.org/img/w/%s.png", icon);
	}
	public String getMain() {
		return main;
	}
	public void setMain(String main) {
		this.main = main;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
}
