package org.frameworkset.spi.ai.model;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class SessionAgentMessage<T extends SessionAgentMessage> extends AgentMessage<T> {
    /** 使用静态变量存储会话记忆（实际项目中建议使用缓存或数据库）*/
    private List<Map<String, Object>> sessionMemory;

    public T setSessionMemory(List<Map<String, Object>> sessionMemory) {
        this.sessionMemory = sessionMemory;
        return (T)this;
    }

    public List<Map<String, Object>> getSessionMemory() {
        return sessionMemory;
    }
    

}
