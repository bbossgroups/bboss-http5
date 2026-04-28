package org.frameworkset.spi.remote.http.auth;
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

import org.frameworkset.spi.remote.http.ClientConfiguration;

/**
 * @author biaoping.yin
 * @Date 2026/4/27
 */
public interface AuthorTokenFunction {
    /**
     * 获取最新令牌
     * @param clientConfiguration
     * @return
     */
    String genAuthorToken(ClientConfiguration clientConfiguration);

    /**
     * 认证header参数名称，一般为Authorization，可以根据不同要求设置为其他值
     * @return
     */
    default String authorHeaderKey(){
        return "Authorization";
    }

    /**
     * 认证token前缀，一般为"Bearer "，如果不需要前缀则返回null即可
     * @return
     */
    default String authorTokenPrefix(){
        return "Bearer ";
    }
    
    /**
     * 是否直接从函数中获取令牌，如果为true，则不从缓存中获取令牌
     * @return
     */
    default boolean directFromFunction(){
        return false;
    }
}
