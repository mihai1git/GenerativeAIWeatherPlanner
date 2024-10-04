//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
//SPDX-License-Identifier: Apache-2.0

package com.amazonaws.lambda.mihai.bedrockinvoker.licensed;



import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.FoundationModelSummary;
import software.amazon.awssdk.services.bedrock.model.ListFoundationModelsResponse;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.aspect.TraceAll;

@TraceAll
public class ListFoundationModels {
	
	private static Logger logger = LogManager.getLogger(ListFoundationModels.class);

	private static Region region;

	 public static void main(String[] args) {
	     final String usage = ""
	     		+ "Usage:"
	     		+ " [<region>]\s"
	     		+ " "
	     		+ "Where:"
	     		+ "region - (Optional) The AWS region where the Agent is located. Default is 'us-east-1'.";
	
	     if (args.length > 1) {
	    	 logger.debug(usage);
	         System.exit(1);
	     }
	
	     region = args.length == 1 ? Region.of(args[0]) : Region.US_EAST_1;
	
	     logger.debug("Initializing the Amazon Bedrock client...");
	     logger.debug("Region: %s%n", region.toString());
	
	     BedrockClient bedrockClient = BedrockClient.builder()
	             .credentialsProvider(DefaultCredentialsProvider.create())
	             .region(region)
	             .build();
	
	     (new ListFoundationModels()).listFoundationModels(bedrockClient);
	 }
	
	 /**
	  * Lists Amazon Bedrock foundation models that you can use.
	  * You can filter the results with the request parameters.
	  *
	  * @param bedrockClient The service client for accessing Amazon Bedrock.
	  * @return A list of objects containing the foundation models' details
	  */
	 public List<FoundationModelSummary> listFoundationModels(BedrockClient bedrockClient) {
	
	     try {
	         ListFoundationModelsResponse response = bedrockClient.listFoundationModels(r -> {});
	
	         List<FoundationModelSummary> models = response.modelSummaries();
	
	         if (models.isEmpty()) {
	        	 logger.debug("No available foundation models in " + region.toString());
	         } else {
	             for (FoundationModelSummary model : models) {
	            	 logger.debug("Model ID: " + model.modelId());
	            	 logger.debug("Provider: " + model.providerName());
	            	 logger.debug("Name:     " + model.modelName());
	             }
	         }
	
	         return models;
	
	     } catch (SdkClientException e) {
	    	 logger.error(e.getMessage());
	         throw new RuntimeException(e);
	     }
	 }
}
