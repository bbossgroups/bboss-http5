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

/**
 * 语音生成模型消息
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class AudioAgentMessage extends StoreAgentMessage<AudioAgentMessage> {
    /**
     * 存储音频文件类型:
     * file 下载文件
     * url 不下载文件
     */
    private String storeAudioType;

    
    public String getStoreAudioType() {
        return storeAudioType;
    }

    public AudioAgentMessage setStoreAudioType(String storeAudioType) {
        this.storeAudioType = storeAudioType;
        return this;
    }
}
