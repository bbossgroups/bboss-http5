package org.frameworkset.spi.ai;
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

import org.frameworkset.spi.ai.model.*;
import org.frameworkset.spi.ai.util.AIAgentUtil;
import org.slf4j.Logger;
import reactor.core.publisher.Flux;

/**
 * 智能体工具包
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class AIAgent {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(AIAgent.class);
    /**
     * 实现图片生成功能
     * @param maasName
     * @param url
     * @param imageAgentMessage
     * @return
     */
    public ImageEvent genImage(String maasName,String url,ImageAgentMessage imageAgentMessage){
        return AIAgentUtil.multimodalImageGeneration(maasName,url,imageAgentMessage);
    }
    public ImageEvent genImage(String url,ImageAgentMessage imageAgentMessage){
        return AIAgentUtil.multimodalImageGeneration(url,imageAgentMessage);
    }
    /**
     * 调用音频合成模型，生成音频
     * @param url
     * @param audioAgentMessage
     * @return
     */
    public AudioEvent genAudio(String url, AudioAgentMessage audioAgentMessage){
        return AIAgentUtil.multimodalAudioGeneration(url,audioAgentMessage);
    }

    /**
     * 调用音频合成模型，生成音频
     * @param url
     * @param audioAgentMessage
     * @return
     */
    public Flux<ServerEvent> streamAudioGen(String url, AudioAgentMessage audioAgentMessage){
        return AIAgentUtil.streamAudioGenerationEvent(null,url,audioAgentMessage);
    }

    /**
     * 调用音频合成模型，生成音频
     * @param maasName
     * @param url
     * @param audioAgentMessage
     * @return
     */
    public Flux<ServerEvent> streamAudioGen(String maasName, String url, AudioAgentMessage audioAgentMessage){
        return AIAgentUtil.streamAudioGenerationEvent(maasName,url,audioAgentMessage);
    }
    /**
     * 调用音频合成模型，生成音频
     * @param maasName
     * @param url
     * @param audioAgentMessage
     * @return
     */
    public AudioEvent genAudio(String maasName, String url, AudioAgentMessage audioAgentMessage){
        return AIAgentUtil.multimodalAudioGeneration(maasName,url,audioAgentMessage);
    }

    /**
     * 实现流式音频识别处理
     * @param maasName
     * @param url
     * @param audioSTTAgentMessage
     * @return
     */
    public Flux<ServerEvent> streamAudioParser(String maasName, String url, AudioSTTAgentMessage audioSTTAgentMessage){
        return AIAgentUtil.streamChatCompletionEvent(maasName,url,audioSTTAgentMessage);
    }

    /**
     * 实现流式图片识别处理
     * @param maasName
     * @param url
     * @param imageVLAgentMessage
     * @return
     */
    public Flux<ServerEvent> streamImageParser(String maasName, String url, ImageVLAgentMessage imageVLAgentMessage){
        return AIAgentUtil.streamChatCompletionEvent(maasName,url,imageVLAgentMessage);
    }
    /**
     * 实现流式图片识别处理
     * @param url
     * @param imageVLAgentMessage
     * @return
     */
    public Flux<ServerEvent> streamImageParser( String url, ImageVLAgentMessage imageVLAgentMessage){
        return AIAgentUtil.streamChatCompletionEvent(url,imageVLAgentMessage);
    }
    /**
     * 实现流式智能问答功能,在指定的数据源上执行
     */
    public Flux<ServerEvent> streamChat(String maasName, String url, ChatAgentMessage chatAgentMessage){
        return AIAgentUtil.streamChatCompletionEvent(maasName,url,chatAgentMessage);
    }

    /**
     * 实现流式智能问答功能,在指定的数据源上执行
     */
    public Flux<ServerEvent> streamChat(String url, ChatAgentMessage chatAgentMessage){
        return AIAgentUtil.streamChatCompletionEvent(url,chatAgentMessage);
    }

    /**
     * 实现同步图片识别处理
     * @param url
     * @param imageVLAgentMessage
     * @return
     */
    public ServerEvent imageParser(String url, ImageVLAgentMessage imageVLAgentMessage){
        return AIAgentUtil.imageParser(url,imageVLAgentMessage);
    }

    /**
     * 实现同步图片识别处理
     * @param maasName
     * @param url
     * @param imageVLAgentMessage
     * @return
     */
    public ServerEvent imageParser(String maasName, String url, ImageVLAgentMessage imageVLAgentMessage){
        return AIAgentUtil.imageParser(maasName,url,imageVLAgentMessage);
    }


    /**
     * 实现同步音频识别处理
     * @param maasName
     * @param url
     * @param audioSTTAgentMessage
     * @return
     */
    public ServerEvent audioParser(String maasName, String url, AudioSTTAgentMessage audioSTTAgentMessage){
        return AIAgentUtil.audioParser(maasName,url,audioSTTAgentMessage);
    }
    /**
     * 实现同步智能问答,在指定的数据源上执行
     */
    public ServerEvent chatCompletionEvent(String maasName, String url, ChatAgentMessage chatAgentMessage){
        return AIAgentUtil.chatCompletionEvent(maasName,url,chatAgentMessage);
    }

    /**
     * 实现同步智能问答,在指定的数据源上执行
     */
    public ServerEvent chatCompletionEvent( String url, ChatAgentMessage chatAgentMessage){
        return AIAgentUtil.chatCompletionEvent(url,chatAgentMessage);
    }



}
