package com.amazonaws.lambda.mihai.bedrockinvoker.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.aspect.TraceAll;
import com.amazonaws.lambda.mihai.bedrockinvoker.licensed.GetFoundationModel;
import com.amazonaws.lambda.mihai.bedrockinvoker.licensed.InvokeModel;
import com.amazonaws.lambda.mihai.bedrockinvoker.licensed.ListFoundationModels;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherData;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherPlanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@TraceAll
public class BedrockService {
	
	private static Logger logger = LogManager.getLogger(BedrockService.class);

	private BedrockRuntimeClient bedrockRuntimeClient;
	private BedrockClient bedrockClient;
	private VelocityService velocityService;
	
	private Map<String, String> environmentVariables;
	//amazon.titan-text-lite-v1 / amazon.titan-text-express-v1 / amazon.titan-text-premier-v1:0
	public static final String modelIDKey = "BEDROCK_MODEL_ID";
	
	public BedrockService() {}
	
	public static BedrockService build() {
		
		BedrockService srv = new BedrockService();
		
        BedrockClient bedrockClient = BedrockClient.builder()
        		.region(Region.US_EAST_1)
                .build();
        
		 BedrockRuntimeClient client = BedrockRuntimeClient.builder()
	             .region(Region.US_EAST_1)
	             .build();
		 
		 srv.setBedrockClient(bedrockClient);
		 srv.setBedrockRuntimeClient(client);
		 
		 return srv;
	}
	

	public void logFoundationModelsInfo () {
		(new ListFoundationModels()).listFoundationModels(bedrockClient);
		(new GetFoundationModel()).getFoundationModel(bedrockClient, environmentVariables.get(modelIDKey));
	}
	
	/**
	 *
     * if needed maybe pre-process data / transform data into some more understandable to have a better prompt for LLM
     * additional create context-specific prompt / add some constant details beside the events
     *
	 * @param lat
	 * @param lon
	 * @return
	 */
	public WeatherPlanner getWeatherComments (WeatherData forecast) {
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("timezone", forecast.getTimezone());
		values.put("jsonHourly", forecast.getJsonHourly().replace("\"", "\\\""));
		
		String prompt = velocityService.getTemplateFromS3("bedrockPromtContext.vm", values);
		logger.debug("prompt: " + prompt);	
		WeatherPlanner planner = new WeatherPlanner();
		
		planner.setWeatherData(forecast);
		planner.setBedrockResponse((new InvokeModel()).invokeModel(bedrockRuntimeClient, prompt, environmentVariables.get(modelIDKey)));
		
		return planner;
	}

	public BedrockRuntimeClient getBedrockRuntimeClient() {
		return bedrockRuntimeClient;
	}

	public void setBedrockRuntimeClient(BedrockRuntimeClient bedrockRuntimeClient) {
		this.bedrockRuntimeClient = bedrockRuntimeClient;
	}

	public BedrockClient getBedrockClient() {
		return bedrockClient;
	}

	public void setBedrockClient(BedrockClient bedrockClient) {
		this.bedrockClient = bedrockClient;
	}

	public VelocityService getVelocityService() {
		return velocityService;
	}

	public void setVelocityService(VelocityService velocityService) {
		this.velocityService = velocityService;
	}

	public Map<String, String> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setEnvironmentVariables(Map<String, String> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}	
	
}
