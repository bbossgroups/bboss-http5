package org.frameworkset.spi.feishu;
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
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.frameworkset.spi.remote.http.auth.AuthorTokenFunction;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author biaoping.yin
 * @Date 2026/4/27
 */
public class FeishuAuthorTokenFunction implements AuthorTokenFunction {

   
    @Override
    public String genAuthorToken(ClientConfiguration clientConfiguration) {
            String feishuDatasource = clientConfiguration.getDatasource() ;
            String appId = clientConfiguration.getExtendConfig("appId");
            String appSecret = clientConfiguration.getExtendConfig("appSecret");
            if(SimpleStringUtil.isEmpty(appId) || SimpleStringUtil.isEmpty(appSecret)){
                throw new FeishuException("app id or app secret is empty:appId="+appId+",appSecret="+appSecret);
            }
            String url = "/open-apis/auth/v3/tenant_access_token/internal";
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("app_id",appId);
            params.put("app_secret",appSecret);
            Map tenantAccessToken = null;
            int times = 10;
            do {
                try {
                    tenantAccessToken = HttpRequestProxy.sendJsonBody(feishuDatasource,params,url,Map.class);
                    break;
                } catch (Exception e) {
                    times--;
                    if(times < 0){
                        throw new FeishuException("get tenant access token failed:",e);
                    }
//						throw new DataImportException("get tenant access token failed:", e);
                }
            }while(true);
            return (String)tenantAccessToken.get("tenant_access_token");
    }
}
