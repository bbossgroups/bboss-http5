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

import org.frameworkset.spi.ai.model.ChatAgentMessage;
import org.frameworkset.spi.ai.model.ImageAgentMessage;
import org.frameworkset.spi.ai.model.ImageEvent;
import org.frameworkset.spi.ai.model.ServerEvent;
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import reactor.core.publisher.Flux;

/**
 * 智能体工具包
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class AIAgent {
    /**
     * 生成图片
     * @param maasName
     * @param url
     * @param imageAgentMessage
     * @return
     */
    public ImageEvent genImage(String maasName,String url,ImageAgentMessage imageAgentMessage){
        return HttpRequestProxy.multimodalImageGeneration(maasName,url,imageAgentMessage);
    }

    /**
     * 创建流式调用的Flux,在指定的数据源上执行
     */
    public Flux<ServerEvent> streamChat(String maasName, String url, ChatAgentMessage chatAgentMessage){
        return HttpRequestProxy.streamChatCompletionEvent(maasName,url,chatAgentMessage);
    }

}
