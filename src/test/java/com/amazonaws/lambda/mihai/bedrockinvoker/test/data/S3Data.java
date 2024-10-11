package com.amazonaws.lambda.mihai.bedrockinvoker.test.data;

import static org.mockito.Mockito.when;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.amazonaws.lambda.mihai.bedrockinvoker.test.utils.TestUtils;
import com.amazonaws.services.s3.AmazonS3;

public class S3Data {
	private static Logger logger = LogManager.getLogger(BedrockData.class);
	
	public static void resetS3Data (AmazonS3 s3Client) {
		
		when(s3Client.getObjectAsString(Mockito.any(String.class), Mockito.any(String.class))).thenAnswer(new Answer<String>() {
			
		     public String answer(InvocationOnMock invocation) throws Throwable {
		    	 
		    	 String data = null;
		    	 
		    	Object[] args = invocation.getArguments();
		    	
		    	logger.debug("s3Client.getObjectAsString " + (String)args[1]);
		    	
		    	if (((String)args[1]).contains("bedrockPromtContext")) {
		    		
		    		data = TestUtils.readFromProjectResource("src/main/resources/bedrockPromtContext.vm");
		    	}
		    	
		    	if (((String)args[1]).contains("weatherPlannerPage")) {
		    		
		    		data = TestUtils.readFromProjectResource("src/main/resources/weatherPlannerPage.vm");
		    	}
			    
		    	return data;	    	 
		    	 
		     }
		 });
	}
}
