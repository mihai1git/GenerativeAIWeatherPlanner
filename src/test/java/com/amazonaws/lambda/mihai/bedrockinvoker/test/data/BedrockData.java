package com.amazonaws.lambda.mihai.bedrockinvoker.test.data;

import static org.mockito.Mockito.when;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.amazonaws.lambda.mihai.bedrockinvoker.test.utils.TestUtils;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

public class BedrockData {
	
	private static Logger logger = LogManager.getLogger(BedrockData.class);
	
	public static void resetBedrockData (BedrockRuntimeClient bedrockRuntimeClient, BedrockClient bedrockClient) {
		
		when(bedrockRuntimeClient.invokeModel(Mockito.any(Consumer.class))).thenAnswer(new Answer<InvokeModelResponse>() {
			
		     public InvokeModelResponse answer(InvocationOnMock invocation) throws Throwable {
		    	 
		    	 logger.debug("bedrockRuntimeClient.invokeModel");
		    	 
		    	 String data = TestUtils.readFromProjectResource("src/test/resources/bedrock_response.txt");
		    	 
		    	 logger.debug("data " + data);
		    	 
		    	return InvokeModelResponse.builder()
		    			.body(SdkBytes.fromByteArray(new String("{\"results\":[{\"outputText\":\"" + data.trim() + "\"}]}").getBytes()))
		    			.build();	    	 
		    	 
		     }
		 });
	}
}
