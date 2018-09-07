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

@SuppressWarnings("all")
public class testt {


    //默认仓库指定--即备份存储位置
    private static final String es_default = "es_default";
    //默认仓库路径设置--远程仓库实际位置:需要与elasticsearch.yml中配置的path.resp一致
    private static final String es_default_uri = "/usr/share/elasticsearch/data/";
    //默认的ES访问路径--
    private static final String es_default_fact_uri = "http://127.0.0.1:9202/_snapshot/";

    private static final CloseableHttpClient httpclient = HttpClients.createDefault();



    /**
     *   //创建仓库+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * @param uri       --访问地址基础路径
     * @param resName   --备份仓库命名
     * @param remoteUri --远程仓库路径
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createRespon(String uri,String resName,String remoteUri) throws UnsupportedEncodingException {

        if(uri ==null){
            uri=  es_default_fact_uri;
        }
        if(resName==null){
            resName=es_default;
        }
        if(remoteUri==null){
            remoteUri=es_default_uri;
        }
        HttpPut httpPut=new HttpPut(uri+resName);
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.getParams().setParameter( "http.protocol.content-charset","utf-8");
        //把数据改为String格式
        //String stringObj = JSONObject.toJSONString(param);
        String stringObj ="{\n" +
                "    \"type\": \"fs\", \n" +
                "    \"settings\": {\n" +
                "        \"location\": \"" +
                remoteUri +
                resName +
                "\" \n" +
                "    }\n" +
                "}";
        httpPut.setEntity(new StringEntity(stringObj));
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPut);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
                System.err.println(result);
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
        return result;
    }

    /**
     *
     * @param copyName  --备份名称
     * @param args      --要备份的索引名称,如不写，则备份所有
     * @return
     * @throws UnsupportedEncodingException
     */
  /*  public static String copyIndexs(String copyName,String ... args) throws UnsupportedEncodingException {

        StringBuffer indexs=new StringBuffer("");
        if(copyName==null){
            return "";
        }
        if(args.length!=0){
            for (String arg:args
                 ) {
                if(arg.equals(args[0])){
                    indexs.append(arg);
                }else{
                    indexs.append(","+arg);
                }
            }
        }


        HttpPut httpPut=new HttpPut(uri+resName);
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.getParams().setParameter( "http.protocol.content-charset","utf-8");
        //把数据改为String格式
        //String stringObj = JSONObject.toJSONString(param);
        String stringObj ="{\n" +
                "    \"type\": \"fs\", \n" +
                "    \"settings\": {\n" +
                "        \"location\": \"" +
                remoteUri +
                resName +
                "\" \n" +
                "    }\n" +
                "}";
        httpPut.setEntity(new StringEntity(stringObj));
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPut);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
                System.err.println(result);
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
        return result;
    }*/
    public static void main(String[] args) throws UnsupportedEncodingException {

        String my_back = createRespon("http://127.0.0.1:9202/_snapshot/", null, null);
        if(!my_back.contains("true")){
            System.err.println("创建仓库失败====");
            return;
        }




    }
}
