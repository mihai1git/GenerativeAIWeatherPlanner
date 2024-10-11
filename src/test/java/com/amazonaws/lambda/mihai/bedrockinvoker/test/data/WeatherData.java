package com.amazonaws.lambda.mihai.bedrockinvoker.test.data;

import static org.mockito.Mockito.when;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.amazonaws.lambda.mihai.bedrockinvoker.service.WeatherService;
import com.amazonaws.lambda.mihai.bedrockinvoker.test.utils.TestUtils;

public class WeatherData {

	private static Logger logger = LogManager.getLogger(WeatherData.class);
	
	public static void resetWeatherData (WeatherService weatherService) {
		
		when(weatherService.getWeatherForecast(Mockito.any(String.class), Mockito.any(String.class))).thenCallRealMethod();
		
		when(weatherService.getRemoteWeatherResponse(Mockito.any(String.class), Mockito.any(String.class))).thenAnswer(new Answer<String>() {
			
		     public String answer(InvocationOnMock invocation) throws Throwable {
		    	 
		    	String data = TestUtils.readFromProjectResource("src/test/resources/weather_response_1.json");
		    	//logger.debug(data);
		    	return data;
		     }
		 });
	}

}
