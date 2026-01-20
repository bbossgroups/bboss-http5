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
import org.frameworkset.spi.ai.model.ImageAgentMessage;
import org.frameworkset.spi.ai.model.ImageEvent;
import org.frameworkset.spi.ai.util.MessageBuilder;
import org.frameworkset.spi.remote.http.ClientConfiguration;

import java.util.*;

/**
 * 阿里百炼通义系列模型智能体适配器
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class QwenAgentAdapter extends AgentAdapter{
    
   

    @Override
    protected Map buildGenImageRequestMap(ImageAgentMessage imageAgentMessage) {

        Map<String, Object> requestMap = new HashMap<>();

        requestMap.put("model", imageAgentMessage.getModel());


        Map<String,Object> input = new LinkedHashMap<>();


        // 构建消息历史列表，包含之前的会话记忆

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = MessageBuilder.buildGenImageMessage(imageAgentMessage);
        messages.add(userMessage);
        input.put("messages", messages);
        requestMap.put("input", input);

        // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
//        Map parameters = new LinkedHashMap();
//        parameters.put("negative_prompt","");
//        parameters.put("prompt_extend",true);
//        parameters.put("watermark",false);
//        parameters.put("size","1328*1328");
        Map parameters = imageAgentMessage.getParameters();
        if(SimpleStringUtil.isEmpty( parameters)){
            // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
//            parameters = new LinkedHashMap();
//            parameters.put("negative_prompt","");
//            parameters.put("prompt_extend",true);
//            parameters.put("watermark",false);
//            parameters.put("size","1328*1328");
        }
        else{
            requestMap.put("parameters", parameters);
        }
        
       
        
        return requestMap;
    }
    public ImageEvent buildGenImageResponse(ClientConfiguration config,ImageAgentMessage imageAgentMessage, Map imageData){
        Map output = (Map)imageData.get("output");
        List choices = (List)output.get("choices");
        if(choices == null || choices.size() == 0)
            return null;
        Map choice = (Map)choices.get(0);
        Map messageData = (Map)choice.get("message");

        String finishReason = (String)choice.get("finish_reason");
        List imageContentData = (List)messageData.get("content");
        ImageEvent imageEvent = null;
        if(imageContentData != null ){
            int size = imageContentData.size();
            if(size > 0) {
                imageEvent = new ImageEvent();
                if(size == 1) {
                    Map image = (Map) imageContentData.get(0);
                    String imageUrl = (String) image.get("image");

                    imageEvent.setGenImageUrl(imageUrl);
                    imageEvent.setImageUrl(genImageFileBase64Download.downloadImage(config,imageAgentMessage,null,imageUrl));
                }
                else{
                    for(int i = 0; i < size; i++){
                        Map image = (Map) imageContentData.get(i);
                        String imageUrl = (String) image.get("image");
                        imageEvent.addGenImageUrl(imageUrl);
                        imageEvent.addImageUrl(genImageFileBase64Download.downloadImage(config,  imageAgentMessage,null,imageUrl));
                    }
                }
                imageEvent.setFinishReason(finishReason);
            }
        }
        return imageEvent;
    }
}
