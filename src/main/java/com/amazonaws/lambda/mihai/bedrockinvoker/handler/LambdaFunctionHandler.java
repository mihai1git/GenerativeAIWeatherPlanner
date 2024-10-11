package com.amazonaws.lambda.mihai.bedrockinvoker.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherData;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherPlanner;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherPlannerView;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.BedrockService;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.HTMLService;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.S3Service;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.VelocityService;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.WeatherService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;

import software.amazon.lambda.powertools.logging.Logging;

public class LambdaFunctionHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
	
	private Logger logger = LogManager.getLogger(LambdaFunctionHandler.class);

	public BedrockService bedrockService;
	private HTMLService htmlService;
	private WeatherService weatherService;
	private S3Service s3Service;
	private VelocityService veloService;

    public LambdaFunctionHandler() {
    	bedrockService = BedrockService.build();
    	htmlService = new HTMLService();
    	weatherService = new WeatherService();
    	s3Service = S3Service.build();
    	veloService = VelocityService.build();
    	
    	veloService.setS3Service(s3Service);
    	bedrockService.setVelocityService(veloService);
    	htmlService.setVelocityService(veloService);
    	
    	setEnvironmentVars(System.getenv());
    	
    }

    // Test purpose only.
    public LambdaFunctionHandler(BedrockService bedrockService, HTMLService htmlService, WeatherService weatherService, S3Service s3Service, VelocityService veloService, Map<String, String> vars) {
    	this.bedrockService = bedrockService;
    	this.htmlService = htmlService;
    	this.weatherService = weatherService;
    	this.s3Service = s3Service;
    	this.veloService = veloService;
    	
    	veloService.setS3Service(s3Service);
    	bedrockService.setVelocityService(veloService);
    	htmlService.setVelocityService(veloService);
    	
    	setEnvironmentVars(vars);
    	
    }

    @Override
    @Logging(logEvent = true,correlationIdPath = "/headers/x-amzn-trace-id")
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
    	
    	logger.debug("Received HTTP AWS API GW route key: " + event.getRouteKey());
    	
    	//if option is NO, choose default: Bucharest/Europe
    	String lat = "44.43225";
    	String lon = "26.10626";
    	Map<String, String> queryParams = event.getQueryStringParameters();
    	if (queryParams != null && queryParams.get("lat") != null && queryParams.get("lon") != null) {
        	lat = queryParams.get("lat");
        	lon = queryParams.get("lon");
    	}
    	logger.debug("geolocation (lat,lon): " + lat + " " + lon);
    	
        //bedrockService.logFoundationModelsInfo();
    	String response = null;
    	Map<String, String> headers = new HashMap<String, String>();
    	headers.put("Content-Type", "text/html");
    	
    	
        //https://docs.aws.amazon.com/bedrock/latest/userguide/model-ids.html
        //String modelIdentifier = "amazon.titan-text-premier-v1:0";
        //String modelIdentifier = "amazon.titan-text-express-v1";// the one used
        //String modelIdentifier = "amazon.titan-text-lite-v1";
        
        WeatherData forecast = weatherService.getWeatherForecast(lat, lon);
        
        WeatherPlanner result = bedrockService.getWeatherComments(forecast);
        
        WeatherPlannerView view = new WeatherPlannerView();
        view.setWeatherPlanner(result);
        response = htmlService.getWeatherPlannerPage(view);
        
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(headers)
                .withIsBase64Encoded(false)
                .withBody(response)
                .build();
    }
    
    private void setEnvironmentVars (Map<String, String> vars) {
    	//logger.debug(Arrays.toString(vars.entrySet().toArray()));
    	bedrockService.setEnvironmentVariables(vars);
    	weatherService.setEnvironmentVariables(vars);
    }
}
