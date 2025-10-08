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
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//
///**
// * <p>Description: </p>
// * <p></p>
// *
// * @author biaoping.yin
// * @Date 2025/2/6
// */
//public class RequestKerberosUrlUtilsTest {
//    private static Logger logger = LoggerFactory.getLogger(RequestKerberosUrlUtilsTest.class);
//
//    public static void main(String[] args) {
//        params();
////        classPath();
//    }
//
//    public static void params() {
//        String user = "elastic/admin@BBOSSGROUPS.COM";
//        String keytab = "C:\\environment\\es\\8.13.2\\elasticsearch-8.13.2\\config\\elastic.keytab";
//        String krb5Location = "C:\\environment\\es\\8.13.2\\elasticsearch-8.13.2\\config\\krb5.conf";
//        try {
//            RequestKerberosUrlUtils restTest = new RequestKerberosUrlUtils(user, keytab, krb5Location, false);
//            // refer to https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/WebHDFS.html#Open_and_Read_a_File
//            String url_liststatus = "http://192.168.137.1:8200";
//            // location
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    do {
//                        HttpResponse response = restTest.callRestUrl(url_liststatus, user);
//                        if(logger.isDebugEnabled()) {
//                            logger.debug("Status code " + response.getStatusLine().getStatusCode());
//                            logger.debug("message is :" + Arrays.deepToString(response.getAllHeaders()));
//                        }
//                        try {
//                            if(logger.isInfoEnabled()) logger.info("string：\n" + EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                        try {
//                            Thread.currentThread().sleep(500L);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                    while (true);
//                }
//            };
//            Thread t1 = new Thread(runnable);
//            t1.start();
//
//            t1 = new Thread(runnable);
//            t1.start();
//
//            t1 = new Thread(runnable);
//            t1.start();
//
//            t1 = new Thread(runnable);
//            t1.start();
//
//            t1 = new Thread(runnable);
//            t1.start();
//           
//
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
//
//    }
//
//    public static void classPath() {
//        String krb5Location = "C:\\environment\\es\\8.13.2\\elasticsearch-8.13.2\\config\\krb5.conf";
//        System.setProperty("java.security.auth.login.config", "D:\\ysstest\\post\\src\\main\\resources\\http.conf");
//        System.setProperty("java.security.krb5.conf", krb5Location);
//        try {
//            RequestKerberosUrlUtilsClassPath restTest = new RequestKerberosUrlUtilsClassPath();
//            // refer to https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/WebHDFS.html#Open_and_Read_a_File
//            String url_liststatus = "http://192.168.137.1:9200";
//            // location
//            HttpResponse response = restTest.get(url_liststatus);
//            InputStream is = response.getEntity().getContent();
//            System.out.println("Status code " + response.getStatusLine().getStatusCode());
//            System.out.println("message is :" + Arrays.deepToString(response.getAllHeaders()));
//            System.out.println("string：\n" + EntityUtils.toString(response.getEntity(),"UTF-8"));
//
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
//
//    }
//}
