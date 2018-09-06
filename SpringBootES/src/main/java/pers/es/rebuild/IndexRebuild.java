package pers.es.rebuild;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("all")
public class IndexRebuild {

    private static final CloseableHttpClient httpclient = HttpClients.createDefault();
    private static final CloseableHttpClient httpclient2 = HttpClients.createDefault();
    /**
     * 记录数据总数
     */
    public static int count=0;
    /**
     * 记录成功数
     */
    public static int success=0;
    /**
     * 记录每个类型数据同步耗时
     */
    public static List<String > list=new ArrayList<String>();

    /**
     * 创建新索引
     * @return
     */
    private static boolean CreateIndex(){
        String uri=PropertyUtil.getProperty("uri")+PropertyUtil.getProperty("rebuild_name");
        HttpPut httpPut = getHttpPut(uri,"\\SpringBootES\\src\\main\\resources\\template\\mapping.txt",null);
        String result = getResponResult(httpPut);
        if(result.contains("true")){
            return true;
        }
        return false;
    }





    /**
     * 获取需要重建索引的类型名称并创建相应csv文件
     * @return
     */
    private static List<String> getIndexTypes(){
        List<String> list=new ArrayList<String>();
        HttpGet httpget=new HttpGet(PropertyUtil.getProperty("uri_first")+PropertyUtil.getProperty("index_name")+"/_mapping");
        String result = getResponResult(httpget);
        JSONObject jsStr = JSONObject.parseObject(result);
        Object o = jsStr.get(PropertyUtil.getProperty("index_name"));
        JSONObject jsStr2= JSONObject.parseObject(o.toString());

        Object mappings = jsStr2.get("mappings");
        JSONObject jsStr3= JSONObject.parseObject(mappings.toString());
        Set<Map.Entry<String, Object>> entries = jsStr3.entrySet();
        for (Map.Entry<String, Object> map :entries
                ) {
            String key = map.getKey();
            System.err.println(key);
            list.add(key);
//                    //所创建文件的路径
//                    File file1=new File(".");
//                    String path=file1.getAbsoluteFile().getParent()+"\\SpringBootES\\src\\main\\resources\\template\\";
//                    File f = new File(path);
//                    if(!f.exists()){
//                         f.mkdirs();//创建目录
//                     }
//                    String fileName =key+ ".csv";//文件名及类型
//                    File file = new File(path, fileName);
//                    if(!file.exists()){
//                            try {
//                                file.createNewFile();
//                            } catch (IOException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                    }
        }
        return list;
    }


    /**
     * 读取类型的所有文件并写入相应文件---写入备份
     * @param type
     * @return
     */
    private static boolean getTypeDataWrite(String type) throws UnknownHostException, UnsupportedEncodingException {
        //首次scroll查询+++++++++++++++++++++++++++++++++++++++++++++++++++
        long start = System.currentTimeMillis();
        String uri =PropertyUtil.getProperty("uri_first") + PropertyUtil.getProperty("index_name") + "/" + type+"/" + "_search?scroll=2m";
//        System.err.println(uri);
        HttpPost httpPost = getHttpPost(uri,"\\SpringBootES\\src\\main\\resources\\template\\scroll_first.txt",null);
        String result = getResponResult(httpPost);
        Object scroll_id = dataAnaylizer(type, result);
        //递归查询其余数据
        ScrollData(type, scroll_id);
        long end=System.currentTimeMillis();
        list.add(type+"-->数据总数："+count+"-->同步成功数："+success+"-->所耗时长：--"+formatDuring(end-start));
        count=0;
        success=0;
        return true;
    }

    /**
     *
     * @param 要转换的毫秒数
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + "天" + hours + "小时 " + minutes + "分钟"
                + seconds + "秒";
    }
    /**
     * 数据迁移
     * @param type
     * @param scroll_id
     * @return
     */
    private static Object ScrollData(String type, Object scroll_id) {
        //scroll非首次查询+++++++++++++++++++++++++++++++++++++++++++++++
        String uri2 = PropertyUtil.getProperty("uri_first") +"_search"+"/scroll";
//        System.err.println(uri);
        String json="  {\n" +
                "                \"scroll\":\"2m\",\n" +
                "                \"scroll_id\":\"" +
                scroll_id+
                "\" }";
//        System.err.println(json);
        HttpPost httpPost2 = getHttpPost(uri2,null,json);
        String result2 = getResponResult(httpPost2);
        if(result2.contains("\"hits\":[]")){
            System.out.println("ALL Data is finashed!====================================++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            return null;
        }else{
            Object scroll_id2 = dataAnaylizer(type, result2);
            return ScrollData(type,scroll_id2);
        }

    }

    /**
     * 数据解析及新索引添加
     * @param type
     * @param result
     * @return
     */
    private static Object dataAnaylizer(final String type, String result) {
        JSONObject jsStr = JSONObject.parseObject(result);
        Object scroll_id = jsStr.get("_scroll_id");
        System.err.println(scroll_id);
        Object hits = jsStr.get("hits");
        JSONObject hitss = JSONObject.parseObject(hits.toString());
        final Object total = hitss.get("total");
        System.err.println("总条数--->"+total);
        Object hit = hitss.get("hits");
        JSONArray myJsonArray = JSONArray.parseArray(hit.toString());
        //线程优化
/*      ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(30);
        for (int i = 0; i < myJsonArray.size(); i++) {
            final JSONObject jsonObject = (JSONObject) myJsonArray.get(i);
            //获取索引，类型，id
            Object index = jsonObject.get("_index");
            Object type1 = jsonObject.get("_type");
            final Object id = jsonObject.get("_id");
            count++;
            System.err.println("-->"+index+"-->"+type1+"-->"+id+"-->"+count);
            newFixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Object data = jsonObject.get("_source");
//                  System.err.println(data);
                    String url= PropertyUtil.getProperty("uri") +PropertyUtil.getProperty("rebuild_name")+"/" + type+"/"+id;
                    HttpPut httpPut = getHttpPut(url,null,data.toString());

//                  System.err.println(url);
                    String result2 = getResponResult(httpPut);
                    if(result2.contains("successful")){
//                             System.out.println(result2);
                        System.out.println(total+"--->"+id+"------------------------------------------------------------------------>"+(++success));
//                System.out.println(result2);
                    }
                }
            });
        }*/
        for (int i = 0; i < myJsonArray.size(); i++) {
            final JSONObject jsonObject = (JSONObject) myJsonArray.get(i);
            //获取索引，类型，id
            Object index = jsonObject.get("_index");
            Object type1 = jsonObject.get("_type");
            final Object id = jsonObject.get("_id");
            count++;
            System.err.println("-->"+index+"-->"+type1+"-->"+id+"-->"+count);
            Object data = jsonObject.get("_source");
//          System.err.println(data);
            String url= PropertyUtil.getProperty("uri") +PropertyUtil.getProperty("rebuild_name")+"/" + type+"/"+id;
            HttpPut httpPut = getHttpPut(url,null,data.toString());
//          System.err.println(url);
            String result2 = getResponResult(httpPut);
            if(result2.contains("successful")){
//               System.out.println(result2);
                System.out.println(total+"--->"+id+"------------------------------------------------------------------------>"+(++success));
//          System.out.println(result2);
           }
        }
        return scroll_id;
    }

    /**
     * 获取httppost请求
     * @param uri
     * @param filepath
     * @return
     */
    private static HttpPost getHttpPost(String uri,String filepath,String json) {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        if(filepath!=null){
            File file = new File(".");
            String filePath = file.getAbsoluteFile().getParent()+filepath;
//        String filePath = file.getAbsoluteFile().getParent() + "\\SpringBootES\\src\\main\\resources\\template\\scroll_first.txt";
            json = ReadFileUtil.readTxtFileALL(filePath);
        }
        try {
            httpPost.setEntity(new StringEntity(json,"utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpPost;
    }
    /**
     * 获取httpPUT请求
     * @param uri
     * @param filepath
     * @return
     */
    private static HttpPut getHttpPut(String uri,String filepath,String json) {
        HttpPut httpPut=new HttpPut(uri);
        httpPut.addHeader("Content-Type", "application/json");
        if(filepath!=null){
            File file=new File(".");
            String filePath = file.getAbsoluteFile().getParent()+filepath;
            json = ReadFileUtil.readTxtFileALL(filePath);
        };
        try {
            httpPut.setEntity(new StringEntity(json,"utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpPut;
    }
    /**
     * 获取响应结果---string类型
     * @param httpPut
     * @return
     */
    private static <T> String getResponResult(T http) {
        CloseableHttpResponse response = null;
        try {
            if(http instanceof HttpPut){
                response = httpclient.execute((HttpPut)http);
            }else if(http instanceof HttpPost){
                response = httpclient.execute((HttpPost)http);
            }else if(http instanceof HttpGet){
                response = httpclient.execute((HttpGet)http);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
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

    public static void main(String[] args) throws UnknownHostException, UnsupportedEncodingException {
        CreateIndex();
        List<String> indexTypes = getIndexTypes();
        for (String type:indexTypes) {
            getTypeDataWrite(type);
        }
        for (String s:list
             ) {
            System.out.println(s);
        }
    }
}
