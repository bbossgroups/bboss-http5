package org.frameworkset.spi.ai.adapter;
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
import org.frameworkset.spi.ai.model.ChatObject;
import org.frameworkset.spi.ai.model.ImageAgentMessage;
import org.frameworkset.spi.ai.model.ImageEvent;
import org.frameworkset.spi.ai.model.ChatAgentMessage;
import org.frameworkset.spi.ai.util.MessageBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能体适配器：针对不同厂家的模型平台服务进行适配，包括请求参数转换、结果转换等
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public abstract class AgentAdapter {
    /**
     * 构建生成图片请求参数
     * @param imageAgentMessage
     * @return
     */
    protected abstract Map buildGenImageRequestMap(ImageAgentMessage imageAgentMessage);


    /**
     * 构建智能问答请求参数
     * @param chatAgentMessage
     * @return
     */
    protected Map buildOpenAIRequestMap(ChatAgentMessage chatAgentMessage){
        String message = chatAgentMessage.getMessage();
        Map<String, Object> userMessage = MessageBuilder.buildUserMessage( message);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", chatAgentMessage.getModel());

        List<Map<String, Object>> messages = null;
        List<Map<String, Object>> sessionMemory = chatAgentMessage.getSessionMemory();
        if(sessionMemory != null){
            // 构建消息历史列表，包含之前的会话记忆
            messages = new ArrayList<>(sessionMemory);
            // 添加当前用户消息
            sessionMemory.add(userMessage);
        }
        else{
            messages = new ArrayList<>();
        }
        messages.add(userMessage);

        requestMap.put("messages", messages);
        Map parameters = chatAgentMessage.getParameters();
        if(SimpleStringUtil.isNotEmpty( parameters)){
            requestMap.putAll(parameters);
        }
        else {
            //设置默认参数
            requestMap.put("stream", true);
            requestMap.put("max_tokens", 8192);
            requestMap.put("temperature", 0.7);
        }
        return requestMap;
    }
    public abstract ImageEvent buildGenImageResponse(Map imageData);

    public Object buildGenImageRequestParameter(Object imageAgentMessage){
        if(imageAgentMessage instanceof ImageAgentMessage){
            return buildGenImageRequestMap((ImageAgentMessage)imageAgentMessage);
        }
        else{
            return imageAgentMessage;
        }
    }

    public ChatObject buildOpenAIRequestParameter(Object agentMessage){
        ChatObject chatObject = new ChatObject();
        Map parameters = null;
        Boolean stream = false;
        if(agentMessage instanceof ChatAgentMessage){
            parameters = buildOpenAIRequestMap((ChatAgentMessage)agentMessage);
            stream = (Boolean)parameters.get("stream");
            agentMessage = parameters;
        }
        else if(agentMessage instanceof Map){
            parameters =  (Map)agentMessage;
            stream = (Boolean)parameters.get("stream");
            
        }
         

        if(stream == null){
            stream = false;
        }
       
        chatObject.setMessage(agentMessage);
        chatObject.setStream(stream);
        return chatObject;
    }

}
