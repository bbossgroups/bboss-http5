package org.frameworkset.spi.remote.http;

import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

public class HttpConnectionKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy{
	private long keepAlive ;
	public HttpConnectionKeepAliveStrategy(long keepAlive) {
		this.keepAlive = keepAlive;
	}
	
	 @Override  
     public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
         TimeValue keepAlive = super.getKeepAliveDuration(response, context);  
         if (keepAlive.getDuration() == -1) {  
             keepAlive = TimeValue.ofMilliseconds(this.keepAlive);  
         }  
         return keepAlive;  
     }  

}
