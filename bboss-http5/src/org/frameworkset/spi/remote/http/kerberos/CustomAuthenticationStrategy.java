package org.frameworkset.spi.remote.http.kerberos;
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

import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.impl.DefaultAuthenticationStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author biaoping.yin
 * @Date 2025/10/9
 */
public class CustomAuthenticationStrategy extends DefaultAuthenticationStrategy {
    private static final List<String> CUSTOM_SCHEME_PRIORITY =
            Collections.unmodifiableList(Arrays.asList(
                    StandardAuthScheme.BEARER,
                    StandardAuthScheme.DIGEST,
                    StandardAuthScheme.BASIC,StandardAuthScheme.SPNEGO,StandardAuthScheme.KERBEROS));

    protected List<String> getSchemePriority() {
        return CUSTOM_SCHEME_PRIORITY;
    }
}
