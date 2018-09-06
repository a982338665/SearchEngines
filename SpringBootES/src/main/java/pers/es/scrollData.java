package pers.es;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

public class scrollData {
    private static final CloseableHttpClient httpclient = HttpClients.createDefault();
    public static void main(String[] args) throws UnsupportedEncodingException {

        HttpGet httpget=new HttpGet("http://118.31.236.154:9202/test-20180824/_mapping");
//        httpget.addHeader("Content-Type", "application/json");
//        httpget.getParams().setParameter( "http.protocol.content-charset","utf-8");
//        //把数据改为String格式
//        //String stringObj = JSONObject.toJSONString(param);
//        String stringObj ="";
//        httpget.
//        httpget.setEntity(new StringEntity(stringObj));
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
                JSONObject jsStr = JSONObject.parseObject(result);
                Object o = jsStr.get("test-20180824");
                JSONObject jsStr2= JSONObject.parseObject(o.toString());

                Object mappings = jsStr2.get("mappings");
                JSONObject jsStr3= JSONObject.parseObject(mappings.toString());
                Set<Map.Entry<String, Object>> entries = jsStr3.entrySet();
                for (Map.Entry<String, Object> map :entries
                     ) {
                    String key = map.getKey();
                    System.err.println(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
}
