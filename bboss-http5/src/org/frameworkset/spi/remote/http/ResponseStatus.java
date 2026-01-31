package org.frameworkset.spi.remote.http;
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
 * @author biaoping.yin
 * @Date 2026/1/26
 */
public class ResponseStatus {
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String ERROR = "error";
    public static final String TIMEOUT = "timeout";
    public static final String CANCEL = "cancel";
    public static final String NOT_FOUND = "not_found";
    public static final String NOT_SUPPORT = "not_support";
    public static final String NOT_AUTHORIZED = "not_authorized";
    public static final String NOT_LOGIN = "not_login";
    
    public static final String SUCCESS_CODE = "200";
    public static final String FAIL_CODE = "500";
    public static final String ERROR_CODE = "501";
    public static final String TIMEOUT_CODE = "502";
    public static final String CANCEL_CODE = "506";
    public static final String NOT_FOUND_CODE = "404";
    public static final String NOT_SUPPORT_CODE = "405";
    public static final String NOT_AUTHORIZED_CODE = "403";
    public static final String NOT_LOGIN_CODE = "401";
    
}
