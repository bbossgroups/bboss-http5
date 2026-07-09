package org.frameworkset.spi.remote.http.callback;
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

import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.frameworkset.spi.remote.http.ClientConfiguration;

/**
 * 初始化时会注入ClientConfiguration配置对象，以便在拦截器方法中获取服务配置参数
 * @author biaoping.yin
 * @Date 2026/7/9
 */
public abstract class ClientConfigurationHttpRequestInterceptor implements HttpRequestInterceptor {
	protected ClientConfiguration clientConfiguration;
	
	public void setClientConfiguration(ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
	}
	
	public ClientConfiguration getClientConfiguration() {
		return clientConfiguration;
	}
}
