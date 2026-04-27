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

/**
 * 刷新认证令牌时，禁用认证头添加
 * @author biaoping.yin
 * @Date 2026/4/27
 */
public class AuthorDisable {
    private static ThreadLocal<Boolean> authorDisable = new ThreadLocal<Boolean>();
    public static void setAuthorDisable(Boolean authorDisable){
        AuthorDisable.authorDisable.set(authorDisable);
    }
    public static Boolean getAuthorDisable(){
        return authorDisable.get();
    }

}
