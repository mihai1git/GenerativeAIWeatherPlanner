// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazonaws.lambda.mihai.bedrockinvoker.licensed;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.model.PromptContent;

// snippet-start:[bedrock-runtime.java2.Converse]
// Use the Converse API to send a text message to Amazon Nova Pro.

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.CachePointBlock;
import software.amazon.awssdk.services.bedrockruntime.model.CachePointType;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

/**
 * prompt cache is supported in Amazon family only by the new NOVA model; TITAN does not support cache
 *
 */
public class Converse {
	
	private Logger logger = LogManager.getLogger(Converse.class);

    public String converse(BedrockRuntimeClient client, PromptContent prompt, String modelId) {

        Message message = Message.builder()
        		.content(List.of(
        				ContentBlock.fromText(prompt.getStaticContent().replaceAll("\\R", " ")),
        				ContentBlock.builder()
	        				.cachePoint(CachePointBlock.builder()
	        				.type(CachePointType.DEFAULT)
	        				.build())
        				.build(),
        				ContentBlock.fromText((prompt.getDynamicContent() == null)?"":prompt.getDynamicContent().replaceAll("\\R", " "))
        				))
                .role(ConversationRole.USER)
                .build();

        logger.debug("request all: " + message.toString());
        
        try {
            // Send the message with a basic inference configuration.
            ConverseResponse response = client.converse(request -> request
                    .modelId(modelId)
                    .messages(message)
                    .inferenceConfig(config -> config
                            .maxTokens(512)
                            .temperature(0.5F)
                            .topP(0.9F)));

            /**
             * If you used prompt caching, then in the usage field, 
             * cacheReadInputTokensCount and cacheWriteInputTokensCount 
             * tell you how many total tokens were read from the cache and written to the cache, respectively.
             */
            logger.debug("response usage: " + response.usage());
            logger.debug("response all: " + response.toString());
            
            // Retrieve the generated text from Bedrock's response object.
            var responseText = response.output().message().content().get(0).text();
            
            return responseText;

        } catch (SdkClientException e) {
        	e.printStackTrace();
        	logger.error("ERROR: Can't invoke '%s'. Reason: %s", modelId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        // Create a Bedrock Runtime client in the AWS Region you want to use.
        // Replace the DefaultCredentialsProvider with your preferred credentials provider.
        var client = BedrockRuntimeClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_2)
                .build();

        // Set the model ID, e.g., Amazon Nova.
        var modelId = "amazon.nova-pro-v1:0";
        
        new Converse().converse(client, new PromptContent(), modelId);
    }
}

// snippet-end:[bedrock-runtime.java2.Converse]