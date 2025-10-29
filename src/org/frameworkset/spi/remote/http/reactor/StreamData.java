package org.frameworkset.spi.remote.http.reactor;
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

/**
 * @author biaoping.yin
 * @Date 2025/10/29
 */
public class StreamData {

    private int type = ServerEvent.CONTENT;
    private String data;
    
    public StreamData(){
    }
    public StreamData(int type,String data){
    	this.type = type;
    	this.data = data;
    }
    public int getType() {
		return type;
	}
    public String getData() {
		return data;
	}
    public void setData(String data) {
		this.data = data;
	}
    public void setType(int type) {
		this.type = type;
	}
    
    public boolean isEmpty(){
        return data == null || data.length() == 0;
    }
    

}
