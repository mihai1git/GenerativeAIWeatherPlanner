package com.amazonaws.lambda.mihai.bedrockinvoker.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.aspect.TraceAll;
import com.amazonaws.lambda.mihai.bedrockinvoker.licensed.Converse;
import com.amazonaws.lambda.mihai.bedrockinvoker.licensed.GetFoundationModel;
import com.amazonaws.lambda.mihai.bedrockinvoker.licensed.ListFoundationModels;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.PromptContent;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherData;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherPlanner;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	/**
	 * As of August 15, 2025 these models will no longer be operational
	 * We recommend one of the following 4 options:
1) If you are using Titan Text G1 - Premier, migrate to the latest version of Nova Pro (amazon.nova-pro-v1:0) [3] by updating your application code [4].
2) If you are using Titan Text G1 - Express, migrate to the latest version of Nova Micro (amazon.nova-micro-v1:0) [3] by updating your application code [4].
3) If you are using Titan Text G1 - Lite, migrate to the latest version of Nova Lite (amazon.nova-lite-v1:0) [3] by updating your application code [4].
4) If you are using Titan Image Generator G1 V1, migrate to the latest version of Nova Canvas (amazon.nova-canvas-v1:0) [3] by updating your application code [4].
	 */
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
		
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("timezone", forecast.getTimezone());
		values.put("sunrise", formatter.format(forecast.getSunrise()));
		values.put("sunset", formatter.format(forecast.getSunset()));
		values.put("timezoneOffset", forecast.getTimezoneOffset());
		values.put("jsonHourly", forecast.getJsonHourly().replace("\"", "\\\""));
		
		String generativeAiStylesStr = environmentVariables.getOrDefault("generativeAiStyles","");
		List<String> generativeAiStyles = new ArrayList<String>(Arrays.asList(generativeAiStylesStr.split(",")));
		generativeAiStyles.add(null);
		Random generator = new Random();
		int randomStyle = generator.nextInt(generativeAiStyles.size());
		String genAiStyle = generativeAiStyles.get(randomStyle);
		values.put("genAiStyle", genAiStyle);
		
		String prompt = velocityService.getTemplateFromS3("bedrockPromtContext.vm", values);
		        
        PromptContent promptContent = new PromptContent();
        promptContent.setStaticContent(prompt.substring(1, prompt.indexOf(PromptContent.CACHE_POINT_MARKER)));
        promptContent.setDynamicContent(prompt.substring(prompt.indexOf(PromptContent.CACHE_POINT_MARKER) + PromptContent.CACHE_POINT_MARKER.length() + 1));
                
		logger.debug("prompt: " + promptContent);	
		
		WeatherPlanner planner = new WeatherPlanner();
		
		planner.setWeatherData(forecast);
		planner.setGenAiStyle(genAiStyle);
		//String llmResp = (new InvokeModel()).invokeModel(bedrockRuntimeClient, prompt, environmentVariables.get(modelIDKey));
		String llmResp = (new Converse()).converse(bedrockRuntimeClient, promptContent, environmentVariables.get(modelIDKey));
		processLLMResponse (planner, llmResp);
		
		return planner;
	}
	
	private void processLLMResponse (WeatherPlanner planner, String llmResponse) {
		
		
		String llmResp = compactJSON(llmResponse);
		logger.debug("processLLMResponse " + llmResp);
		
    	JsonNode objectRequest = null;  // root node
    	
        try {
        	ObjectMapper OBJECT_MAPPER = new ObjectMapper();
        	objectRequest = OBJECT_MAPPER.readTree(llmResp);
        	
        	String eventString = OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL).writer().writeValueAsString(objectRequest);
        	
        	//logger.debug("llmResp: " + eventString);
        	
        } catch (Exception ex) {
        	ex.printStackTrace();
        	throw new RuntimeException(ex);
        }
        String title = objectRequest.at("/title").asText();
        String recommendation = objectRequest.at("/recommendation").asText();
        
        planner.setLlmResponseTitle(title);
        planner.setLlmResponseBody(recommendation.replace("\\n", "<br>"));
	}

	/**
	 * hard-coded for this particular output structure (title, recommendation), to keep line-breaks
	 * @param jsonString
	 * @return
	 */
	private String compactJSON (String jsonString) {
		
		String finalJSON = jsonString.replaceFirst("\\R", "").replaceFirst("\\R", "");
		String reverse = new StringBuffer(finalJSON).reverse().toString();
		reverse = reverse.replaceFirst("\\R", "");
		finalJSON = new StringBuffer(reverse).reverse().toString();
		finalJSON = finalJSON.replaceAll("\\R", "\\\\n");
		
		return finalJSON;
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
