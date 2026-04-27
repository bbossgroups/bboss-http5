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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author biaoping.yin
 * @Date 2026/3/31
 */
public class AuthorTokenHolder {
    private String token;
    private long expireTime;
    private AuthorTokenFunction refreshTokenFunction;
    private Thread refreshThread;
    private ReadWriteLock readWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private Logger logger = LoggerFactory.getLogger(AuthorTokenHolder.class);
    private ClientConfiguration clientConfiguration;
    
    private boolean refreshFailed;
    private boolean firsted = true;
    private boolean stopped;
    
    public AuthorTokenHolder(ClientConfiguration clientConfiguration,AuthorTokenFunction refreshTokenFunction, long expireTime) {
        this.clientConfiguration = clientConfiguration;
        this.expireTime = expireTime;
        this.refreshTokenFunction = refreshTokenFunction;
//        refreshToken(false);
        refreshThread = new Thread(() -> {
            while (true) {
                try {
                    if(stopped)
                        break;
                    Thread.sleep(AuthorTokenHolder.this.expireTime);
                } catch (InterruptedException e) {
                    break;
                }
                if(stopped)
                    break;
                try {
                    refreshToken(false);
                }
                catch (Exception e){
                    logger.error("refreshToken error",e);
                }
            }
            
        });
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    public AuthorTokenFunction getRefreshTokenFunction() {
        return refreshTokenFunction;
    }
    public String getAuthorHeaderKey(){
        return refreshTokenFunction.authorHeaderKey();
    }
    public String getAuthorTokenPrefix(){
        return refreshTokenFunction.authorTokenPrefix();
    }


    private void refreshToken(boolean fromGetToken){
        writeLock.lock();
        try {
            if(firsted){
                AuthorDisable.setAuthorDisable(true);
                try {
                    
                    token = refreshTokenFunction.genAuthorToken(clientConfiguration);
                }
                finally {
                    AuthorDisable.setAuthorDisable(null);
                }
                firsted = false;
                return;
            }
            if(fromGetToken && !refreshFailed){
                return;
            }
            AuthorDisable.setAuthorDisable(true);
            try {

                token = refreshTokenFunction.genAuthorToken(clientConfiguration);
            }
            finally {
                AuthorDisable.setAuthorDisable(null);
            }
            if (refreshFailed) {                
                refreshFailed = false;
            }
        }
        catch (Exception e){
            logger.error("refreshToken error",e);
            refreshFailed = true;
        }
        finally {
            writeLock.unlock();
        }
    }
    public void destroy(){
        stopped = true;
        refreshThread.interrupt();
        try {
            refreshThread.join();
        } catch (InterruptedException e) {
        }
    }
    
    public String getToken() {
        if(firsted){
            refreshToken(true);
        }
        // 如果刷新失败，则再次刷新，避免使用无效的token
        if(refreshFailed){
            refreshToken(true);
        }
        if(token == null){            
            refreshToken(true);
        }
        
        readLock.lock();
        try {
            
            return token;
        }
        finally {
            readLock.unlock();
        }
    }
     
    
    
    
}
