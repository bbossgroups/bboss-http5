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
import org.frameworkset.spi.ai.model.*;
import org.frameworkset.spi.ai.util.AIResponseUtil;
import org.frameworkset.spi.ai.util.MessageBuilder;
import org.frameworkset.spi.remote.http.ResponseUtil;
import org.frameworkset.util.concurrent.BooleanWrapperInf;
import reactor.core.publisher.FluxSink;

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
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AgentAdapter.class);
    /**
     * 构建生成图片请求参数
     * @param imageAgentMessage
     * @return
     */
    protected abstract Map buildGenImageRequestMap(ImageAgentMessage imageAgentMessage);

    protected abstract Map buildImageVLRequestMap(ImageVLAgentMessage imageAgentMessage);
    public boolean isDone(String data){
        return "[DONE]".equals(data);

    }
    
    public String getDoneData(){
        return "data:[DONE]";
    }
    /**
     * 语音识别：data:{"output":{"choices":[{"message":{"annotations":[{"type":"audio_info","language":"zh","emotion":"neutral"}],"content":[{"text":"欢迎与"}],"role":"assistant"},"finish_reason":"null"}]},"usage":{"output_tokens_details":{"text_tokens":6},"input_tokens_details":{"text_tokens":16},"seconds":1},"request_id":"e84128d5-4bae-4e7e-91ab-6fb33504d2e3"}
     * LLM和图像识别：data: {"id":"ccf32be6-ad2f-4658-963a-fc3c22346e6b","object":"chat.completion.chunk","created":1761725211,"model":"deepseek-reasoner","system_fingerprint":"fp_ffc7281d48_prod0820_fp8_kvcache","choices":[{"index":0,"delta":{"content":null,"reasoning_content":"在"},"logprobs":null,"finish_reason":null}]}
     * @param data
     * @return
     */
    public StreamData parseStreamContentFromData(String data){
        return AIResponseUtil.parseStreamContentFromData(data);
    }
    
    /**
     * 获取智能问答请求参数类型
     * @return
     */
    public String getAIChatRequestType(){
        return AIConstants.AI_CHAT_REQUEST_BODY_JSON;
        
    }
    
    protected Map<String, Object> buildInputImagesMessage(String message,String... imageUrls) {
        return MessageBuilder.buildInputImagesMessage(message,imageUrls);
    }
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
            if(!parameters.containsKey("stream") && chatAgentMessage.getStream() != null){
                requestMap.put("stream", chatAgentMessage.getStream());
            }
            requestMap.putAll(parameters);
        }
        else {
            //设置默认参数
            if( chatAgentMessage.getStream() != null){
                requestMap.put("stream", chatAgentMessage.getStream());
            }
            else {
                requestMap.put("stream", true);
            }
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
        else if(agentMessage instanceof ImageVLAgentMessage){
            parameters = buildImageVLRequestMap((ImageVLAgentMessage)agentMessage);;
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
