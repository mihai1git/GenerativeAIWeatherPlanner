//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
//SPDX-License-Identifier: Apache-2.0

package com.amazonaws.lambda.mihai.bedrockinvoker.licensed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONPointer;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

//Use the native inference API to send a text message to Amazon Titan Text.
public class InvokeModel {
	
	private Logger logger = LogManager.getLogger(InvokeModel.class);

	public String invokeModel(BedrockRuntimeClient client, String prompt, String modelId) {
	
	     // The InvokeModel API uses the model's native payload.
	     // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-text.html
		//"textGenerationConfig": {"temperature": 0.7, "topP": 0.9, "maxTokenCount": 8000}
	     String nativeRequestTemplate = "{ \"inputText\": \"{{prompt}}\" }";
	
	     // Embed the prompt in the model's native request payload.
	     String nativeRequest = nativeRequestTemplate.replace("{{prompt}}", prompt).replaceAll("\\R", " ");
	     
	     try {
	    	 
	         // Encode and send the request to the Bedrock Runtime.
	         InvokeModelResponse response = client.invokeModel(request -> request
	                 .body(SdkBytes.fromUtf8String(nativeRequest))
	                 .modelId(modelId)
	         );
	         	
	         // Decode the response body.
	         JSONObject responseBody = new JSONObject(response.body().asUtf8String());
	         	
	         // Retrieve the generated text from the model's response.
	         String text = new JSONPointer("/results/0/outputText").queryFrom(responseBody).toString();
	         logger.debug("response: " + text);
	
	         return text;
	
	     } catch (SdkClientException e) {
	    	 logger.error("ERROR: Can't invoke '%s'. Reason: %s", modelId, e.getMessage());
	         throw new RuntimeException(e);
	     }
	}

	public static void main(String[] args) {
	     // Create a Bedrock Runtime client in the AWS Region you want to use.
	     // Replace the DefaultCredentialsProvider with your preferred credentials provider.
		 BedrockRuntimeClient client = BedrockRuntimeClient.builder()
	             .credentialsProvider(DefaultCredentialsProvider.create())
	             .region(Region.US_EAST_1)
	             .build();
	     // Define the prompt for the model.
	     String prompt = "Describe the purpose of a 'hello world' program in one line.";
	     
	     (new InvokeModel()).invokeModel(client, prompt, "amazon.titan-text-express-v1");
	 }
}