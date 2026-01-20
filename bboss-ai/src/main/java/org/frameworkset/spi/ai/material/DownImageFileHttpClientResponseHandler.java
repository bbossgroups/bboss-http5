package org.frameworkset.spi.ai.material;
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
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.frameworkset.spi.ai.model.ChatObject;
import org.frameworkset.spi.ai.model.ImageAgentMessage;
import org.frameworkset.spi.remote.http.ClientConfiguration;

import java.io.*;
import java.util.Base64;

/**
 * @author biaoping.yin
 * @Date 2026/1/20
 */
public class DownImageFileHttpClientResponseHandler implements HttpClientResponseHandler<String> {
    private ClientConfiguration clientConfiguration;
    private String imageUrl;
    private ImageAgentMessage imageAgentMessage;
    public DownImageFileHttpClientResponseHandler(ClientConfiguration clientConfiguration, ImageAgentMessage imageAgentMessage, String imageUrl) {
        this.clientConfiguration = clientConfiguration;
        this.imageUrl = imageUrl;
        this.imageAgentMessage = imageAgentMessage;

    }

    @Override
    public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        if (response.getCode() == 200) {
            String storeFilePath = null;
            if(imageAgentMessage.getStoreFilePathFunction() != null){
                storeFilePath = imageAgentMessage.getStoreFilePathFunction().getStoreFilePath(imageUrl);
            }
            else{
                storeFilePath = imageAgentMessage.getStoreFilePath();
            }
            String targetPath = generateFilePath(imageUrl,storeFilePath); // 根据URL生成目标文件路径
            File file = new File(targetPath);
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            try (InputStream inputStream = response.getEntity().getContent();
                 FileOutputStream outputStream = new FileOutputStream(targetPath)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            if(imageAgentMessage.getEndpoint() == null) {
                return targetPath; // 返回下载文件路径
            }
            else{
                return SimpleStringUtil.getRealPath(imageAgentMessage.getEndpoint(),storeFilePath);
            }
        } else {
            throw new IOException("Download failed: " + response.getCode());
        }
    }
   
    private String generateFilePath(String imageUrl,String storeFilePath) {
        // 根据imageUrl生成本地文件路径
        String fileName = SimpleStringUtil.getRealPath(imageAgentMessage.getGenImageStoreDir(),storeFilePath);
        return fileName;
    }


    
}
