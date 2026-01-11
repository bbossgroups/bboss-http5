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
import org.frameworkset.spi.ai.model.AIRuntimeException;
import org.frameworkset.spi.ai.model.AgentMessage;
import org.frameworkset.spi.remote.http.ClientConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class AgentAdapterFactory {
    private static Map<String,AgentAdapter> agentAdapters = new LinkedHashMap<>();
    static{
        agentAdapters.put(AIConstants.AI_MODEL_TYPE_DOUBAO,new DoubaoAgentAdapter());
        agentAdapters.put(AIConstants.AI_MODEL_TYPE_QWEN,new QwenAgentAdapter());
        agentAdapters.put(AIConstants.AI_MODEL_TYPE_DEEPSEEK,new DeepseekAgentAdapter());
        agentAdapters.put(AIConstants.AI_MODEL_TYPE_KIMI,new KimiAgentAdapter());
        agentAdapters.put(AIConstants.AI_MODEL_TYPE_NONE,new NoneAgentAdapter());
        agentAdapters.put(AIConstants.AI_MODEL_TYPE_SILICONFLOW,new SiliconflowAgentAdapter());
        agentAdapters.put(AIConstants.AI_MODEL_TYPE_OPENAI,new OpenaiAgentAdapter());
        agentAdapters.put(AIConstants.AI_MODEL_TYPE_BAIDU,new BaiduAgentAdapter());

        agentAdapters.put(AIConstants.AI_MODEL_TYPE_JIUTIAN,new JiutianAgentAdapter());
    }
    
    public static AgentAdapter getAgentAdapter(String modelType) {
        AgentAdapter agentAdapter = null;
        if (SimpleStringUtil.isNotEmpty(modelType)) {
            agentAdapter = agentAdapters.get(modelType);
        } else {
            agentAdapter = agentAdapters.get(AIConstants.AI_MODEL_TYPE_NONE);
        }
        if(agentAdapter == null){
            throw new AIRuntimeException("modelType:["+modelType+"] is not supported.");
        }
        return agentAdapter;
    }

    
    public static AgentAdapter getAgentAdapter(ClientConfiguration clientConfiguration,Object message) {
        String modelType = null;
        if(message instanceof AgentMessage){
            modelType = ((AgentMessage)message).getModelType();
            if(modelType == null || modelType.equals("")){
                modelType = clientConfiguration.getModelType();
            }
        }
        return AgentAdapterFactory.getAgentAdapter(modelType);
    }
    

}
