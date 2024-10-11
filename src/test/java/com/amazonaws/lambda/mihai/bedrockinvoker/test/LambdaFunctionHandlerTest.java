package com.amazonaws.lambda.mihai.bedrockinvoker.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.lambda.mihai.bedrockinvoker.handler.LambdaFunctionHandler;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.BedrockService;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.HTMLService;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.S3Service;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.VelocityService;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.WeatherService;
import com.amazonaws.lambda.mihai.bedrockinvoker.test.data.BedrockData;
import com.amazonaws.lambda.mihai.bedrockinvoker.test.data.S3Data;
import com.amazonaws.lambda.mihai.bedrockinvoker.test.data.WeatherData;
import com.amazonaws.lambda.mihai.bedrockinvoker.test.utils.TestContext;
import com.amazonaws.lambda.mihai.bedrockinvoker.test.utils.TestUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.s3.AmazonS3;

import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@ExtendWith(MockitoExtension.class)
public class LambdaFunctionHandlerTest {

	public BedrockService bedrockService = new BedrockService();
	private HTMLService htmlService = new HTMLService();
	private VelocityService veloService = new VelocityService();
	private S3Service s3Service = new S3Service();
	
	private BedrockRuntimeClient bedrockRuntimeClient = Mockito.mock(BedrockRuntimeClient.class);
	private BedrockClient bedrockClient = Mockito.mock(BedrockClient.class);
	private WeatherService weatherService = Mockito.mock(WeatherService.class);
	private AmazonS3 s3Client = Mockito.mock(AmazonS3.class);
	
	private Map<String, String> vars = new HashMap<String, String>();
	
	private LambdaFunctionHandler handler;

        
    public LambdaFunctionHandlerTest () {
    	handler = new LambdaFunctionHandler(bedrockService, htmlService, weatherService, s3Service, veloService, vars);
    	bedrockService.setBedrockClient(bedrockClient);
    	bedrockService.setBedrockRuntimeClient(bedrockRuntimeClient);
    	s3Service.setS3Client(s3Client);
    	
    	//AWS SDK JAVA 1 will be deprecated, needs to be replaced with AWS SDK JAVA 2 in future versions 
    	System.setProperty("aws.java.v1.disableDeprecationAnnouncement", "true");
    }

    @BeforeEach
    public void setUp() throws IOException {
       
    	WeatherData.resetWeatherData(weatherService);
    	BedrockData.resetBedrockData(bedrockRuntimeClient, bedrockClient);
    	S3Data.resetS3Data(s3Client);
    }

    private Context createContext() {
        
    	TestContext ctx = new TestContext();
        ctx.setFunctionName("bedrockinvoker.handler.LambdaFunctionHandler");

        return ctx;
    }
    
    @Test
    @DisplayName("Ensure correct flow in all layers")
    public void testLambdaFunctionGetDefaultLocation() throws IOException {
    	APIGatewayV2HTTPEvent event = TestUtils.parse("/api-gateway.event.get.json", APIGatewayV2HTTPEvent.class);
    	
    	APIGatewayV2HTTPResponse response = handler.handleRequest(event, createContext());
    	
    	System.out.println("TEST RESP: " + response);
    	
    }

}
