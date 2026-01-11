package org.frameworkset.spi.ai.util;
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
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.frameworkset.spi.ai.adapter.AgentAdapter;
import org.frameworkset.spi.ai.model.ServerEvent;
import org.frameworkset.spi.ai.model.StreamData;
import org.frameworkset.spi.reactor.FluxSinkStatus;
import org.frameworkset.spi.reactor.ReactorCallException;
import org.frameworkset.spi.reactor.StreamDataHandler;
import org.frameworkset.spi.remote.http.proxy.BBossEntityUtils;
import org.frameworkset.util.concurrent.BooleanWrapperInf;
import org.frameworkset.util.concurrent.NoSynBooleanWrapper;
import reactor.core.publisher.FluxSink;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author biaoping.yin
 * @Date 2026/1/11
 */
public class AIResponseUtil {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AIResponseUtil.class);
 


  

  

    public static boolean handleStringExceptionData(Throwable throwable,FluxSink<String> sink, BooleanWrapperInf firstEventTag){
        if(logger.isWarnEnabled()) {
            logger.warn("服务端异常：", throwable);
        }
        if(firstEventTag.get()) {
            firstEventTag.set(false);
        }
        String error = SimpleStringUtil.exceptionToString(throwable);
        sink.next(error);
        sink.complete();
        return true;

    }

    public static boolean handleServerEventExceptionData(Throwable throwable,FluxSink<ServerEvent> sink, BooleanWrapperInf firstEventTag){
        if(logger.isWarnEnabled()) {
            logger.warn("服务端异常：", throwable);
        }

        String error = SimpleStringUtil.exceptionToString(throwable);
        ServerEvent serverEvent = new ServerEvent();
        if(firstEventTag.get()) {
            firstEventTag.set(false);
            serverEvent.setFirst(true);
        }
        serverEvent.setData(error);
        serverEvent.setType(ServerEvent.ERROR);
        sink.next(serverEvent);

        serverEvent = new ServerEvent();

        serverEvent.setDone( true);
        sink.next(serverEvent);
        sink.complete();
        return true;

    }

    private static StreamData parseAudioStreamContentFromData(Map output){
        try {

            Object choices_ = output.get("choices");
            if (choices_ != null ) {
                if (choices_ instanceof List) {
                    List<Map> choices = (List<Map>) choices_;
                    if (choices.size() > 0) {
                        Map choice = choices.get(0);
                        Map message = (Map) choice.get("message");

                        if(message != null) {
                            List<Map> content_ = (List) message.get("content");

                            String content = content_ != null && content_.size() > 0? (String) content_.get(0).get("text"):null;
                            List<Map> reasoning_content_ = (List) message.get("reasoning_content");
                            String reasoning_content = reasoning_content_ != null && reasoning_content_.size() > 0?(String) reasoning_content_.get(0).get("text"):null;
                            String finishReason = (String) choice.get("finish_reason");
                            if (SimpleStringUtil.isNotEmpty(reasoning_content)) {
                                return new StreamData(ServerEvent.REASONING_CONTENT, reasoning_content, finishReason);
                            } else {
                                return new StreamData(ServerEvent.CONTENT, content, finishReason);
                            }
                        }
                        else{
                            if(logger.isDebugEnabled())
                                logger.debug("choices list message null");
                        }

                    }
                    else {
                        if(logger.isDebugEnabled())
                            logger.debug("choices list size is 0");
                    }

                }
                else{
                    if (logger.isDebugEnabled())
                        logger.debug("choices is not list:{}");
                }
            }

        } catch (Exception e) {
            throw new ReactorCallException("ParseAudioStreamContentFromData failed:",e);
        }
        return null;
    }
    /**
     * 语音识别：data:{"output":{"choices":[{"message":{"annotations":[{"type":"audio_info","language":"zh","emotion":"neutral"}],"content":[{"text":"欢迎与"}],"role":"assistant"},"finish_reason":"null"}]},"usage":{"output_tokens_details":{"text_tokens":6},"input_tokens_details":{"text_tokens":16},"seconds":1},"request_id":"e84128d5-4bae-4e7e-91ab-6fb33504d2e3"}
     * LLM和图像识别：data: {"id":"ccf32be6-ad2f-4658-963a-fc3c22346e6b","object":"chat.completion.chunk","created":1761725211,"model":"deepseek-reasoner","system_fingerprint":"fp_ffc7281d48_prod0820_fp8_kvcache","choices":[{"index":0,"delta":{"content":null,"reasoning_content":"在"},"logprobs":null,"finish_reason":null}]}
     * @param data
     * @return
     */
    public static StreamData parseStreamContentFromData(String data) {
        try {
            Map map = SimpleStringUtil.json2Object(data,Map.class);
            Object choices_ = map.get("choices");
            if(choices_ == null){
                Map output = (Map) map.get("output");
                if(output != null){
                    return parseAudioStreamContentFromData(output);
                }
            }
            if (choices_ != null ) {
                if (choices_ instanceof List) {
                    List<Map> choices = (List<Map>) choices_;
                    if (choices.size() > 0) {
                        Map choice = choices.get(0);
                        String finishReason = (String) choice.get("finish_reason");
                        Map delta = (Map) choice.get("delta");
                        if (delta != null) {
//                            String content = (String)delta.get("content");
//                            return content;
                            String reasoning_content = (String)delta.get("reasoning_content");
                            String content = (String) delta.get("content");
                            if(SimpleStringUtil.isNotEmpty(reasoning_content)){
                                return new StreamData(ServerEvent.REASONING_CONTENT,reasoning_content,finishReason);
                            }
                            else{
                                return new StreamData(ServerEvent.CONTENT,content,finishReason);
                            }

                        }
                        else{
                            Map message = (Map) choice.get("message");
                            if(message != null) {
                                String reasoning_content = (String)message.get("reasoning_content");
                                String content = (String) message.get("content");
                                if(SimpleStringUtil.isNotEmpty(reasoning_content)){
                                    return new StreamData(ServerEvent.REASONING_CONTENT,reasoning_content,finishReason);
                                }
                                else{
                                    return new StreamData(ServerEvent.CONTENT,content,finishReason);
                                }
                            }
                            if(logger.isDebugEnabled())
                                logger.debug("choices list delta null: {}",data);
                        }
                    }
                    else {
                        if(logger.isDebugEnabled())
                            logger.debug("choices list size is 0: {}",data);
                    }

                }
                else{
                    if (logger.isDebugEnabled())
                        logger.debug("choices is not list:{}", data);
                }
            }
            else {
                String code =  (String)map.get("code");
                String message = (String) map.get("message");
                if(SimpleStringUtil.isNotEmpty(code)) {
                    return new StreamData(ServerEvent.CONTENT, message, code);
                }
                else {
                    if(logger.isDebugEnabled())
                        logger.debug("-----------no choices:{}",data);
                }

            }
        } catch (Exception e) {
            throw new ReactorCallException(data,e);
        }
        return null;
    }

    /**
     * 语音识别：data:{"output":{"choices":[{"message":{"annotations":[{"type":"audio_info","language":"zh","emotion":"neutral"}],"content":[{"text":"欢迎与"}],"role":"assistant"},"finish_reason":"null"}]},"usage":{"output_tokens_details":{"text_tokens":6},"input_tokens_details":{"text_tokens":16},"seconds":1},"request_id":"e84128d5-4bae-4e7e-91ab-6fb33504d2e3"}
     * LLM和图像识别：
     * data:{"parts":[{"role":"assistant","id":"9db6ab71-b12b-4426-a631-1d92757194bc","content":{"delta":"一只","type":"text","text":"一只","status":"init"},"status":"init"}]}
     *
     * data:{"parts":[{"role":"assistant","id":"9db6ab71-b12b-4426-a631-1d92757194bc","content":{"delta":"白色的","type":"text","text":"一只白色的","status":"init"},"status":"init"}]}
     *
     * data:{"parts":[{"role":"assistant","id":"9db6ab71-b12b-4426-a631-1d92757194bc","content":{"delta":"狗","type":"text","text":"一只白色的狗","status":"init"},"status":"init"}]}
     *
     * ......
     * ......
     * data:{"parts":[{"role":"assistant","id":"9db6ab71-b12b-4426-a631-1d92757194bc","content":{"delta":" ","type":"text","text":"一只白色的狗坐在一块石头上，背景是草地。 ","status":"init"},"status":"init"}]}
     *
     * data:{"usage":{"completion_tokens":12,"prompt_tokens":29,"total_tokens":41},"parts":[{"role":"assistant","id":"9db6ab71-b12b-4426-a631-1d92757194bc","content":{"delta":"[EOS]","history":[{"input":" Ref OCR: [] 描述图片","upload_img":"iVBORw0KGgoAAAANSUhEUgAAAwYAAAI5CAIAAACU/7pPAAAACXBIWXMAABJ0AAASdAHeZh94AAAgAElEQVR42lS8948kTXrn9657d9+KcYcW7WaMYVcLBrpG3z9SioZxHE8Cx4alQ2zfBJHIol4SM7H2YBBACAqlhm4ICSQCPF/lGU5mpamHpoAAAAASUVORK5CYII=","label":"3-2","revise_prompt":"一只白色的狗坐在一块石头上，背景是草地。 "}],"type":"text","text":"一只白色的狗坐在一块石头上，背景是草地。 ","status":"finish"},"status":"finish"}],"finished":"Stop","completionMsg":{"modelId":"LLMImage2Text","modelVersion":"4"}}
     * @param data
     * @return
     */
    public static StreamData parseJiutianStreamContentFromData(String data) {
        try {
            Map map = SimpleStringUtil.json2Object(data,Map.class);
            Object choices_ = map.get("parts");
            String finishReason = (String) map.get("finished");
            if(choices_ == null){
                Map output = (Map) map.get("output");
                if(output != null){
                    return parseAudioStreamContentFromData(output);
                }
            }
            if (choices_ != null ) {
                if (choices_ instanceof List) {
                    List<Map> choices = (List<Map>) choices_;
                    if (choices.size() > 0) {
                        Map choice = choices.get(0);
                      
                        Map content = (Map) choice.get("content");
                        if (content != null) {
//                            String content = (String)delta.get("content");
//                            return content;
                            String reasoning_content = (String)content.get("reasoning_content");
                            String delta = (String) content.get("delta");
                            if(SimpleStringUtil.isNotEmpty(reasoning_content)){
                                return new StreamData(ServerEvent.REASONING_CONTENT,reasoning_content,finishReason);
                            }
                            else{
                                if(!delta.equals("[EOS]")) {
                                    return new StreamData(ServerEvent.CONTENT, delta, finishReason);
                                }
                                else{
                                    return new StreamData(ServerEvent.CONTENT, delta, finishReason,true);
                                }
                            }

                        }
                        else{
                            Map message = (Map) choice.get("message");
                            if(message != null) {
                                String reasoning_content = (String)message.get("reasoning_content");
                                String delta = (String) message.get("delta");
                                if(SimpleStringUtil.isNotEmpty(reasoning_content)){
                                    return new StreamData(ServerEvent.REASONING_CONTENT,reasoning_content,finishReason);
                                }
                                else{
                                    return new StreamData(ServerEvent.CONTENT,delta,finishReason);
                                }
                            }
                            if(logger.isDebugEnabled())
                                logger.debug("choices list delta null: {}",data);
                        }
                    }
                    else {
                        if(logger.isDebugEnabled())
                            logger.debug("choices list size is 0: {}",data);
                    }

                }
                else{
                    if (logger.isDebugEnabled())
                        logger.debug("choices is not list:{}", data);
                }
            }
            else {
                String code =  (String)map.get("code");
                String message = (String) map.get("message");
                if(SimpleStringUtil.isNotEmpty(code)) {
                    return new StreamData(ServerEvent.CONTENT, message, code);
                }
                else {
                    if(logger.isDebugEnabled())
                        logger.debug("-----------no choices:{}",data);
                }

            }
        } catch (Exception e) {
            throw new ReactorCallException(data,e);
        }
        return null;
    }

    private static <T> void processStreamResponse(ClassicHttpResponse response, FluxSink<T> sink, StreamDataHandler<T> streamDataHandler) throws IOException {

        FluxSinkStatus fluxSinkStatus = null;
        try  {
            fluxSinkStatus = new FluxSinkStatus(response,streamDataHandler.getHttpUriRequestBase());

//            // 添加取消监听器
//            sink.onCancel(() -> {
//                // 当订阅被取消时执行
//                logger.info("Subscription cancelled");
//                fluxSinkStatus.cancel();
//                // 执行清理工作
//            });
            final FluxSinkStatus fluxSinkStatus_ = fluxSinkStatus;
            AgentAdapter agentAdapter = streamDataHandler.getAgentAdapter();
            // 添加处置监听器
            sink.onDispose(() -> {
                // 当 sink 被处置时执行（包括正常完成、错误和取消）
                if(logger.isDebugEnabled()) {
                    logger.debug("Sink disposed");
                }
                fluxSinkStatus_.dispose();
                // 执行清理工作
                fluxSinkStatus_.releaseResources();

            });
            String line;
            boolean needBreak = false;
            BooleanWrapperInf firstEventTag = new NoSynBooleanWrapper(true);
            while (!sink.isCancelled() && (line = fluxSinkStatus.readLine()) != null ) {
                if(fluxSinkStatus.isDispose()){
                    break;
                }
                needBreak = streamDataHandler.handle(line, sink,   firstEventTag);
                if(needBreak){
                    sink.complete();
                    break;
                }


            }
            if(!needBreak){
//                ServerEvent serverEvent = new ServerEvent();
//                if(firstEventTag.get()) {
//                    firstEventTag.set(false);
//                    serverEvent.setFirst(true);
//                }
//                serverEvent.setType(ServerEvent.DATA);
//                serverEvent.setDone(true);
//                sink.next(serverEvent);
                streamDataHandler.handle(agentAdapter.getDoneData(), sink,   firstEventTag);
                sink.complete();
            }
        }
        finally {
            fluxSinkStatus.releaseResources();
        }
    }


    public static ServerEvent handleChatResponse(AgentAdapter agentAdapter,String url, ClassicHttpResponse response)
            throws IOException, ParseException {

        int status = response.getCode();

        if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
            HttpEntity entity = response.getEntity();
            String line = entity != null ? BBossEntityUtils.toString(entity) : null;
            if(line == null || line.equals("")){
                return null;
            }
            return handleServerEventData( agentAdapter, line);
        } else {
            HttpEntity entity = response.getEntity();
            if (entity != null ) {
                if (logger.isDebugEnabled()) {
                    logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
                }
                throw new ReactorCallException(new StringBuilder().append("Request url:")
                        .append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString()));
            }
            else {
                throw new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status).toString()));
            }
        }
    }
    public static <T> void handleStreamResponse(String url, ClassicHttpResponse response,
                                                FluxSink<T> sink, StreamDataHandler<T> streamDataHandler)
            throws IOException, ParseException {

        int status = response.getCode();

        if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
            processStreamResponse(response, sink,streamDataHandler);
        } else {
            HttpEntity entity = response.getEntity();
            if (entity != null ) {
                if (logger.isDebugEnabled()) {
                    logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
                }
                throw new ReactorCallException(new StringBuilder().append("Request url:")
                        .append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString()));
            }
            else {
                throw new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status).toString()));
            }
        }
    }

    /**
     * line：遵循openai规范
     * @param line
     * @param sink
     * @param firstEventTag
     * @return
     */
    public static   boolean handleStringData(AgentAdapter agentAdapter ,String line,FluxSink<String> sink, BooleanWrapperInf firstEventTag){
        if(logger.isDebugEnabled()){
            logger.debug("line: " + line);
        }
        if (line.startsWith("data: ") || line.startsWith("data:")) {
            String data = line.substring(5).trim();

            if (agentAdapter.isDone(  data)) {
                return true;
            }
            if (!data.isEmpty()) {
                if(firstEventTag.get()) {
                    firstEventTag.set(false);
                }
                StreamData content = agentAdapter.parseStreamContentFromData(data);
                if (content != null && !content.isEmpty()) {
                    sink.next(content.getData());
                }
            }
        }
        else{
            if(logger.isDebugEnabled()) {
                logger.debug("streamChatCompletion: " + line);
            }
        }
        return false;
    }
    /**
     * line：遵循openai规范
     * @param line
     * @return
     */
    public static   ServerEvent handleServerEventData(AgentAdapter agentAdapter ,String line){
        if(logger.isDebugEnabled()){
            logger.debug("line: " + line);
        }
        ServerEvent serverEvent = null;
        if (SimpleStringUtil.isNotEmpty(line)) {
            StreamData content = agentAdapter.parseStreamContentFromData(line);
            if (content != null) {

                serverEvent = new ServerEvent();

                serverEvent.setFinishReason(content.getFinishReason());
                serverEvent.setData(content.getData());
                serverEvent.setType(ServerEvent.DATA);
                serverEvent.setContentType(content.getType());


            }

        }
        return serverEvent;

    }


    /**
     * line：遵循openai规范
     * @param stream
     * @param line
     * @param sink
     * @param firstEventTag
     * @return
     */
    public static boolean handleServerEventData(AgentAdapter agentAdapter, boolean stream, String line, FluxSink<ServerEvent> sink, BooleanWrapperInf firstEventTag){
        if(logger.isDebugEnabled()){
            logger.debug("line: " + line);
        }
        String data = null;
        if(stream){
            if (line.startsWith("data: ")||line.startsWith("data:")) {
                data = line.substring(5).trim();
            }
            else{
                if(logger.isDebugEnabled()) {
                    logger.debug("streamChatCompletion: {}",line);
                }
            }
        }
        else{
            if (line.startsWith("data: ")||line.startsWith("data:")) {
                data = line.substring(5).trim();
            }
            else{
                data = line;
            }

        }
        if(SimpleStringUtil.isNotEmpty( data)){
            if (agentAdapter.isDone(  data)) {

                ServerEvent serverEvent = new ServerEvent();
                if(firstEventTag.get()) {
                    firstEventTag.set(false);
                    serverEvent.setFirst(true);
                }
                serverEvent.setType(ServerEvent.DATA);
                serverEvent.setDone(true);
                sink.next(serverEvent);
                return true;
            }
            StreamData content = agentAdapter.parseStreamContentFromData(data);
            if (content != null) {
                if( !content.isEmpty()) {
                    ServerEvent serverEvent = new ServerEvent();
                    if (firstEventTag.get()) {
                        firstEventTag.set(false);
                        serverEvent.setFirst(true);
                    }
                    serverEvent.setFinishReason(content.getFinishReason());
                    if(!content.isDone()) {
                        serverEvent.setData(content.getData());
                    }
                    serverEvent.setType(ServerEvent.DATA);
                    serverEvent.setContentType(content.getType());
                    serverEvent.setDone(content.isDone());
                    sink.next(serverEvent);
                    return content.isDone();
                }
                else if(content.getFinishReason() != null && content.getFinishReason().length() > 0){
                    ServerEvent serverEvent = new ServerEvent();
                    if (firstEventTag.get()) {
                        firstEventTag.set(false);
                        serverEvent.setFirst(true);
                    }
                    serverEvent.setFinishReason(content.getFinishReason());
                    serverEvent.setType(ServerEvent.DATA);
                    serverEvent.setContentType(content.getType());
                    serverEvent.setDone(content.isDone());
                    sink.next(serverEvent);
                    return content.isDone();
                }
            }
        }

        return false;
    }

}
