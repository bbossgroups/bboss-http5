package org.frameworkset.spi.ai.util;
/**
 * Copyright 2026 bboss
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
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.frameworkset.spi.ai.AIAgent;
import org.frameworkset.spi.ai.adapter.AgentAdapter;
import org.frameworkset.spi.ai.adapter.AgentAdapterFactory;
import org.frameworkset.spi.ai.model.*;
import org.frameworkset.spi.reactor.BaseStreamDataHandler;
import org.frameworkset.spi.reactor.ReactorCallException;
import org.frameworkset.spi.remote.http.BaseURLResponseHandler;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.frameworkset.util.concurrent.BooleanWrapperInf;
import org.frameworkset.util.concurrent.NoSynBooleanWrapper;
import org.slf4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author biaoping.yin
 * @Date 2026/1/11
 */
public class AIAgentUtil {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(AIAgentUtil.class);


    /**
     * 创建流式调用的Flux，使用默认数据源
     */
    public static Flux<String> streamChatCompletion(String url, Object message) {
        return streamChatCompletion((String)null , url, message);
    }

    /**
     * 创建流式调用的Flux,在指定的数据源上执行
     */
    public static Flux<String> streamChatCompletion(String poolName,String url,Object message) {
        return streamChatCompletion(  poolName,  url,  message,new BaseStreamDataHandler<String>() {
            @Override
            public boolean handle(String line, FluxSink<String> sink, BooleanWrapperInf firstEventTag) {
                return AIResponseUtil.handleStringData( this.agentAdapter, line, sink,   firstEventTag);

            }
            @Override

            public boolean handleException(Object requestBody,Throwable throwable, FluxSink<String> sink, BooleanWrapperInf firstEventTag){
                boolean result = AIResponseUtil.handleStringExceptionData(  throwable, sink,   firstEventTag);
                return result;
            }
        });

    }

    /**
     * 调用图片生成模型，生成图片
     * @param poolName
     * @param url
     * @param message
     * @return
     */
    public static ImageEvent multimodalImageGeneration(String poolName, String url, Object message) {
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolName);
        AgentAdapter agentAdapter = AgentAdapterFactory.getAgentAdapter(config,message);
        message = agentAdapter.buildGenImageRequestParameter(message);
        Map data = HttpRequestProxy.sendJsonBody(config,message,url,Map.class);
        ImageEvent imageEvent = agentAdapter.buildGenImageResponse(data);

        return imageEvent;
//        Map output = (Map)data.get("output");
//        List choices = (List)output.get("choices");
//        if(choices == null || choices.size() == 0)
//            return null;
//        Map choice = (Map)choices.get(0);
//        Map messageData = (Map)choice.get("message");
//        
//        String finishReason = (String)choice.get("finish_reason");
//        List imageContentData = (List)messageData.get("content");
//        
//        if(imageContentData != null ){
//            int size = imageContentData.size();
//            if(size > 0) {
//                imageEvent = new ImageEvent();
//                if(size == 1) {
//                    Map image = (Map) imageContentData.get(0);
//                    String imageUrl = (String) image.get("image");
//
//                    imageEvent.setImageUrl(imageUrl);
//                }
//                else{
//                    for(int i = 0; i < size; i++){
//                        Map image = (Map) imageContentData.get(i);
//                        String imageUrl = (String) image.get("image");
//                        imageEvent.addImageUrl(imageUrl);
//                    }
//                }
//                imageEvent.setFinishReason(finishReason);
//            }
//        }
//        return imageEvent;
    }

    /**
     * 调用图片生成模型，生成图片
     * @param url
     * @param message
     * @return
     */
    public static ImageEvent multimodalImageGeneration(String url, Object message) {

        return multimodalImageGeneration(null, url, message) ;
    }


    /**
     * 调用音频合成模型，生成音频
     * @param poolName
     * @param url
     * @param message
     * @return
     */
    public static AudioEvent multimodalAudioGeneration(String poolName, String url, Object message) {
        Map data = HttpRequestProxy.sendJsonBody(poolName,message,url,Map.class);
        Map output = (Map)data.get("output");
        Map audio = (Map)output.get("audio");
        String finishReason = (String)output.get("finish_reason");

        if(audio == null && finishReason == null)
            return null;
        AudioEvent audioEvent = new AudioEvent();
        audioEvent.setFinishReason(finishReason);
        String audioUrl = (String)audio.get("url");
        String auditData = (String)audio.get("data");
        Object expiresAt_ = audio.get("expires_at");
        if(expiresAt_ != null) {
            if (expiresAt_ instanceof Long) {
                audioEvent.setExpiresAt((Long) expiresAt_);
            } else {
                audioEvent.setExpiresAt((Integer) expiresAt_);
            }
        }
        audioEvent.setAudioBase64(auditData);
        audioEvent.setAudioUrl(audioUrl);


        return audioEvent;
    }

    /**
     * 调用音频合成模型，生成音频
     * @param url
     * @param message
     * @return
     */
    public static AudioEvent multimodalAudioGeneration(String url, Object message) {

        return multimodalAudioGeneration(null, url, message) ;
    }
    /**
     * 创建流式调用的Flux，使用默认数据源
     */
    public static Flux<ServerEvent> streamChatCompletionEvent(String url, Object message) {
        return streamChatCompletionEvent((String)null , url, message);
    }

    /**
     * 创建流式调用的Flux,在指定的数据源上执行
     */
    public static Flux<ServerEvent> streamChatCompletionEvent(String poolName,String url,Object message) {
        return streamChatCompletion(  poolName,  url,  message,new BaseStreamDataHandler<ServerEvent>() {
            @Override
            public boolean handle(String line, FluxSink<ServerEvent> sink, BooleanWrapperInf firstEventTag) {
                return AIResponseUtil.handleServerEventData(this.agentAdapter, this.isStream(), line, sink,   firstEventTag);

            }
            @Override

            public boolean handleException(Object requestBody,Throwable throwable, FluxSink<ServerEvent> sink, BooleanWrapperInf firstEventTag){
                boolean result = AIResponseUtil.handleServerEventExceptionData(  throwable, sink,   firstEventTag);
                return result;
            }
        });

    }

    /**
     * 同步调用模型服务，返回问答内容
     */
    public static ServerEvent chatCompletionEvent(String url,Object message) {
        return chatCompletionEvent(  (String)null,url,  message);


    }

    /**
     * 同步调用模型服务，返回问答内容
     */
    public static ServerEvent chatCompletionEvent(String poolName,String url,Object message) {
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolName);
        AgentAdapter agentAdapter = AgentAdapterFactory.getAgentAdapter(config,message);
        message = agentAdapter.buildOpenAIRequestParameter(message);
        String data = null;
        if(message != null){
            if(message instanceof String){
                data = (String)message;
            }
            else{
                data = SimpleStringUtil.object2json(message);
            }
        }
        return  HttpRequestProxy.sendJsonBody(config,data,url,null,new BaseURLResponseHandler<ServerEvent>(){
            @Override
            public ServerEvent handleResponse(ClassicHttpResponse response) throws IOException, ParseException {
                return AIResponseUtil.handleChatResponse(agentAdapter,url, response);
            }
        });


    }
    public static <T> Flux<T> streamChatCompletion(String url,Object message,BaseStreamDataHandler<T> streamDataHandler){
        return streamChatCompletion((String)null ,  url,  message, streamDataHandler);
    }

    /**
     * 创建流式调用的Flux,在指定的数据源上执行
     */
    public static <T> Flux<T> streamChatCompletion(String poolName,String url,Object chatMessage,BaseStreamDataHandler<T> streamDataHandler) {
        ClientConfiguration clientConfiguration = ClientConfiguration.getClientConfiguration(poolName);
        AgentAdapter agentAdapter = AgentAdapterFactory.getAgentAdapter(clientConfiguration,chatMessage);
        final ChatObject chatObject = agentAdapter.buildOpenAIRequestParameter(chatMessage);
        streamDataHandler.setStream(chatObject.isStream());
        streamDataHandler.setAgentAdapter(agentAdapter);
        return Flux.<T>create(sink -> {
                    Object data = null;
                    Object message = chatObject.getMessage();
                    try {


                        BaseURLResponseHandler responseHandler = new BaseURLResponseHandler<Void>() {
                            @Override
                            public Void handleResponse(ClassicHttpResponse response) throws IOException, ParseException {
                                streamDataHandler.setHttpUriRequestBase(httpUriRequestBase);
                                AIResponseUtil.handleStreamResponse(url, response, sink, streamDataHandler);
                                return null;

                            }
                        };
                        if (agentAdapter.getAIChatRequestType().equals(AIConstants.AI_CHAT_REQUEST_BODY_JSON)){
                            Map header = new LinkedHashMap();
                            if (chatObject.isStream()) {
                                header.put("Accept", "text/event-stream");
                            }

                            if (message != null) {
                                if (message instanceof String) {
                                    data = (String) message;
                                } else {
                                    data = SimpleStringUtil.object2json(message);
                                }
                            }

                            HttpRequestProxy.sendJsonBody(clientConfiguration, (String)data, url, header, responseHandler);
                        }
                        else if (agentAdapter.getAIChatRequestType().equals(AIConstants.AI_CHAT_REQUEST_POST_FORM)){
                            Map header = new LinkedHashMap();
                            if (chatObject.isStream()) {
                                header.put("Accept", "text/event-stream");
                            }
                            data = message;

                            HttpRequestProxy.httpPost(clientConfiguration, url,message,  header, responseHandler);
                        }
                        else {
                            throw new ReactorCallException("Unsupported request type: "+agentAdapter.getAIChatRequestType());
                        }
                    } catch (ReactorCallException e) {
//                        logger.error("流式请求失败：poolName["+poolName +"],url["+url +"],data:" + data);
                        streamDataHandler.handleException(data,e,sink,new NoSynBooleanWrapper( true));
//                        sink.error(e);
                    } catch (Exception e) {
                        streamDataHandler.handleException(data,e,sink,new NoSynBooleanWrapper( true));
//                        sink.error(new ReactorCallException("流式请求失败：poolName["+poolName +"],url["+url +"],", e));
                    }
                    catch (Throwable e) {
                        streamDataHandler.handleException(data,e,sink,new NoSynBooleanWrapper( true));
//                        sink.error(new ReactorCallException("流式请求失败：poolName["+poolName +"],url["+url +"],", e));
                    }
                }, FluxSink.OverflowStrategy.BUFFER)
                .subscribeOn(Schedulers.boundedElastic()) // 在弹性线程池中执行阻塞IO
                .timeout(Duration.ofSeconds(60)) // 设置超时
                .onErrorResume(throwable -> {
//                    String error = SimpleStringUtil.exceptionToString(throwable);
//                    System.err.println("流式处理错误: " + throwable.getMessage());
//                    String error = SimpleStringUtil.exceptionToString(throwable);
                    if(logger.isDebugEnabled()) {
                        logger.debug(throwable.getMessage(), throwable);
                    }
                    // 修改此处，将错误信息作为Flux输出
                    return Flux.empty();
                });
    }

}
