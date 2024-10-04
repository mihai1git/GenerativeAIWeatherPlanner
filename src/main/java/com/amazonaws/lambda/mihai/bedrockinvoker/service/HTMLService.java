package com.amazonaws.lambda.mihai.bedrockinvoker.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.aspect.TraceAll;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherPlannerView;

@TraceAll
public class HTMLService {
	
	private VelocityService velocityService;

	private Logger logger = LogManager.getLogger(HTMLService.class);
	
	public String getWeatherPlannerPage (WeatherPlannerView view) {
		
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("view", view);
		
		String page = velocityService.getTemplateFromS3("weatherPlannerPage.vm", context);
		logger.debug("page: " + page);
		
		return page.toString();
	}

	public VelocityService getVelocityService() {
		return velocityService;
	}

	public void setVelocityService(VelocityService velocityService) {
		this.velocityService = velocityService;
	}
	
}
