//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
//SPDX-License-Identifier: Apache-2.0

package com.amazonaws.lambda.mihai.bedrockinvoker.licensed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.aspect.TraceAll;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.FoundationModelDetails;
import software.amazon.awssdk.services.bedrock.model.GetFoundationModelResponse;
import software.amazon.awssdk.services.bedrock.model.ValidationException;

@TraceAll
public class GetFoundationModel {
	
	private Logger logger = LogManager.getLogger(InvokeModel.class);

	public static void main(String[] args) {
	     final String usage = 
	    		  "	             Usage:"
	     		+ "	                 <modelId> [<region>]\s"
	     		+ "	"
	     		+ "	             Where:"
	     		+ "	                 modelId - The ID of the foundation model you want to use."
	     		+ "	                 region - (Optional) The AWS region where the Agent is located. Default is 'us-east-1'.";
	
	     if (args.length < 1 || args.length > 2) {
	         System.out.println(usage);
	         System.exit(1);
	     }
	
	     String modelId = args[0];
	     Region region = args.length == 2 ? Region.of(args[1]) : Region.US_EAST_1;
	
	     System.out.println("Initializing the Amazon Bedrock client...");
	     System.out.printf("Region: %s%n", region.toString());
	
	     BedrockClient client = BedrockClient.builder()
	             .credentialsProvider(DefaultCredentialsProvider.create())
	             .region(region)
	             .build();
	
	     (new GetFoundationModel()).getFoundationModel(client, modelId);
	}
	
	 /**
	  * Get details about an Amazon Bedrock foundation model.
	  *
	  * @param bedrockClient   The service client for accessing Amazon Bedrock.
	  * @param modelIdentifier The model identifier.
	  * @return An object containing the foundation model's details.
	  */
	public FoundationModelDetails getFoundationModel(BedrockClient bedrockClient, String modelIdentifier) {
	     try {
	         GetFoundationModelResponse response = bedrockClient.getFoundationModel(
	                 r -> r.modelIdentifier(modelIdentifier)
	         );
	
	         FoundationModelDetails model = response.modelDetails();
	
	         logger.debug(" Model ID:                     " + model.modelId());
	         logger.debug(" Model ARN:                    " + model.modelArn());
	         logger.debug(" Model Name:                   " + model.modelName());
	         logger.debug(" Provider Name:                " + model.providerName());
	         logger.debug(" Lifecycle status:             " + model.modelLifecycle().statusAsString());
	         logger.debug(" Input modalities:             " + model.inputModalities());
	         logger.debug(" Output modalities:            " + model.outputModalities());
	         logger.debug(" Supported customizations:     " + model.customizationsSupported());
	         logger.debug(" Supported inference types:    " + model.inferenceTypesSupported());
	         logger.debug(" Response streaming supported: " + model.responseStreamingSupported());
	
	         return model;
	
	     } catch (ValidationException e) {
	         throw new IllegalArgumentException(e.getMessage());
	     } catch (SdkException e) {
	    	 logger.error(e.getMessage());
	         throw new RuntimeException(e);
	     }
	}
}
