package org.frameworkset.spi.remote.http;

import com.frameworkset.util.SimpleStringUtil;
import org.apache.hc.core5.http.HttpEntity;

import java.io.IOException;
import java.util.List;


public abstract class BaseResponseHandler<T> extends StatusResponseHandler<T> {


    /**
     * 标记是否在响应对象中放置请求报文，便于在异常处理中放置请求报文数据
     */
    protected boolean enableSetRequestBody;
    protected String requestBody;



    protected boolean truncateLogBody;
	protected <T> T converJson(HttpEntity entity,Class<T> clazz) throws IOException {
        return ResponseUtil.converJson(entity,clazz);
		
	}

    protected <T> List<T> converJson2List(HttpEntity entity, Class<T> clazz) throws IOException {
        return ResponseUtil.converJson2List(entity,clazz);

    }

    public boolean isEnableSetRequestBody() {
        return enableSetRequestBody;
    }

    public void setEnableSetRequestBody(boolean enableSetRequestBody) {
        this.enableSetRequestBody = enableSetRequestBody;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    public boolean isTruncateLogBody() {
        return truncateLogBody;
    }

    public void setTruncateLogBody(boolean truncateLogBody) {
        this.truncateLogBody = truncateLogBody;
    }
}
