package com.amazonaws.lambda.mihai.bedrockinvoker.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;

public class Utils {
	

	private static Logger logger = LogManager.getLogger(Utils.class);
	
	private Utils() {}


    public static <T> T getJsonAsObject(String jsonObject, Class<T> objectClass) {
    	
    	T result = null;
    	
    	ObjectMapper om = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
    	
    	om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	om.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    	om.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
    	
    	try {
    		result = om.readValue(jsonObject.getBytes(), objectClass);
    		
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		throw new RuntimeException(ex);
    	}
    	
    	
    	return result;
    }
    	
    public static String getObjectAsJson (Object object, FilterProvider filterProvider) {
    	StringBuilder sb = new StringBuilder();
    	    	
    	ObjectMapper om = new ObjectMapper()
    			.setSerializationInclusion(Include.NON_NULL)
    			.addMixIn(Object.class, DynamicFilterMixIn.class)
    			.setFilterProvider(filterProvider);
    			//.withDefaultPrettyPrinter();
		ObjectWriter ow = om.writer();
    	
    	try {
    		sb.append(ow.writeValueAsString(object));
    		
    	} catch (JsonProcessingException ex) {
    		ex.printStackTrace();
    	}
    	
    	return sb.toString();
    }
}
