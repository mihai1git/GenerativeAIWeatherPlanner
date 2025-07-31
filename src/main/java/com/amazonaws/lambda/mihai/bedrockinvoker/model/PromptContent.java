package com.amazonaws.lambda.mihai.bedrockinvoker.model;

public class PromptContent {
	
	public static final String CACHE_POINT_MARKER = "[cachePoint]";

	//only 1 cache Point considered
	private String staticContent;
	private String dynamicContent;
	
	
	public String toString () {
		return "staticContent: " + staticContent +
				" dynamicContent: " + dynamicContent;
	}
	
	public String getStaticContent() {
		return staticContent;
	}
	public void setStaticContent(String staticContent) {
		this.staticContent = staticContent;
	}
	public String getDynamicContent() {
		return dynamicContent;
	}
	public void setDynamicContent(String dynamicContent) {
		this.dynamicContent = dynamicContent;
	}
	
	
}
