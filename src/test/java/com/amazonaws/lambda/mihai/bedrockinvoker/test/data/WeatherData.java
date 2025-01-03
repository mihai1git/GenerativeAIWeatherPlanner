package com.amazonaws.lambda.mihai.bedrockinvoker.test.data;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.amazonaws.lambda.mihai.bedrockinvoker.model.WeatherException;
import com.amazonaws.lambda.mihai.bedrockinvoker.service.WeatherService;
import com.amazonaws.lambda.mihai.bedrockinvoker.test.utils.HttpResponseImpl;
import com.amazonaws.lambda.mihai.bedrockinvoker.test.utils.TestUtils;

public class WeatherData {

	private static Logger logger = LogManager.getLogger(WeatherData.class);
	
	public static void resetWeatherData (HttpClient weatherHttpClient) {
		
		
		try {
			when(weatherHttpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenAnswer(new Answer<HttpResponse<String>>() {
				
				public HttpResponse<String> answer(InvocationOnMock invocation) throws Throwable {
					Map<String,List<String>> headersMap = new HashMap<String, List<String>>();
					BiPredicate<String, String> lambda = (a, b) -> a.equals(b);
					HttpHeaders headers = HttpHeaders.of(headersMap, lambda);
					
					URI uri = ((HttpRequest)invocation.getArgument(0)).uri();
					logger.debug(uri.toString());
					
					String data = TestUtils.readFromProjectResource("src/test/resources/weather_response_1.json");
					
					HttpResponse<String> res = new HttpResponseImpl<String>(invocation.getArgument(0), 200, headers, uri, data);
										
					if (uri.toString().contains("&lat=45")) {
			    		data = TestUtils.readFromProjectResource("src/test/resources/weather_response_2.json");
			    		res = new HttpResponseImpl<String>(invocation.getArgument(0), 200, headers, uri, data);
			    	}
					
					if (uri.toString().contains("&lat=1000")) {
						res = new HttpResponseImpl<String>(invocation.getArgument(0), 400, headers, uri, null);
					}
					
					return res;
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void resetWeatherData (WeatherService weatherService) {
		
		try {
			when(weatherService.getWeatherForecast(Mockito.any(String.class), Mockito.any(String.class))).thenCallRealMethod();
			
			when(weatherService.getRemoteWeatherResponse(Mockito.any(String.class), Mockito.any(String.class))).thenAnswer(new Answer<String>() {
				
			     public String answer(InvocationOnMock invocation) throws Throwable {
			    	 
			    	String data = TestUtils.readFromProjectResource("src/test/resources/weather_response_1.json");
			    	//logger.debug(invocation.getArgument(0).toString());
			    	
			    	if (invocation.getArgument(0).equals("45")) {
			    		data = TestUtils.readFromProjectResource("src/test/resources/weather_response_2.json");
			    	}
			    	
			    	if (invocation.getArgument(0).equals("1000")) {
						WeatherException ex = new WeatherException();
						ex.setStatusCode(500);
						throw ex;
			    	}
			    	
			    	return data;
			     }
			 });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
