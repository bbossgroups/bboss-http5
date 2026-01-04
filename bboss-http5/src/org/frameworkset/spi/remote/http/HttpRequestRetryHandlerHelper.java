package org.frameworkset.spi.remote.http;
/**
 * Copyright 2008 biaoping.yin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 重试组件
 * <p>Copyright (c) 2018</p>
 * @Date 2018/12/18 22:10
 * @author biaoping.yin
 * @version 1.0
 */
public class HttpRequestRetryHandlerHelper implements HttpRequestRetryStrategy {
	private static Logger logger = LoggerFactory.getLogger(DefaultHttpRequestRetryHandler.class);
	private CustomHttpRequestRetryHandler httpRequestRetryHandler;
    private HttpRequestRetryStrategy defaultHttpRequestRetryHandler;
	private ClientConfiguration configuration ;
    private int retryTime;
	public HttpRequestRetryHandlerHelper(CustomHttpRequestRetryHandler httpRequestRetryHandler,ClientConfiguration configuration){
		
        if(httpRequestRetryHandler != null) {
            if(configuration.getRetryTime() > 0){
                retryTime = configuration.getRetryTime();
            }
            else{
                retryTime = 3;
            }
            this.httpRequestRetryHandler = httpRequestRetryHandler;
        }
        else{
            if (configuration.getRetryTime() > 0) {
                long interval = configuration.getRetryInterval();
                if(interval < 0){
                    interval = 1L;
                }
                this.defaultHttpRequestRetryHandler = new DefaultHttpRequestRetryStrategy(configuration.getRetryTime(),TimeValue.ofMilliseconds(interval));
            }
            else {
                this.defaultHttpRequestRetryHandler = DefaultHttpRequestRetryStrategy.INSTANCE;
            }
        }
        
		this.configuration = configuration;
	}

	@Override
	public boolean retryRequest(HttpRequest request, IOException exception, int executionCount, HttpContext context) {
		if(httpRequestRetryHandler != null){
			if (executionCount > retryTime) {
				return false;
			}
			if(httpRequestRetryHandler.retryRequest(  request,exception,executionCount,context,configuration)) {
				if (configuration.getRetryInterval() > 0) {
					try {
						Thread.sleep(configuration.getRetryInterval());
					} catch (InterruptedException e1) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		else if(defaultHttpRequestRetryHandler.retryRequest(request,exception,executionCount,context)){
			if (configuration.getRetryInterval() > 0) {
				try {
					Thread.sleep(configuration.getRetryInterval());
				} catch (InterruptedException e1) {
					return false;
				}
			}
			return true;
		}
		return false;

	}

    /**
     * Determines if a method should be retried given the response from
     * the target server.
     *
     * @param response  the response from the target server
     * @param execCount the number of times this method has been
     *                  unsuccessfully executed
     * @param context   the context for the request execution
     * @return {@code true} if the request should be retried, {@code false}
     * otherwise
     */
    @Override
    public boolean retryRequest(HttpResponse response, int execCount, HttpContext context) {
        return false;
    }

    /**
     * Determines the retry interval between subsequent retries.
     *
     * @param response  the response from the target server
     * @param execCount the number of times this method has been
     *                  unsuccessfully executed
     * @param context   the context for the request execution
     * @return the retry interval between subsequent retries
     */
    @Override
    public TimeValue getRetryInterval(HttpResponse response, int execCount, HttpContext context) {
        long interval = configuration.getRetryInterval();
        if(interval < 0){
            interval = 1L;
        }
        return TimeValue.ofMilliseconds(interval);
    }
}
