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
import org.frameworkset.spi.ai.model.AIConstants;
import org.frameworkset.spi.ai.model.StreamData;
import org.frameworkset.spi.ai.util.AIResponseUtil;
import org.frameworkset.spi.ai.util.MessageBuilder;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Kimi模型智能体适配器
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class JiutianAgentAdapter extends QwenAgentAdapter{
    private Logger logger = org.slf4j.LoggerFactory.getLogger(JiutianAgentAdapter.class);

    @Override
    protected Map<String, Object> buildInputImagesMessage(String message,String... imageUrls) {
        return MessageBuilder.buildJiuTianInputImagesMessage(message,imageUrls);
    }
    @Override
    protected Object handleImageParserMessages(List<Map<String, Object>> messages){
        return SimpleStringUtil.object2json(messages);
    }
 
//    protected Map buildImageVLRequestMap(ImageVLAgentMessage imageAgentMessage) {
//
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("model",imageAgentMessage.getModel());
//
////        "image": image_path,
////                "prompt": "描述下这张图片",
////        requestMap.put("prompt",imageAgentMessage.getMessage());
////        requestMap.put("image",imageAgentMessage.getImageUrls().get(0));
//
//        // 构建消息历史列表，包含之前的会话记忆
//
//        List<Map<String, Object>> sessionMemory = imageAgentMessage.getSessionMemory();
//        List<Map<String, Object>> messages = null;
//        if(sessionMemory != null && sessionMemory.size() > 0){
//            messages = new ArrayList<>(sessionMemory);
//        }
//        else{
//            messages = new ArrayList<>();
//        }
//
//        Map<String, Object> userMessage = buildInputImagesMessage(imageAgentMessage.getMessage(),imageAgentMessage.getImageUrls().toArray(new String[]{}));
//        messages.add(userMessage);
//
//        String data = SimpleStringUtil.object2json(messages);
//        
//        requestMap.put("messages", data);
//        Map parameters = imageAgentMessage.getParameters();
//
//        filterParameters(imageAgentMessage,requestMap,parameters);
//
//        return requestMap;
//    }
    public String getAIImageParsertRequestType(){
        return AIConstants.AI_CHAT_REQUEST_POST_FORM;

    }
//    protected void filterParameters(AgentMessage agentMessage, Map<String, Object> requestMap, Map<String, Object> parameters) {
//        if(SimpleStringUtil.isEmpty( parameters)){
//            if( agentMessage.getStream() != null){
//                requestMap.put("stream", agentMessage.getStream());
//            }
//            
//
//            if( agentMessage.getTemperature() != null){
//                requestMap.put("temperature", agentMessage.getTemperature());
//            }
//
//            // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
//
//        }
//        else {
//            //设置默认参数
//            if(!parameters.containsKey("stream") && agentMessage.getStream() != null){
//                requestMap.put("stream", agentMessage.getStream());
//            }
//            if(!parameters.containsKey("temperature") && agentMessage.getTemperature() != null){
//                requestMap.put("temperature", agentMessage.getTemperature());
//            }
//            requestMap.putAll( parameters);
//        }
//    }


    @Override
    public boolean isImageParserDone(String data){
        return "[EOS]".equals(data);

    }
    @Override
    public String getImageParserDoneData(){
        return "data:[EOS]";
    }

 

    @Override
    public StreamData parseImageParserStreamContentFromData(String data){
        return AIResponseUtil.parseJiutianImageParserStreamContentFromData(data);
    }
    
}
