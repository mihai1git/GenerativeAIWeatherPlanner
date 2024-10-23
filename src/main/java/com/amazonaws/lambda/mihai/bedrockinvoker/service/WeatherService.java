package com.amazonaws.lambda.mihai.bedrockinvoker.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.aspect.TraceAll;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherData;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherDataHour;
import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherDataHourDetail;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@TraceAll
public class WeatherService {
	
	private static Logger logger = LogManager.getLogger(WeatherService.class);
	private Map<String, String> environmentVariables;
	private static final String weatherKey = "WEATHER_KEY";
	private static final String weatherURL = "https://api.openweathermap.org/data/3.0/onecall?units=metric&exclude=daily,minutely";
	private static final Integer maxHours = 12;
	
	public WeatherData getWeatherForecast (String lat, String lon) {

		String jsonResponse = getRemoteWeatherResponse(lat, lon);
		WeatherData response = new WeatherData();
		
    	JsonNode objectRequest = null;  // root node
    	
        try {
        	ObjectMapper OBJECT_MAPPER = new ObjectMapper();
        	objectRequest = OBJECT_MAPPER.readTree(jsonResponse);
        	
        	String eventString = OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL).writer().writeValueAsString(objectRequest);
        	
        	//logger.debug("jsonResponse: " + eventString);
        	
        } catch (Exception ex) {
        	ex.printStackTrace();
        	throw new RuntimeException(ex);
        }
        
        JsonNode hourlyForecast = objectRequest.at("/hourly");
        String timezoneOffset = objectRequest.at("/timezone_offset").asText();
        response.setTimezoneOffset(Long.valueOf(timezoneOffset));
        response.setServerTime(convertLinuxToDate(objectRequest.at("/current/dt").asText(),timezoneOffset));
        response.setSunrise(convertLinuxToDate(objectRequest.at("/current/sunrise").asText(),timezoneOffset));
        response.setSunset(convertLinuxToDate(objectRequest.at("/current/sunset").asText(),timezoneOffset));
        response.setTimezone(objectRequest.at("/timezone").asText());
        response.setLat(objectRequest.at("/lat").asText());
        response.setLon(objectRequest.at("/lon").asText());
        
        Integer hourCounter = 0;
        List<JsonNode> firstHours = new ArrayList<JsonNode>();
        
        if (hourlyForecast.isArray()) {
        	
        	List<WeatherDataHour> hours = new ArrayList<WeatherDataHour>();
        	response.setHours(hours);
        	
            for (JsonNode jsonNode : hourlyForecast) {
            	
            	if (hourCounter++ < maxHours) {
            		firstHours.add(jsonNode);
            	} else {
            		break;
            	}
            	
            	WeatherDataHour h = new WeatherDataHour();
            	hours.add(h);
            	String dtFieldNode = jsonNode.get("dt").asText();

            	Date dtDate = convertLinuxToDate(dtFieldNode,timezoneOffset);
                h.setHourDate(dtDate);
                ((ObjectNode)jsonNode).put("dt", h.getHourDateStr());// for AI
                
                if (h.getHourDate().compareTo(response.getSunrise()) < 0  
                		|| h.getHourDate().compareTo(response.getSunset()) > 0) {
                	
                	h.setIsDay(Boolean.FALSE);
                } else {
                	h.setIsDay(Boolean.TRUE);
                }
                
                h.setTemperature(jsonNode.get("temp").asText());
                h.setFeelTemperature(jsonNode.get("feels_like").asText());
                
                if (jsonNode.get("weather").isArray()) {
                	List<WeatherDataHourDetail> details = new ArrayList<WeatherDataHourDetail>();
                	h.setDetails(details);
                	for (JsonNode jsonNodeW : jsonNode.get("weather")) {
                		WeatherDataHourDetail detail = new WeatherDataHourDetail();
                		details.add(detail);
                		detail.setIcon(jsonNodeW.get("icon").asText());
                		detail.setDescription(jsonNodeW.get("description").asText());
                		detail.setMain(jsonNodeW.get("main").asText());
                		//logger.debug("nameFieldNode " + jsonNodeW.get("icon").asText());
                	}
                }
            }
        }
               
        response.setJsonHourly(getJsonFromArray(firstHours));
        
		logger.debug("getWeatherForecast response: "  + response);
		return response;
	}
	

	public String getRemoteWeatherResponse (String lat, String lon) {
		
		String locatedWeatherURL = weatherURL + "&lat=" + lat + "&lon=" + lon + "&appid=" + environmentVariables.get(weatherKey);
		//logger.debug("weatherURL " + locatedWeatherURL);
		
		
		String jsonResponse = null;
		
		try {
			HttpRequest request = HttpRequest.newBuilder()
					  .uri(new URI(locatedWeatherURL))
					  .GET()
					  .build();
			
			HttpResponse<String> response = HttpClient.newBuilder()
					  .build()
					  .send(request, HttpResponse.BodyHandlers.ofString());

			HttpHeaders responseHeaders = response.headers();
			jsonResponse = response.body();
			
		} catch (URISyntaxException ex) {
			logger.error(ex);
			throw new RuntimeException("");
		} catch (InterruptedException ex) {
			logger.error(ex);
			throw new RuntimeException("");
		} catch (IOException ex) {
			logger.error(ex);
			throw new RuntimeException("");
		}

		return jsonResponse;
	}
	
	private String getJsonFromArray (List<JsonNode> firstHours) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode array = mapper.valueToTree(firstHours);
		return array.toString();
	}
	
	private Date convertLinuxToDate(String linuxTime, String timezone_offset) {
		return Date.from(Instant.ofEpochSecond(Long.valueOf(linuxTime) + Long.valueOf(timezone_offset)));
	}


	public Map<String, String> getEnvironmentVariables() {
		return environmentVariables;
	}


	public void setEnvironmentVariables(Map<String, String> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}
	
	
}
