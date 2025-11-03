package org.frameworkset.spi.ai.util;
/**
 * Copyright 2025 bboss
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author biaoping.yin
 * @Date 2025/11/2
 */
public class MessageBuilder {
    private static final Logger logger = LoggerFactory.getLogger(MessageBuilder.class);
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_TOOL = "tool";
    
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image_url";
    public static Map<String,Object> buildSystemMessage(String message){


        return buildMessage(ROLE_SYSTEM,  message);
    }
    public static Map<String,Object> buildAudioSystemMessage(String message){
        // 添加当前用户消息
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", ROLE_SYSTEM);
        List<Map> contents = new ArrayList<>();
        Map contentData = new LinkedHashMap();
        contentData.put("text", message);
        contents.add(contentData);
        userMessage.put("content", contents);
        return userMessage;
    }

    public static Map<String,Object> buildAudioMessage(String audioUrl){
        LinkedHashMap<String, Object> userMessage = new LinkedHashMap<>();
        userMessage.put("role", ROLE_USER);
        List contents = new ArrayList<>();
        Map<String,Object> contentData = new LinkedHashMap<>();
        contentData.put("audio", audioUrl);

        contents.add(contentData);
        userMessage.put("content", contents);
        return userMessage;
    }
    
    public static Map<String,Object> buildGenImageMessage(String message){
        Map<String, Object> userMessage = new LinkedHashMap<>();
        userMessage.put("role",ROLE_USER);

        List contents = new ArrayList<>();
        Map contentData = new LinkedHashMap();
        contentData.put("text", message);

        contents.add(contentData);
        userMessage.put("content", contents);
        return userMessage;
    }
    

    public static Map<String,Object> buildInputImagesMessage(String message,String... imageUrls){

        List contents = new ArrayList<>();
        Map contentData = null;
        for (String imageUrl:imageUrls) {
            contentData = new LinkedHashMap();
            contentData.put("type", TYPE_IMAGE);
            String _imageUrl = imageUrl;
            contentData.put("image_url", new HashMap<String, String>() {{

                put("url", _imageUrl);
            }});
            contents.add(contentData);
        }
 
        contentData = new LinkedHashMap();
        contentData.put("type", TYPE_TEXT);
        contentData.put("text", message);;
        contents.add(contentData);
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", ROLE_USER);
        userMessage.put("content", contents);
        return userMessage;
    }

    public static Map<String,Object> buildAudioMessage(AudioDataBuilder audioDataBuilder){
        LinkedHashMap<String, Object> userMessage = new LinkedHashMap<>();
        userMessage.put("role", ROLE_USER);
        List contents = new ArrayList<>();
        Map<String,Object> contentData = new LinkedHashMap<>();
        contentData.put("audio", audioDataBuilder.buildAudioBase64Data());

        contents.add(contentData);
        userMessage.put("content", contents);
        return userMessage;
    }
    public static Map<String,Object> buildUserMessage(String message){


        return buildMessage(ROLE_USER,  message);
    }

    public static Map<String,Object> buildAssistantMessage(String message){


        return buildMessage(ROLE_ASSISTANT,  message);
    }

    public static Map<String,Object> buildMessage(String role,String message){

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", role);
        userMessage.put("content", message);

        return userMessage;
    }

    public static Map<String,Object> buildToolMessage(String message,String toolId){

        Map<String, Object> toolMessage = new HashMap<>();
        toolMessage.put("role", ROLE_TOOL);
        toolMessage.put("content", message);
        toolMessage.put("tool_call_id", toolId);
 

        return toolMessage;
    }




}
