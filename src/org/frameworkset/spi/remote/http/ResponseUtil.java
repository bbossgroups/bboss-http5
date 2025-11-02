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

import com.frameworkset.util.SimpleStringUtil;
import org.frameworkset.spi.ai.model.StreamData;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.impl.io.EmptyInputStream;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.frameworkset.spi.ai.model.ServerEvent;
import org.frameworkset.spi.remote.http.proxy.BBossEntityUtils;
import org.frameworkset.spi.remote.http.proxy.HttpProxyRequestException;
import org.frameworkset.spi.remote.http.proxy.InvokeContext;
import org.frameworkset.spi.remote.http.reactor.*;
import org.frameworkset.util.concurrent.BooleanWrapperInf;
import org.frameworkset.util.concurrent.NoSynBooleanWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

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

    public static boolean handleStringData(String line,FluxSink<String> sink, BooleanWrapperInf firstEventTag){
        if(logger.isDebugEnabled()){
            logger.debug("line: " + line);
        }
        if (line.startsWith("data: ") || line.startsWith("data:")) {
            String data = line.substring(5).trim();

            if ("[DONE]".equals(data)) {                
                return true;
            }            
            if (!data.isEmpty()) {
                if(firstEventTag.get()) {
                    firstEventTag.set(false);
                }
                StreamData content = ResponseUtil.parseStreamContentFromData(data);
                if (content != null && !content.isEmpty()) {
                    sink.next(content.getData());
                }
            }
        }
        else{
            if(logger.isDebugEnabled()) {
                logger.debug("streamChatCompletion: " + line);
            }
        }
        return false;
    }

 
    public static boolean handleServerEventData(String line,FluxSink<ServerEvent> sink, BooleanWrapperInf firstEventTag){
        if(logger.isDebugEnabled()){
            logger.debug("line: " + line);
        }
        if (line.startsWith("data: ")||line.startsWith("data:")) {
            String data = line.substring(5).trim();

            if ("[DONE]".equals(data)) {
                 
                ServerEvent serverEvent = new ServerEvent();
                if(firstEventTag.get()) {
                    firstEventTag.set(false);
                    serverEvent.setFirst(true);
                }
                serverEvent.setType(ServerEvent.DATA);
                serverEvent.setDone(true);
                sink.next(serverEvent);
                return true;
            }
            if (!data.isEmpty()) {
                StreamData content = ResponseUtil.parseStreamContentFromData(data);
                if (content != null) {
                   if( !content.isEmpty()) {
                       ServerEvent serverEvent = new ServerEvent();
                       if (firstEventTag.get()) {
                           firstEventTag.set(false);
                           serverEvent.setFirst(true);
                       }
                       serverEvent.setFinishReason(content.getFinishReason());
                       serverEvent.setData(content.getData());
                       serverEvent.setType(ServerEvent.DATA);
                       serverEvent.setContentType(content.getType());
                       sink.next(serverEvent);
                   }
                   else if(content.getFinishReason() != null && content.getFinishReason().length() > 0){
                       ServerEvent serverEvent = new ServerEvent();
                       if (firstEventTag.get()) {
                           firstEventTag.set(false);
                           serverEvent.setFirst(true);
                       }
                       serverEvent.setFinishReason(content.getFinishReason());
                       serverEvent.setType(ServerEvent.DATA);
                       serverEvent.setContentType(content.getType());
                       sink.next(serverEvent);
                   }
                }
                
            }
        }
        else{
            if(logger.isDebugEnabled()) {
                logger.debug("streamChatCompletion: {}",line);
            }
        }
        return false;
    }

    public static boolean handleStringExceptionData(Throwable throwable,FluxSink<String> sink, BooleanWrapperInf firstEventTag){
        if(logger.isWarnEnabled()) {
            logger.warn("服务端异常：", throwable);
        }
        if(firstEventTag.get()) {
            firstEventTag.set(false);
        }
        String error = SimpleStringUtil.exceptionToString(throwable);
        sink.next(error);
        sink.complete();
        return true;
        
    }

    public static boolean handleServerEventExceptionData(Throwable throwable,FluxSink<ServerEvent> sink, BooleanWrapperInf firstEventTag){
        if(logger.isWarnEnabled()) {
            logger.warn("服务端异常：", throwable);
        }
        
        String error = SimpleStringUtil.exceptionToString(throwable);
        ServerEvent serverEvent = new ServerEvent();
        if(firstEventTag.get()) {
            firstEventTag.set(false);
            serverEvent.setFirst(true);
        }
        serverEvent.setData(error);
        serverEvent.setType(ServerEvent.ERROR);
        sink.next(serverEvent);

        serverEvent = new ServerEvent();
        
        serverEvent.setDone( true);
        sink.next(serverEvent);
        sink.complete();
        return true;

    }

    private static StreamData parseAudioStreamContentFromData(Map output){
        try {
            
            Object choices_ = output.get("choices");            
            if (choices_ != null ) {
                if (choices_ instanceof List) {
                    List<Map> choices = (List<Map>) choices_;
                    if (choices.size() > 0) {
                        Map choice = choices.get(0);
                        Map message = (Map) choice.get("message");
                     
                        if(message != null) {
                            List<Map> content_ = (List) message.get("content");
                            
                            String content = content_ != null && content_.size() > 0? (String) content_.get(0).get("text"):null;
                            List<Map> reasoning_content_ = (List) message.get("reasoning_content");
                            String reasoning_content = reasoning_content_ != null && reasoning_content_.size() > 0?(String) reasoning_content_.get(0).get("text"):null;
                            String finishReason = (String) choice.get("finish_reason");
                            if (SimpleStringUtil.isNotEmpty(reasoning_content)) {
                                return new StreamData(ServerEvent.REASONING_CONTENT, reasoning_content, finishReason);
                            } else {
                                return new StreamData(ServerEvent.CONTENT, content, finishReason);
                            }
                        }
                        else{
                            if(logger.isDebugEnabled())
                                logger.debug("choices list message null");
                        }
                       
                    }
                    else {
                        if(logger.isDebugEnabled())
                            logger.debug("choices list size is 0");
                    }

                }
                else{
                    if (logger.isDebugEnabled())
                        logger.debug("choices is not list:{}");
                }
            }
           
        } catch (Exception e) {
            throw new ReactorCallException("ParseAudioStreamContentFromData failed:",e);
        }
        return null;
    }
    /**
     * 语音识别：data:{"output":{"choices":[{"message":{"annotations":[{"type":"audio_info","language":"zh","emotion":"neutral"}],"content":[{"text":"欢迎与"}],"role":"assistant"},"finish_reason":"null"}]},"usage":{"output_tokens_details":{"text_tokens":6},"input_tokens_details":{"text_tokens":16},"seconds":1},"request_id":"e84128d5-4bae-4e7e-91ab-6fb33504d2e3"}
     * LLM和图像识别：data: {"id":"ccf32be6-ad2f-4658-963a-fc3c22346e6b","object":"chat.completion.chunk","created":1761725211,"model":"deepseek-reasoner","system_fingerprint":"fp_ffc7281d48_prod0820_fp8_kvcache","choices":[{"index":0,"delta":{"content":null,"reasoning_content":"在"},"logprobs":null,"finish_reason":null}]}
     * @param data
     * @return
     */
    public static StreamData parseStreamContentFromData(String data) {
        try {
            Map map = SimpleStringUtil.json2Object(data,Map.class);
            Object choices_ = map.get("choices");
            if(choices_ == null){
                Map output = (Map) map.get("output");
                if(output != null){ 
                    return parseAudioStreamContentFromData(output);
                }
            }
            if (choices_ != null ) {
                if (choices_ instanceof List) {
                    List<Map> choices = (List<Map>) choices_;
                    if (choices.size() > 0) {
                        Map choice = choices.get(0);
                        String finishReason = (String) choice.get("finish_reason");
                        Map delta = (Map) choice.get("delta");
                        if (delta != null) {
//                            String content = (String)delta.get("content");
//                            return content;
                            String reasoning_content = (String)delta.get("reasoning_content");
                            String content = (String) delta.get("content");
                            if(SimpleStringUtil.isNotEmpty(reasoning_content)){
                                return new StreamData(ServerEvent.REASONING_CONTENT,reasoning_content,finishReason);
                            }
                            else{
                                return new StreamData(ServerEvent.CONTENT,content,finishReason);
                            }
                            

                        }
                        else{
                            if(logger.isDebugEnabled())
                                logger.debug("choices list delta null: {}",data);
                        }
                    }
                    else {
                        if(logger.isDebugEnabled())
                            logger.debug("choices list size is 0: {}",data);
                    }

                }
                else{
                    if (logger.isDebugEnabled())
                        logger.debug("choices is not list:{}", data);
                }
            }
            else {
                String code =  (String)map.get("code");
                String message = (String) map.get("message");
                if(SimpleStringUtil.isNotEmpty(code)) {
                    return new StreamData(ServerEvent.CONTENT, message, code);
                }
                else {
                    if(logger.isDebugEnabled())
                        logger.debug("-----------no choices:{}",data);
                }
                
            }
        } catch (Exception e) {
            throw new ReactorCallException(data,e);
        }
        return null;
    }

    private static <T> void processStreamResponse(ClassicHttpResponse response, FluxSink<T> sink, StreamDataHandler<T> streamDataHandler) throws IOException {

        FluxSinkStatus fluxSinkStatus = null;
        try  {
            fluxSinkStatus = new FluxSinkStatus(response,streamDataHandler.getHttpUriRequestBase());
             
//            // 添加取消监听器
//            sink.onCancel(() -> {
//                // 当订阅被取消时执行
//                logger.info("Subscription cancelled");
//                fluxSinkStatus.cancel();
//                // 执行清理工作
//            });
            final FluxSinkStatus fluxSinkStatus_ = fluxSinkStatus;
            // 添加处置监听器
            sink.onDispose(() -> {
                // 当 sink 被处置时执行（包括正常完成、错误和取消）
                if(logger.isDebugEnabled()) {
                    logger.debug("Sink disposed");
                }
                fluxSinkStatus_.dispose();
                // 执行清理工作
                fluxSinkStatus_.releaseResources();
                
            });
            String line;
            boolean needBreak = false;
            BooleanWrapperInf firstEventTag = new NoSynBooleanWrapper(true);
            while (!sink.isCancelled() && (line = fluxSinkStatus.readLine()) != null ) {
                if(fluxSinkStatus.isDispose()){
                    break;
                }
                needBreak = streamDataHandler.handle(line, sink,   firstEventTag);
                if(needBreak){
                    sink.complete();
                    break;
                }
               
                
            }
            if(!needBreak){
//                ServerEvent serverEvent = new ServerEvent();
//                if(firstEventTag.get()) {
//                    firstEventTag.set(false);
//                    serverEvent.setFirst(true);
//                }
//                serverEvent.setType(ServerEvent.DATA);
//                serverEvent.setDone(true);
//                sink.next(serverEvent);
                streamDataHandler.handle("data:[DONE]", sink,   firstEventTag);
                sink.complete();
            }
        }
        finally {
            fluxSinkStatus.releaseResources();
        }
    }


    public static <T> void handleStreamResponse(String url, ClassicHttpResponse response, 
                                                FluxSink<T> sink, StreamDataHandler<T> streamDataHandler)
            throws IOException, ParseException {
         
        int status = response.getCode();       
        
        if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
            processStreamResponse(response, sink,streamDataHandler);
        } else {
            HttpEntity entity = response.getEntity();
            if (entity != null ) {
                if (logger.isDebugEnabled()) {
                    logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
                }
                throw new ReactorCallException(new StringBuilder().append("Request url:")
                        .append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString()));
            }
            else {
                throw new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status).toString()));
            }
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
