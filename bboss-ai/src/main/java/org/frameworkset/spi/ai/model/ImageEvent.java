package org.frameworkset.spi.ai.model;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 封装视觉模型图片事件
 * @author biaoping.yin
 * @Date 2025/10/30
 */
public class ImageEvent extends MultimodalGeneration{
   
    /**
     * 图片地址
     */
    private String imageUrl;
    private String imageSize;
    private List<String> imageUrls;

    private List<String> imageSizes;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void addImageUrl(String imageUrl) {
        if(imageUrls == null){
            imageUrls = new ArrayList<>();
        }
        imageUrls.add(imageUrl);
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public String getImageSize() {
        return imageSize;
    }
    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public List<String> getImageSizes() {
        return imageSizes;
    }
    public void setImageSizes(List<String> imageSizes) {
        this.imageSizes = imageSizes;
    }
    public void addImageSize(String imageSize) {
        if(imageSizes == null){
            imageSizes = new ArrayList<>();
        }
        imageSizes.add(imageSize);
    }
}
