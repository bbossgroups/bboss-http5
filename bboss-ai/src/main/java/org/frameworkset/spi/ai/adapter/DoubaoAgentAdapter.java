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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class DoubaoAgentAdapter  extends AgentAdapter{

    @Override
    public Map buildGenImageRequestMap(ImageAgentMessage imageAgentMessage) {

        Map<String, Object> requestMap = new HashMap<>();

        requestMap.put("model", imageAgentMessage.getModel());
        requestMap.put("prompt", imageAgentMessage.getMessage());

        Map parameters = imageAgentMessage.getParameters();
        if(SimpleStringUtil.isEmpty( parameters)){
            //默认参数
            requestMap.put("sequential_image_generation", "disabled");
            requestMap.put("response_format", "url");
            requestMap.put("size", "2k");
            requestMap.put("watermark", true);
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

    public ImageEvent buildGenImageResponse(Map imageData){
        List data = (List)imageData.get("data");
        if(data == null || data.size() == 0)
            return null;
        ImageEvent imageEvent = new ImageEvent();
        if(data.size() == 1) {
            Map imgInfo = (Map) data.get(0);
            String url = (String) imgInfo.get("url");
            String size = (String) imgInfo.get("size");

            
            imageEvent.setImageUrl(url);
            imageEvent.setImageSize(size);
            return imageEvent;
        }
        else{
            for (int i = 0; i < data.size(); i ++){
                Map imgInfo = (Map) data.get(i);
                String url = (String) imgInfo.get("url");
                String size = (String) imgInfo.get("size");
                imageEvent.addImageUrl(url);
                imageEvent.addImageSize(size);
            }
            return imageEvent;
        }
    }

}
