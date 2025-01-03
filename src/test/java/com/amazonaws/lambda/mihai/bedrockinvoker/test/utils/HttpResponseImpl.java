package com.amazonaws.lambda.mihai.bedrockinvoker.test.utils;

import java.net.URI;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import javax.net.ssl.SSLSession;

public class HttpResponseImpl<T> implements HttpResponse<T> {

	private HttpRequest initialRequest;
    private int responseCode;
    private HttpHeaders headers;
    private T body;
    private URI uri;
    
    public HttpResponseImpl(
    		HttpRequest initialRequest,
    		int responseCode,
    		HttpHeaders headers,
    		URI uri,
    		T body) {
    	
    	this.initialRequest = initialRequest;
    	this.responseCode = responseCode;
    	this.headers = headers;
    	this.body = body;
    	this.uri = uri;
    }
    
	@Override
	public int statusCode() {
		// TODO Auto-generated method stub
		return responseCode;
	}

	@Override
	public HttpRequest request() {
		// TODO Auto-generated method stub
		return initialRequest;
	}

	@Override
	public Optional<HttpResponse<T>> previousResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpHeaders headers() {
		// TODO Auto-generated method stub
		return headers;
	}

	@Override
	public T body() {
		// TODO Auto-generated method stub
		return body;
	}

	@Override
	public Optional<SSLSession> sslSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI uri() {
		// TODO Auto-generated method stub
		return uri;
	}

	@Override
	public Version version() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
