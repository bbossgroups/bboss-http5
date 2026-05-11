package org.frameworkset.spi.reactor;
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

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

import java.io.IOException;

/**
 * @author biaoping.yin
 * @Date 2026/5/11
 */
public class ReactorReponseUtil {
    private static Logger logger = LoggerFactory.getLogger(ReactorReponseUtil.class);
    private static void processStreamResponse(ClassicHttpResponse response,
                                              FluxSink<String> sink,
                                              CommonStreamDataHandler<String> streamDataHandler) throws IOException {

        FluxSinkStatus fluxSinkStatus = null;
        try  {
            fluxSinkStatus = new FluxSinkStatus(response,streamDataHandler.getHttpUriRequestBase());
            FluxSinkStatus _fluxSinkStatus = fluxSinkStatus;
            // 添加取消监听器
            sink
//                    .onCancel(() -> {
//                // 当订阅被取消时执行
//                logger.info("Sink cancelled");
//                _fluxSinkStatus.dispose();
//                _fluxSinkStatus.releaseResources();
//                // 执行清理工作
//            })
                    .onDispose(() -> {
                        // 当 sink 被处置时执行（包括正常完成、错误和取消）
                        if(logger.isDebugEnabled()) {
                            logger.debug("Sink disposed");
                        }
                        _fluxSinkStatus.dispose();
                        _fluxSinkStatus.releaseResources();
                        // 执行清理工作
                    });

            String line;

            while ( !sink.isCancelled() && (line = fluxSinkStatus.readLine()) != null ) {
                if(fluxSinkStatus.isDispose()){
                    break;
                }
                sink.next(line);
//                logger.info(line);




            }


        }
        finally {
            fluxSinkStatus.releaseResources();
        }
    }
    public static void handleStreamResponse(String url, ClassicHttpResponse response,
                                            FluxSink<String> sink, String message, CommonStreamDataHandler<String> streamDataHandler)
            throws IOException, ParseException {

        int status = response.getCode();

        if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
            processStreamResponse(response, sink,streamDataHandler );
        } else {
            HttpEntity entity = response.getEntity();
            String data = message;
            if (entity != null ) {
                if (logger.isDebugEnabled()) {
                    logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
                }
                throw new ReactorCallException(new StringBuilder().append("Request url:")
                        .append(url).append(",error,").append("status=")
                        .append(status).append(":")
                        .append(EntityUtils.toString(entity))
                        .append(",\r\n use message:").append( data).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString()));
            }
            else {
                throw new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status)
                        .append(",\r\n use message:").append( data).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status).toString()));
            }
        }
    }
    private static void processStreamResponse(ClassicHttpResponse response,
                                              DataCollector dataCollector,
                                              CommonStreamDataHandler<String> streamDataHandler) throws IOException {

        FluxSinkStatus fluxSinkStatus = null;
        try  {
            fluxSinkStatus = new FluxSinkStatus(response,streamDataHandler.getHttpUriRequestBase());
//            // 添加取消监听器
//            sink.onCancel(() -> {
//                // 当订阅被取消时执行
//                logger.info("Subscription cancelled");
//                fluxSinkStatus.cancel();
//                // 执行清理工作
//            });

            String line;

            do{
                line = fluxSinkStatus.readLine();

                if(line != null) {
                    dataCollector.collector(line);
                }
                else{
                    break;
                }
            }while (true);
//			while (  (line = fluxSinkStatus.readLine()) != null ) {
//				if(fluxSinkStatus.isDispose()){
//					break;
//				}
//				dataCollector.collector(line);
////                logger.info(line);
//				
//				
//				
//				
//			}


        }
        finally {
            fluxSinkStatus.releaseResources();
        }
    }

    public static <T> void handleStreamResponse(String url, ClassicHttpResponse response,
                                                String data, DataCollector dataCollector,CommonStreamDataHandler<String> streamDataHandler)
            throws IOException, ParseException {

        int status = response.getCode();

        if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
            processStreamResponse(response,  dataCollector,streamDataHandler);
        } else {
            HttpEntity entity = response.getEntity();

            if (entity != null ) {
                if (logger.isDebugEnabled()) {
                    logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
                }
                throw new ReactorCallException(new StringBuilder().append("Request url:")
                        .append(url).append(",error,").append("status=")
                        .append(status).append(":")
                        .append(EntityUtils.toString(entity))
                        .append(",\r\n use message:").append( data).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",error,").append("status=").append(status).append(":").append(EntityUtils.toString(entity)).toString()));
            }
            else {
                throw new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status)
                        .append(",\r\n use message:").append( data).toString());
//                sink.error(new ReactorCallException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append(status).toString()));
            }
        }
    }
}
