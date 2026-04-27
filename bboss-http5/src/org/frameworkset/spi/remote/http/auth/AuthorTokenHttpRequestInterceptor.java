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

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;

/**
 * @author biaoping.yin
 * @Date 2026/4/27
 */
public class AuthorTokenHttpRequestInterceptor implements HttpRequestInterceptor {
    private AuthorTokenHolder authorTokenHolder;
    public AuthorTokenHttpRequestInterceptor(AuthorTokenHolder authorTokenHolder){
        this.authorTokenHolder = authorTokenHolder;
    }
    /**
     * Processes a request.
     * On the client side, this step is performed before the request is
     * sent to the server. On the server side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param request the request to process
     * @param entity  the request entity details or {@code null} if not available
     * @param context the context for the request
     * @throws HttpException in case of an HTTP protocol violation
     * @throws IOException   in case of an I/O error
     */
    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Boolean disable = AuthorDisable.getAuthorDisable();
        //没有禁用认证时，添加认证头
        if(disable == null) {
            request.addHeader(authorTokenHolder.getAuthorHeaderKey(), getValue());
        }
    }

    public String getValue(){
        if(authorTokenHolder.getAuthorTokenPrefix() == null) {
            return authorTokenHolder.getToken();
        }
        else{
            return authorTokenHolder.getAuthorTokenPrefix() + authorTokenHolder.getToken();
        }
    }
}
