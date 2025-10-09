package org.frameworkset.spi.remote.http;
/**
 * Copyright 2022 bboss
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

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.frameworkset.util.SimpleStringUtil;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import  org.apache.hc.core5.http.HttpResponse;
import  org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.core5.http.ParseException;
import  org.apache.hc.core5.http.impl.io.EmptyInputStream;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.frameworkset.spi.remote.http.proxy.BBossEntityUtils;
import org.frameworkset.spi.remote.http.proxy.HttpProxyRequestException;
import org.frameworkset.spi.remote.http.proxy.InvokeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2022/5/28
 * @author biaoping.yin
 * @version 1.0
 */
public class ResponseUtil {

	private static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
	public static <K,T> Map<K,T> handleMapResponse(String url,HttpResponse response_,Class<K> keyType,Class<T> beanType)
            throws ClientProtocolException, IOException, ParseException {
        ClassicHttpResponse response = (ClassicHttpResponse)response_;
		int status = response.getCode();

		if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson2Map(  entity,  keyType,  beanType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).append(",error:").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}


	public static <T> List<T> handleListResponse(String url,HttpResponse response_, Class<T> resultType)
            throws ClientProtocolException, IOException, ParseException {

        ClassicHttpResponse response = (ClassicHttpResponse)response_;
		int status = response.getCode();

		if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson2List(  entity,  resultType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).append(",error:").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}
	public static <T> Set<T> handleSetResponse(String url,HttpResponse response_, Class<T> resultType)
            throws IOException, ParseException {
        ClassicHttpResponse response = (ClassicHttpResponse)response_;
		int status = response.getCode();

		if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson2Set(  entity,  resultType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).append(",error:").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}
	public static String handleStringResponse(String url, HttpResponse response_, InvokeContext invokeContext)
            throws IOException, ParseException {
        ClassicHttpResponse response = (ClassicHttpResponse)response_;
        if(invokeContext == null || invokeContext.getResponseCharset() == null)
            return handleStringResponse( url, response);
		int status = response.getCode();

		if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
			HttpEntity entity = response.getEntity();
			return entity != null ? BBossEntityUtils.toString(entity,invokeContext.getResponseCharset()) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null )
				throw new HttpProxyRequestException(new StringBuilder().append("send request to ")
						.append(url).append(" failed,").append("status=").append(status).append(":")
						.append(BBossEntityUtils.toString(entity,invokeContext.getResponseCharset())).toString());
			else
				throw new HttpProxyRequestException(new StringBuilder().append("send request to ")
                        .append(url).append(",Unexpected response status: " ).append( status).toString());
		}
	}

    public static String handleStringResponse(String url, HttpResponse response_)
            throws IOException, ParseException {
        ClassicHttpResponse response = (ClassicHttpResponse)response_;
        int status = response.getCode();

        if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
            HttpEntity entity = response.getEntity();
            return entity != null ? BBossEntityUtils.toString(entity) : null;
        } else {
            HttpEntity entity = response.getEntity();
            if (entity != null )
                throw new HttpProxyRequestException(new StringBuilder().append("send request to ")
                        .append(url).append(" failed,").append("status=").append(status).append(":")
                        .append(BBossEntityUtils.toString(entity)).toString());
            else
                throw new HttpProxyRequestException(new StringBuilder().append("send request to ").append(url).append(",Unexpected response status: " ).append( status).toString());
        }
    }
	public static <T> T handleResponse(String url,HttpResponse response, Class<T> resultType)
            throws IOException, ParseException {
		if(resultType != null  ){
			if(resultType.isAssignableFrom(String.class)) {
				return (T) handleStringResponse(url, response);
			}
			else if(resultType.isAssignableFrom(Integer.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
                    Object v = Integer.parseInt(value);
                    return (T)v;
				}
			}
			else if(resultType.isAssignableFrom(int.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
                    Object v = 0;
                    return (T)v;
//					return (T)new Integer(0);
				}
				else{
                    Object v = Integer.parseInt(value);
                    return (T)v;
//					return (T)Integer.valueOf(Integer.parseInt(value));
				}
			}
			else if(resultType.isAssignableFrom(Long.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
                    Object v = Long.parseLong(value);
                    return (T)v;
				}
			}
			else if(resultType.isAssignableFrom(long.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
                    Object v = 0l;
                    return (T)v;
//					return (T)new Long(0l);
				}
				else{
                    Object v = Long.parseLong(value);
                    return (T)v;
//					return (T)Long.valueOf(Long.parseLong(value));
				}
			}
			else if(resultType.isAssignableFrom(Short.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
                    Object v = Short.parseShort(value);
                    return (T)v;
//					return (T)Short.valueOf(Short.parseShort(value));
				}
			}
			else if(resultType.isAssignableFrom(short.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
                    Object v = (short)0;
                    return (T)v;
//					return (T)new Short((short)0);
				}
				else{
                    Object v = Short.parseShort(value);
                    return (T)v;
//					return (T)Short.valueOf(Short.parseShort(value));
				}
			}
			else if(resultType.isAssignableFrom(Float.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
                    Object v = Float.parseFloat(value);
                    return (T)v;
//					return (T)Float.valueOf(Float.parseFloat(value));
				}
			}
			else if(resultType.isAssignableFrom(float.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
                    Object v = 0f;
					return (T)v;
				}
				else{
                    Object v = Float.parseFloat(value);
                    return (T)v;
//					return (T)Float.valueOf(Float.parseFloat(value));
				}
			}
			else if(resultType.isAssignableFrom(Double.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
                    Object v = Double.parseDouble(value);
                    return (T)v;
				}
			}
			else if(resultType.isAssignableFrom(double.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
                    Object v = 0d;
                    return (T)v;
//					return (T)new Double(0d);
				}
				else{
                    Object v = Double.parseDouble(value);
                    return (T)v;
//					return (T)Double.valueOf(Double.parseDouble(value));
				}
			}
			else if(resultType.isAssignableFrom(Boolean.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
					return (T)Boolean.valueOf(Boolean.parseBoolean(value));
				}
			}
			else if(resultType.isAssignableFrom(boolean.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
                    Object v = false;
                    return (T)v;
				}
				else{
                    Object v = Boolean.parseBoolean(value);
                    return (T)v;
//					return (T)Boolean.valueOf(Boolean.parseBoolean(value));
				}
			}
		}
        ClassicHttpResponse response_ = (ClassicHttpResponse)response;
		int status = response_.getCode();

		if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
			HttpEntity entity = response_.getEntity();
			return entity != null ? converJson(  entity,  resultType) : null;
		} else {
			HttpEntity entity = response_.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}

    /**
     * 2xx状态为正常状态
     * @param status
     * @return
     */
    public static boolean isHttpStatusOK(int status){
        return status >= 200 && status < 300;
    }
	public static <D,T> D handleResponse(String url,HttpResponse response_,Class<D> containType, Class<T> resultType)
            throws IOException, ParseException {
        ClassicHttpResponse response = (ClassicHttpResponse)response_;
		int status = response.getCode();

		if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson(  entity, containType, resultType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}

    /**
     * 判断响应报文是否为空
     * @param entity
     * @param inputStream
     * @return
     * @throws IOException
     */
	public static boolean entityEmpty(HttpEntity entity,InputStream inputStream) throws IOException {
        
		if(inputStream instanceof EmptyInputStream )
			return true;
		return false;

	}
	public static <T> T converJson(HttpEntity entity, Class<T> clazz) throws IOException {
		InputStream inputStream = null;

		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}

			return SimpleStringUtil.json2Object(inputStream, clazz);
		}catch (IllegalArgumentException e) {
            String message = e.getMessage();//No content to map due to end-of-input
            if (message != null && message.contains("No content to map")) {
                // 处理空输入情况
                return null; // 或返回默认实例
            }
            throw e;
        } finally {
			inputStream.close();
		}

	}

	public static <D,T> D converJson(HttpEntity entity, Class<D> containType ,Class<T> clazz) throws IOException {
		InputStream inputStream = null;

		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}
			return SimpleStringUtil.json2TypeObject(inputStream,containType, clazz);
		} catch (IllegalArgumentException e) {
            String message = e.getMessage();//No content to map due to end-of-input
            if (message != null && message.contains("No content to map")) {
                // 处理空输入情况
                return null; // 或返回默认实例
            }
            throw e;
        }finally {
			inputStream.close();
		}

	}

	public static <T> List<T> converJson2List(HttpEntity entity, Class<T> clazz) throws IOException {
		InputStream inputStream = null;

		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}
			return SimpleStringUtil.json2ListObject(inputStream, clazz);
		} catch (IllegalArgumentException e) {
            String message = e.getMessage();//No content to map due to end-of-input
            if (message != null && message.contains("No content to map")) {
                // 处理空输入情况
                return null; // 或返回默认实例
            }
            throw e;
        }finally {
			if(inputStream != null)
				inputStream.close();
		}

	}

	public static <T> Set<T> converJson2Set(HttpEntity entity, Class<T> clazz) throws IOException {
		InputStream inputStream = null;

		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}
			return SimpleStringUtil.json2LSetObject(inputStream, clazz);
		} catch (IllegalArgumentException e) {
            String message = e.getMessage();//No content to map due to end-of-input
            if (message != null && message.contains("No content to map")) {
                // 处理空输入情况
                return null; // 或返回默认实例
            }
            throw e;
        }finally {
			inputStream.close();
		}

	}

	public static <K,T> Map<K,T> converJson2Map(HttpEntity entity, Class<K> keyType, Class<T> beanType) throws IOException {
		InputStream inputStream = null;

		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}
			return SimpleStringUtil.json2LHashObject(inputStream,  keyType, beanType);
		} catch (IllegalArgumentException e) {
            String message = e.getMessage();//No content to map due to end-of-input
            if (message != null && message.contains("No content to map")) {
                // 处理空输入情况
                return null; // 或返回默认实例
            }
            throw e;
        }finally {
			inputStream.close();
		}

	}
}
