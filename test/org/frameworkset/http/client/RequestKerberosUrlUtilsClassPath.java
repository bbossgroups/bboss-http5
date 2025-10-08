//package org.frameworkset.http.client;
///**
// * Copyright 2025 bboss
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//import org.apache.http.HttpResponse;
//import org.apache.http.auth.AuthSchemeProvider;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.Credentials;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.AuthSchemes;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.config.Lookup;
//import org.apache.http.config.RegistryBuilder;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.auth.SPNegoSchemeFactory;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.security.auth.Subject;
//import javax.security.auth.login.Configuration;
//import javax.security.auth.login.LoginContext;
//import javax.security.auth.login.LoginException;
//import java.io.IOException;
//import java.security.Principal;
//import java.security.PrivilegedAction;
//
///**
// * <p>Description: </p>
// * <p></p>
// *
// * @author biaoping.yin
// * @Date 2025/2/6
// */
//public class RequestKerberosUrlUtilsClassPath {
//    public static Logger logger = LoggerFactory.getLogger(RequestKerberosUrlUtilsClassPath.class);
//
//    public RequestKerberosUrlUtilsClassPath() {
//    }
//
//    public RequestKerberosUrlUtilsClassPath(boolean isDebug) {
//        if (isDebug) {
//            System.setProperty("sun.security.spnego.debug", "true");
//            System.setProperty("sun.security.krb5.debug", "true");
//        }
//    }
//
//    public RequestKerberosUrlUtilsClassPath(String krb5Location, boolean isDebug) {
//        System.setProperty("java.security.krb5.conf", krb5Location);
//    }
//
//    //模拟curl使用kerberos认证
//    private static HttpClient buildSpengoHttpClient() {
//        HttpClientBuilder builder = HttpClientBuilder.create();
//        Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create().
//                register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
//        builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
//        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {
//            @Override
//            public Principal getUserPrincipal() {
//                return null;
//            }
//
//            @Override
//            public String getPassword() {
//                return null;
//            }
//        });
//        builder.setDefaultCredentialsProvider(credentialsProvider);
//        CloseableHttpClient httpClient = builder.build();
//        return httpClient;
//    }
//
//    public HttpResponse get(final String url) {
//        try {
//
//            Subject serviceSubject = getSubject();
//            return Subject.doAs(serviceSubject, new PrivilegedAction<HttpResponse>() {
//                HttpResponse httpResponse = null;
//
//                @Override
//                public HttpResponse run() {
//                    try {
//                        HttpUriRequest request = new HttpGet(url);
//                        HttpClient spnegoHttpClient = buildSpengoHttpClient();
//                        httpResponse = spnegoHttpClient.execute(request);
//                        return httpResponse;
//                    } catch (IOException ioe) {
//                        ioe.printStackTrace();
//                    }
//                    return httpResponse;
//                }
//            });
//        } catch (Exception le) {
//            le.printStackTrace();
//        }
//        return null;
//    }
//
//    public HttpResponse post(final String url, final String params) {
//        try {
//
//            Subject serviceSubject = getSubject();
//            return Subject.doAs(serviceSubject, new PrivilegedAction<HttpResponse>() {
//                HttpResponse httpResponse = null;
//
//                @Override
//                public HttpResponse run() {
//                    try {
//                        HttpPost httpPost = new HttpPost();
//                        httpPost.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON));
//                        HttpClient spnegoHttpClient = buildSpengoHttpClient();
//                        httpResponse = spnegoHttpClient.execute(httpPost);
//                        return httpResponse;
//                    } catch (IOException ioe) {
//                        ioe.printStackTrace();
//                    }
//                    return httpResponse;
//                }
//            });
//        } catch (Exception le) {
//            le.printStackTrace();
//        }
//        return null;
//    }
//
//    private Subject getSubject() throws LoginException {
//        String property = System.getProperty("java.security.auth.login.config");
//        if (null != property) {
//            Configuration configuration = Configuration.getConfiguration();
//            //认证模块：Krb5Login
//            LoginContext lc = new LoginContext("Krb5Login", null, null, configuration);
//            lc.login();
//            return lc.getSubject();
//        }
//        return new Subject();
//    }
//}
