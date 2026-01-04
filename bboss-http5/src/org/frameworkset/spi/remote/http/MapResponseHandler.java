package org.frameworkset.spi.remote.http;



import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Map;

public class MapResponseHandler extends BaseResponseHandler<Map> implements URLResponseHandler<Map> {

	public MapResponseHandler() {
		// TODO Auto-generated constructor stub
	}
	
	 @Override
     public Map handleResponse(final ClassicHttpResponse response)
             throws ClientProtocolException, IOException, ParseException {
		 int status = initStatus(  response);

         if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
             HttpEntity entity = response.getEntity();
			 if(entity.getContentLength() == 0){
				return null;
			 }
             else{
             	return super.converJson(entity,Map.class);
			 }

			 //return entity != null ? EntityUtils.toString(entity) : null;
         } else {
             HttpEntity entity = response.getEntity();
//             if (entity != null )
////            	 return SimpleStringUtil.json2Object(entity.getContent(), Map.class);
//				 throw new HttpRuntimeException(EntityUtils.toString(entity),status);
//             else
//                 throw new HttpRuntimeException("Unexpected response status: " + status,status);
			 throw super.throwException(status,entity);
         }
     }


}
