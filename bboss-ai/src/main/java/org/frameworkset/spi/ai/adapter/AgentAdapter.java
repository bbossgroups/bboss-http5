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

import com.frameworkset.util.FileUtil;
import com.frameworkset.util.SimpleStringUtil;
import org.frameworkset.spi.ai.material.GenMaterialFileDownload;
import org.frameworkset.spi.ai.model.*;
import org.frameworkset.spi.ai.util.AIResponseUtil;
import org.frameworkset.spi.ai.material.GenFileDownload;
import org.frameworkset.spi.ai.util.AudioDataBuilder;
import org.frameworkset.spi.ai.util.MessageBuilder;
import org.frameworkset.spi.ai.util.StreamDataBuilder;
import org.frameworkset.spi.reactor.SSEHeaderSetFunction;
import org.frameworkset.spi.remote.http.ClientConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 智能体适配器：针对不同厂家的模型平台服务进行适配，包括请求参数转换、结果转换等
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public abstract class AgentAdapter {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AgentAdapter.class);
    protected GenFileDownload genFileDownload;
 
 
    protected AgentAdapter initAgentAdapter(){
        genFileDownload = new GenMaterialFileDownload();
        return this;
    }

    public GenFileDownload getGenFileDownload() {
        return genFileDownload;
    }

    /**
     * 构建生成图片请求参数
     * @param imageAgentMessage
     * @return
     */
    protected abstract Map buildGenImageRequestMap(ImageAgentMessage imageAgentMessage);

    protected void filterParameters(AgentMessage agentMessage,Map<String, Object> requestMap, Map<String, Object> parameters) {
        if(SimpleStringUtil.isEmpty( parameters)){
            if( agentMessage.getStream() != null){
                requestMap.put("stream", agentMessage.getStream());
            }

            if( agentMessage.getTemperature() != null){
                requestMap.put("temperature", agentMessage.getTemperature());
            }
            // enable_thinking 参数开启思考过程，thinking_budget 参数设置最大推理过程 Token 数

        }
        else {
            //设置默认参数
            if(!parameters.containsKey("stream") && agentMessage.getStream() != null){
                requestMap.put("stream", agentMessage.getStream());
            }

            if(!parameters.containsKey("temperature") && agentMessage.getTemperature() != null){
                requestMap.put("temperature", agentMessage.getTemperature());
            }
            requestMap.putAll( parameters);
        }
    }
    protected Object handleImageParserMessages(List<Map<String, Object>> messages){
        return messages;
    }
    
    
    public Map buildImageVLRequestMap(ImageVLAgentMessage imageAgentMessage) {

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model",imageAgentMessage.getModel());

        // 构建消息历史列表，包含之前的会话记忆

        List<Map<String, Object>> sessionMemory = imageAgentMessage.getSessionMemory();
        List<Map<String, Object>> messages = null;
        if(sessionMemory != null && sessionMemory.size() > 0){
            messages = new ArrayList<>(sessionMemory);
        }
        else{
            messages = new ArrayList<>();
        }
        List<String > imageUrls = imageAgentMessage.getImageUrls();

        Map<String, Object> userMessage = null;
        if(imageUrls != null && imageUrls.size() > 0) {
            userMessage = buildInputImagesMessage(imageAgentMessage.getPrompt(), imageUrls.toArray(new String[]{}));
        }
        else{
            userMessage = buildInputImagesMessage(imageAgentMessage.getPrompt(), (String[])null);
        }
        messages.add(userMessage);
        imageAgentMessage.addSessionMessage(userMessage);

        requestMap.put("messages", handleImageParserMessages(messages));
        Map parameters = imageAgentMessage.getParameters();

        filterParameters(imageAgentMessage,requestMap,parameters);

        return requestMap;
    }
    public boolean isDone(String data){
        return "[DONE]".equals(data);

    }
    
    public String getDoneData(){
        return "data:[DONE]";
    }


    public boolean isImageParserDone(String data){
        return isDone(  data);

    }

    public String getImageParserDoneData(){
        return getDoneData();
    }
    /**
     * 处理音频识别流数据
     * {"output":{"audio":{"data":"xxxx",
     *   "expires_at":1769158890,
     *   "id":"audio_66356352-8808-49bd-9c9c-d0283a3e2eb1"},
     *   "finish_reason":"null"},
     *   "usage":{"characters":53},
     *   "request_id":"66356352-8808-49bd-9c9c-d0283a3e2eb1"}
     * @param data
     * @return
     */
    public StreamData parseAudioGenStreamContentFromData(String data){
        return AIResponseUtil.parseQianwenAudioGenStreamContentFromData(data);
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
     * 语音识别：data:{"output":{"choices":[{"message":{"annotations":[{"type":"audio_info","language":"zh","emotion":"neutral"}],"content":[{"text":"欢迎与"}],"role":"assistant"},"finish_reason":"null"}]},"usage":{"output_tokens_details":{"text_tokens":6},"input_tokens_details":{"text_tokens":16},"seconds":1},"request_id":"e84128d5-4bae-4e7e-91ab-6fb33504d2e3"}
     * LLM和图像识别：data: {"id":"ccf32be6-ad2f-4658-963a-fc3c22346e6b","object":"chat.completion.chunk","created":1761725211,"model":"deepseek-reasoner","system_fingerprint":"fp_ffc7281d48_prod0820_fp8_kvcache","choices":[{"index":0,"delta":{"content":null,"reasoning_content":"在"},"logprobs":null,"finish_reason":null}]}
     * @param data
     * @return
     */
    public StreamData parseImageParserStreamContentFromData(String data){
        return AIResponseUtil.parseStreamContentFromData(data);
    }

    /**
     * 语音识别数据解析
     * @param data
     * @return
     */
    public StreamData parseAudioStreamContentFromData(StreamDataBuilder streamDataBuilder,String data){
        return AIResponseUtil.parseAudioStreamContentFromData(  streamDataBuilder,data);
    }

    
    
    /**
     * 获取图片识别模型智能问答请求参数类型
     * @return
     */
    public String getAIImageParsertRequestType(){
        return AIConstants.AI_CHAT_REQUEST_BODY_JSON;
        
    }

    /**
     * 获取音频识别模型智能问答请求参数类型
     * @return
     */
    public String getAIAudioParsertRequestType(){
        return AIConstants.AI_CHAT_REQUEST_BODY_JSON;

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
    public Map buildOpenAIRequestMap(ChatAgentMessage chatAgentMessage){
        String message = chatAgentMessage.getPrompt();
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
            if(!parameters.containsKey("temperature") && chatAgentMessage.getTemperature() != null){
                requestMap.put("temperature", chatAgentMessage.getTemperature());
            }
            requestMap.putAll(parameters);
        }
        else {
            //设置默认参数
            if( chatAgentMessage.getStream() != null){
                requestMap.put("stream", chatAgentMessage.getStream());
            }
            
            if( chatAgentMessage.getTemperature() != null){
                requestMap.put("temperature", chatAgentMessage.getTemperature());
            }
        }
        return requestMap;
    }
    public abstract ImageEvent buildGenImageResponse(ClientConfiguration config, ImageAgentMessage imageAgentMessage,Map imageData);
   
    public Object buildGenImageRequestParameter(ClientConfiguration clientConfiguration, Object imageAgentMessage){
        if(imageAgentMessage instanceof ImageAgentMessage){
            ImageAgentMessage temp = (ImageAgentMessage)imageAgentMessage;
            imageAgentMessage = buildGenImageRequestMap(temp);
            if(temp.getGenFileStoreDir() == null)
                temp.setGenFileStoreDir(clientConfiguration.getExtendConfig("genFileStoreDir"));
            if(temp.getEndpoint() == null)
                temp.setEndpoint(clientConfiguration.getExtendConfig("endpoint"));
            if(temp.getStoreImageType() == null)
                temp.setStoreImageType(clientConfiguration.getExtendConfig("storeImageType"));

            if(temp.getGenFileStoreDir() != null)
                temp.setGenFileStoreDir(temp.getGenFileStoreDir().trim());
            if(temp.getEndpoint() != null)
                temp.setEndpoint(temp.getEndpoint().trim());
            if(temp.getStoreImageType() != null)
                temp.setStoreImageType(temp.getStoreImageType().trim());
        }
        return imageAgentMessage;
        
    }
    
 
    public SSEHeaderSetFunction getAudioGenSSEHeaderSetFunction(){
        return SSEHeaderSetFunction.DEFAULT_SSEHEADERSETFUNCTION;
    }

    public ChatObject buildOpenAIRequestParameter(ClientConfiguration clientConfiguration,Object agentMessage){
        AgentMessage _agentMessage = null;
        if(agentMessage instanceof AgentMessage){
            _agentMessage =  ((AgentMessage)agentMessage);
        }          
        else if (agentMessage instanceof Map){
            _agentMessage = new MapAgentMessage((Map)agentMessage);
        }
        else{
            _agentMessage = new ObjectAgentMessage(agentMessage);
        }
        return _agentMessage.buildChatObject(clientConfiguration,this);
         
 
    }
    protected abstract Map<String, Object> buildGenAudioRequestMap(AudioAgentMessage audioAgentMessage);

    public Map<String, Object> _buildGenAudioRequestMap(AudioAgentMessage audioAgentMessage,ClientConfiguration clientConfiguration){

        if(audioAgentMessage.getGenFileStoreDir() == null)
            audioAgentMessage.setGenFileStoreDir(clientConfiguration.getExtendConfig("genFileStoreDir"));
        if(audioAgentMessage.getEndpoint() == null)
            audioAgentMessage.setEndpoint(clientConfiguration.getExtendConfig("endpoint"));
        if(audioAgentMessage.getStoreAudioType() == null){
            audioAgentMessage.setStoreAudioType(clientConfiguration.getExtendConfig("storeAudioType"));
        }
        Map params = buildGenAudioRequestMap(audioAgentMessage);
        if(audioAgentMessage.getStream() != null){
            params.put("stream", audioAgentMessage.getStream());
        }
        return params;
    }

    public Map<String, Object> _buildGetVideoResultRquestMap(VideoStoreAgentMessage videoStoreAgentMessage,ClientConfiguration clientConfiguration){

        if(videoStoreAgentMessage.getGenFileStoreDir() == null)
            videoStoreAgentMessage.setGenFileStoreDir(clientConfiguration.getExtendConfig("genFileStoreDir"));
        if(videoStoreAgentMessage.getEndpoint() == null)
            videoStoreAgentMessage.setEndpoint(clientConfiguration.getExtendConfig("endpoint"));
        if(videoStoreAgentMessage.getStoreVideoType() == null){
            videoStoreAgentMessage.setStoreVideoType(clientConfiguration.getExtendConfig("storeVideoType"));
        }
       
        return buildGetVideoResultRquestMap(  videoStoreAgentMessage);
    }

    protected abstract Map<String, Object> buildGetVideoResultRquestMap(VideoStoreAgentMessage videoStoreAgentMessage);

    /**
     * 构建音频生成请求参数
     * @param clientConfiguration
     * @param audioAgentMessage
     * @return
     */
    public Object buildGenAudioRequestParameter(ClientConfiguration clientConfiguration, Object audioAgentMessage) {
        if(audioAgentMessage instanceof AudioAgentMessage){
            AudioAgentMessage temp = (AudioAgentMessage)audioAgentMessage;
            audioAgentMessage = this._buildGenAudioRequestMap(temp,clientConfiguration);
          
           
        }
        return audioAgentMessage;
    }

    public abstract AudioEvent buildGenAudioResponse(ClientConfiguration config, AudioAgentMessage message, Map data);

    public Map buildAudioSTTRequestMap(AudioSTTAgentMessage audioSTTAgentMessage) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", audioSTTAgentMessage.getModel());

        // 构建消息历史列表，包含之前的会话记忆
        List<Map<String, Object>> messages = audioSTTAgentMessage.getSessionMemory() !=  null?
                new ArrayList<>(audioSTTAgentMessage.getSessionMemory()):new ArrayList<>();
        Object audio = audioSTTAgentMessage.getAudio();
        // 添加当前用户消息
        Map<String, Object> userMessage = null;
        if(audio != null) {
            userMessage = MessageBuilder.buildAudioSystemMessage(audioSTTAgentMessage.getPrompt());
        }
        else{
            userMessage = MessageBuilder.buildAudioUserMessage(audioSTTAgentMessage.getPrompt());
        }
        messages.add(userMessage);
        audioSTTAgentMessage.addSessionMessage(userMessage);
       
        if(audio != null) {
            AudioDataBuilder audioDataBuilder = audioSTTAgentMessage.getAudioDataBuilder();
            if (audioDataBuilder == null) {
                audioDataBuilder = () -> {
                    String base64Audio = null;


                    if (audio instanceof File) {

                        try {
                            byte[] audioBytes = FileUtil.getBytes((File) audio);
                            String contentType = audioSTTAgentMessage.getContentType();
                            if (contentType == null) {
                                contentType = "audio/wav";
                            }
                            base64Audio = "data:" + contentType + ";base64," +
                                    Base64.getEncoder().encodeToString(audioBytes);
                        } catch (IOException e) {
                            throw new AIRuntimeException(e);
                        }

                    } else if (audio instanceof byte[]) {
                        base64Audio = "data:" + audioSTTAgentMessage.getContentType() + ";base64," +
                                Base64.getEncoder().encodeToString((byte[]) audio);
                    } else if (audio instanceof String) {
                        base64Audio = (String) audio;
                    }
                    return base64Audio;

                };
            }

            //直接设置音频url地址
//        MessageBuilder.buildAudioMessage("https://dashscope.oss-cn-beijing.aliyuncs.com/audios/welcome.mp3");
            //将音频文件转换为base64编码
            userMessage = MessageBuilder.buildAudioMessage(audioDataBuilder);

            messages.add(userMessage);
        }
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("messages", messages);
        requestMap.put("input", input);
        Map parameters = audioSTTAgentMessage.getParameters();
        if(parameters != null) {
            requestMap.put("parameters", parameters);
        }
        if(audioSTTAgentMessage.getStream() != null){
            requestMap.put("stream", audioSTTAgentMessage.getStream());
        }
        if(audioSTTAgentMessage.getResultFormat() != null)
            requestMap.put("result_format", audioSTTAgentMessage.getResultFormat());
        return requestMap;
    }
    protected abstract Object buildGenVideoRequestMap(VideoAgentMessage videoAgentMessage,ClientConfiguration clientConfiguration);

    public Object buildVideoRequestParameter(ClientConfiguration clientConfiguration, VideoAgentMessage videoAgentMessage) {
        return this.buildGenVideoRequestMap(videoAgentMessage,clientConfiguration);
    }

    public abstract VideoTask buildVideoResponseTask(ClientConfiguration clientConfiguration, VideoAgentMessage videoAgentMessage,Map taskInfo);

    public VideoGenResult buildVideoGenResult(ClientConfiguration clientConfiguration,VideoStoreAgentMessage videoStoreAgentMessage,Map taskInfo) {
        return null;
    }
}
