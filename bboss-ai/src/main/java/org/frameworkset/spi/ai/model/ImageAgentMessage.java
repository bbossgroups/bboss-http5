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

import org.frameworkset.spi.ai.material.StoreFilePathFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片生成报文
 * @author biaoping.yin
 * @Date 2026/1/4
 */
public class ImageAgentMessage extends AgentMessage<ImageAgentMessage>{
    /**
     * 图片生成存储目录
     */
    private String genImageStoreDir;
    private String endpoint;
    private String storeImageType ;
    private StoreFilePathFunction storeFilePathFunction;
     
    private List<String> imageUrls;
    public ImageAgentMessage setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public ImageAgentMessage addImageUrl(String imageUrl) {
        if(imageUrls == null){
            imageUrls = new ArrayList<>();
        }
        imageUrls.add(imageUrl);
        return this;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }
    public String getStoreFilePath() {
        return storeFilePath;
    }

    public ImageAgentMessage setStoreFilePath(String storeFilePath) {
        this.storeFilePath = storeFilePath;
        return this;
    }

    /**
     * 存储图片文件相对路径，包含名称
     */
    private String storeFilePath;

    public String getGenImageStoreDir() {
        return genImageStoreDir;
    }

    public ImageAgentMessage setGenImageStoreDir(String genImageStoreDir) {
        this.genImageStoreDir = genImageStoreDir;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public ImageAgentMessage setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getStoreImageType() {
        return storeImageType;
    }

    public ImageAgentMessage setStoreImageType(String storeImageType) {
        this.storeImageType = storeImageType;
        return this;
    }


    public StoreFilePathFunction getStoreFilePathFunction() {
        return storeFilePathFunction;
    }

    public ImageAgentMessage setStoreFilePathFunction(StoreFilePathFunction storeFilePathFunction) {
        this.storeFilePathFunction = storeFilePathFunction;
        return this;
    }
}
