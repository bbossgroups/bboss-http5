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

import com.frameworkset.util.SimpleStringUtil;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.*;
import org.apache.hc.client5.http.impl.auth.BasicSchemeFactory;
import org.apache.hc.client5.http.impl.auth.DigestSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SystemDefaultCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.callback.HttpClientBuilderCallback;

import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/2/7
 */
public abstract class BaseRequestKerberosUrlUtils implements HttpClientBuilderCallback {
    protected KerberosConfig kerberosConfig;

    protected Configuration configuration;
    protected ClientConfiguration clientConfiguration;

    public BaseRequestKerberosUrlUtils(KerberosConfig kerberosConfig,ClientConfiguration clientConfiguration) {
        this.kerberosConfig = kerberosConfig;
        this.clientConfiguration = clientConfiguration;
        System.setProperty("java.security.krb5.conf", kerberosConfig.getKrb5Location());
        if(SimpleStringUtil.isNotEmpty(kerberosConfig.getUseSubjectCredsOnly())){
            System.setProperty("javax.security.auth.useSubjectCredsOnly", kerberosConfig.getUseSubjectCredsOnly());
        }
        if (kerberosConfig.getDebug() != null && kerberosConfig.getDebug().equals("true")) {
            System.setProperty("sun.security.spnego.debug", "true");
            System.setProperty("sun.security.krb5.debug", "true");
        }
    }
    @Override
    public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder , ClientConfiguration clientConfiguration) throws Exception{
        return builder;
    }
    //模拟curl使用kerberos认证
    public  void buildSpengoHttpClient(HttpClientBuilder builder) {

//        org.apache.hc.client5.http.impl.DefaultAuthenticationStrategy s;
        

        Lookup<AuthSchemeFactory> authSchemeRegistry = RegistryBuilder.<AuthSchemeFactory>create().register(StandardAuthScheme.BASIC, BasicSchemeFactory.INSTANCE)
                .register(StandardAuthScheme.DIGEST, DigestSchemeFactory.INSTANCE)
                .register(StandardAuthScheme.SPNEGO, SPNegoSchemeFactory.DEFAULT).build();
//        Lookup<AuthSchemeFactory> authSchemeRegistry = RegistryBuilder.<AuthSchemeFactory>create().register(StandardAuthScheme.SPNEGO, new SPNegoSchemeFactory(
//                org.apache.hc.client5.http.auth.KerberosConfig.custom()
//                        .setStripPort(org.apache.hc.client5.http.auth.KerberosConfig.Option.DEFAULT) // 默认情况下剥离端口
//                        .setUseCanonicalHostname(org.apache.hc.client5.http.auth.KerberosConfig.Option.DEFAULT) // 使用规范主机名
//                        .build(),
//                SystemDefaultDnsResolver.INSTANCE
//        )).build();
        builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
        builder.setTargetAuthenticationStrategy(new CustomAuthenticationStrategy());
        SystemDefaultCredentialsProvider credentialsProvider = new SystemDefaultCredentialsProvider();
//        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(new AuthScope(null,  -1), new Credentials() {
//            @Override
//            public Principal getUserPrincipal() {
//                return null;
//            }
//
//            @Override
//            public char[] getPassword() {
//                return null;
//            }
//        });
        builder.setDefaultCredentialsProvider(credentialsProvider);
//        CloseableHttpClient httpClient = builder.build();
//        return httpClient;
    }

    protected String getLoginContextName(){
        String name = kerberosConfig.getLoginContextName();
        if(name == null || name.equals("")){
            name = "Krb5Login";
        }
        return name;
    }
    protected abstract Subject getSubject() throws LoginException ;
 
    public <T> T callRestUrl(KerberosCallback<T> kerberosCallback) throws Exception {

        try {

            Subject serviceSubject = getSubject();
            return Subject.doAs(serviceSubject, new PrivilegedAction<T>() {

                @Override
                public T run() {
                    try {
                        return kerberosCallback.call();
                    } catch (Exception e) {
                        throw new KerberosCallException(e);
                    }
                }
            });
        } catch (KerberosCallException le) {
            throw (Exception) le.getCause();
        } catch (Exception le) {
            throw le;
        }

    }

    public void afterStart() {
        
    }

    public void close() {
    }
}
