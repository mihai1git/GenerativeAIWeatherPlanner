package com.amazonaws.lambda.mihai.bedrockinvoker.service;

import java.io.StringWriter;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.amazonaws.lambda.mihai.bedrockinvoker.aspect.TraceAll;

@TraceAll
public class VelocityService {
	
	private S3Service s3Service;
	
	private static final String templatesBucket = "lambda-config-ohio-mihaiadam";
	private static final String templatesKeyPrefix = "BedrockWeatherPlanner/";

	private static Logger logger = LogManager.getLogger(VelocityService.class);
	private static VelocityEngine velocityEngine;
	
	public static VelocityService build() {
		velocityEngine = new VelocityEngine();
		velocityEngine.init();
		return new VelocityService();
	}
	
	public VelocityService () {}
	
	public VelocityService (S3Service s3Service) {
		this.s3Service = s3Service;
	}
	
	public String getTemplateFromS3 (String templateFile, Map<String, Object> contextMap) {
		
		String s3File = s3Service.getTemplate(templatesBucket, templatesKeyPrefix + templateFile);
		
        StringWriter swOut = new StringWriter();
        
		VelocityContext context = new VelocityContext();
		contextMap.entrySet().forEach(entry -> context.put(entry.getKey(), entry.getValue()));		
        /**
         * Merge data and template
         */
        Velocity.evaluate(context, swOut, "VelocityService", s3File);
        
        return swOut.toString();
	}	
	
	public String getTemplate (String templateFile, Map<String, Object> contextMap) {
		
		Template t = null;
		try {
			t=velocityEngine.getTemplate(templateFile);
		} catch (ResourceNotFoundException ex) {
			logger.debug("ResourceNotFoundException for: " + templateFile);
			t=velocityEngine.getTemplate("/src/main/resources/" + templateFile);
		}
		    
		VelocityContext context = new VelocityContext();
		contextMap.entrySet().forEach(entry -> context.put(entry.getKey(), entry.getValue()));		
		    
		StringWriter writer = new StringWriter();
		t.merge( context, writer );
		
		return writer.toString();
	}

	public S3Service getS3Service() {
		return s3Service;
	}

	public void setS3Service(S3Service s3Service) {
		this.s3Service = s3Service;
	}
	
}
