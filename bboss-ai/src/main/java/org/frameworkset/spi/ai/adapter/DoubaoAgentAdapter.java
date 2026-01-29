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
import org.frameworkset.spi.remote.http.ClientConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字节模型智能体适配器
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class DoubaoAgentAdapter  extends QwenAgentAdapter{

    @Override
    public Map buildGenImageRequestMap(ImageAgentMessage imageAgentMessage) {

        Map<String, Object> requestMap = new HashMap<>();

        requestMap.put("model", imageAgentMessage.getModel());
        requestMap.put("prompt", imageAgentMessage.getPrompt());
        List<String> imageUrls = imageAgentMessage.getImageUrls();
        if(imageUrls != null && imageUrls.size() > 0) {
            if(imageUrls.size() == 1){
                requestMap.put("image", imageUrls.get(0));
            }
            else{
                requestMap.put("image", imageUrls);
            }
             
        }

        Map parameters = imageAgentMessage.getParameters();
        if(SimpleStringUtil.isEmpty( parameters)){
            //默认参数
//            requestMap.put("sequential_image_generation", "disabled");
//            requestMap.put("response_format", "url");
//            requestMap.put("size", "2k");
//            requestMap.put("watermark", true);
        }
        else{
            requestMap.putAll(parameters);
        }
        
        
//        requestMap.put("sequential_image_generation", "disabled");
//        requestMap.put("response_format", "url");
//        requestMap.put("size", "2k");
//        requestMap.put("watermark", true);
        return requestMap;
    }
    /**
     * curl https://ark.cn-beijing.volces.com/api/v3/chat/completions \
     *   -H "Content-Type: application/json" \
     *   -H "Authorization: Bearer $ARK_API_KEY" \
     *   -d $'{
     *     "model": "doubao-seed-1-8-251228",
     *     "max_completion_tokens": 65535,
     *     "reasoning_effort": "medium",
     *     "messages": [
     *         {
     *             "content": [
     *                 {
     *                     "image_url": {
     *                         "url": "https://ark-project.tos-cn-beijing.ivolces.com/images/view.jpeg"
     *                     },
     *                     "type": "image_url"
     *                 },
     *                 {
     *                     "text": "图片主要讲了什么?",
     *                     "type": "text"
     *                 }
     *             ],
     *             "role": "user"
     *         }
     *     ]
     * }'
     */
//    @Override
//    protected void filterParameters(AgentMessage agentMessage,Map<String, Object> requestMap, Map<String, Object> parameters) {
//        if(SimpleStringUtil.isEmpty( parameters)){
////            requestMap.put("stream", true);
//
//            // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数
//
//            requestMap.put("max_completion_tokens",65535);
//            requestMap.put("reasoning_effort","medium");
//        }
//        else {
//
//            requestMap.putAll( parameters);
//        }
//    }

    public ImageEvent buildGenImageResponse(ClientConfiguration config, ImageAgentMessage imageAgentMessage,Map imageData){
        List data = (List)imageData.get("data");
        if(data == null || data.size() == 0)
            return null;
        ImageEvent imageEvent = new ImageEvent();
        if(data.size() == 1) {
            Map imgInfo = (Map) data.get(0);
            String url = (String) imgInfo.get("url");
            String size = (String) imgInfo.get("size");

            
            imageEvent.setGenImageUrl(url);
            imageEvent.setImageUrl(genFileDownload.downloadImage(config,   imageAgentMessage,null,url));
            imageEvent.setImageSize(size);
            return imageEvent;
        }
        else{
            for (int i = 0; i < data.size(); i ++){
                Map imgInfo = (Map) data.get(i);
                String url = (String) imgInfo.get("url");
                String size = (String) imgInfo.get("size");
                imageEvent.addImageUrl(genFileDownload.downloadImage(config,   imageAgentMessage,null,url));
                imageEvent.addImageSize(size);
                imageEvent.addGenImageUrl(url);
            }
            return imageEvent;
        }
    }

}
