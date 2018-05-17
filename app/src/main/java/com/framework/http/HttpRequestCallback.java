package com.framework.http;

public interface HttpRequestCallback {
	
	void requestBefore();

	void requestAfter(HttpResponse response);

}
