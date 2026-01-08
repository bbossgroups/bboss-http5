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
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import reactor.core.publisher.Flux;

/**
 * 智能体工具包
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class AIAgent {
    /**
     * 实现图片生成功能
     * @param maasName
     * @param url
     * @param imageAgentMessage
     * @return
     */
    public ImageEvent genImage(String maasName,String url,ImageAgentMessage imageAgentMessage){
        return HttpRequestProxy.multimodalImageGeneration(maasName,url,imageAgentMessage);
    }

    /**
     * 实现流式图片识别处理
     * @param maasName
     * @param url
     * @param imageVLAgentMessage
     * @return
     */
    public Flux<ServerEvent> streamImageParser(String maasName, String url, ImageVLAgentMessage imageVLAgentMessage){
        return HttpRequestProxy.streamChatCompletionEvent(maasName,url,imageVLAgentMessage);
    }


    /**
     * 实现流式智能问答功能,在指定的数据源上执行
     */
    public Flux<ServerEvent> streamChat(String maasName, String url, ChatAgentMessage chatAgentMessage){
        return HttpRequestProxy.streamChatCompletionEvent(maasName,url,chatAgentMessage);
    }

    /**
     * 实现同步图片识别处理
     * @param maasName
     * @param url
     * @param imageVLAgentMessage
     * @return
     */
    public ServerEvent imageParser(String maasName, String url, ImageVLAgentMessage imageVLAgentMessage){
        return HttpRequestProxy.chatCompletionEvent(maasName,url,imageVLAgentMessage);
    }


    /**
     * 实现同步智能问答,在指定的数据源上执行
     */
    public ServerEvent synChat(String maasName, String url, ChatAgentMessage chatAgentMessage){
        return HttpRequestProxy.chatCompletionEvent(maasName,url,chatAgentMessage);
    }

}
