package pers.li;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
 
 
/**
 * @author Created by KSpring on 2017/2/1.
 */
public class ElasticSearchHandler {
 
    public static final  String fieldType = "type";
    private String clusterName;
    private String ip;
    private int port;
 
    public ElasticSearchHandler() {
    }
 
    public void setField(String clusterName, String ip, int port) {
        this.clusterName = clusterName;
        this.ip = ip;
        this.port = port;
    }
 
 
    private static final Logger logger = Logger.getLogger(ElasticSearchHandler.class);
 
 
    /**
     * 取得实例
     * @return
     */
    public synchronized TransportClient getTransportClient() {
        TransportClient client = null ;
        try {
            Settings settings = Settings.builder().put("cluster.name", clusterName)
        /*            .put("client.transport.sniff", true)*/
                    .put("client.transport.ping_timeout", "30s").build();
            client = new PreBuiltTransportClient(settings);
            String[] ips = ip.split(",");
            for (int i = 0; i < ips.length; i++) {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ips[i]), port));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }
 
    /**
     * 关闭连接
     * @param client   es客户端
     */
    public void close(TransportClient client) {
        client.close();
    }
 
    /**
     * 为集群添加新的节点
     * @param name
     * @param client   es客户端
     */
    public synchronized void addNode(String name,TransportClient client) {
        try {
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(name), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 删除集群中的某个节点
     * @param client   es客户端
     * @param name
     */
    public synchronized void removeNode(String name,TransportClient client) {
        try {
            client.removeTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(name), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 创建mapping
     * @param index    索引
     * @param type     类型
     * @param client   es客户端
     * @param xMapping mapping描述
     */
    public void createBangMapping(String index, String type, XContentBuilder xMapping,TransportClient client) {
        PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(xMapping);
        client.admin().indices().putMapping(mapping).actionGet();
 
    }
 
    /**
     * 创建索引
     *
     * @param index   索引名称
     * @param client   es客户端
     */
    public void createIndex(String index,TransportClient client) {
        CreateIndexRequest request = new CreateIndexRequest(index);
        client.admin().indices().create(request);
    }
 
    /**
     * 根据信息自动创建索引与mapping
     * 构建mapping描述    有问题
     * @param fieldInfoList  字段信息
     * @param client   es客户端
     * @return
     */
   /* public void createIndexAndCreateMapping(String index, String type,List<FieldInfo> fieldInfoList,TransportClient client) {
        XContentBuilder mapping = null;
        try {
            CreateIndexRequestBuilder cib=client.admin()
                    .indices().prepareCreate(index);
            mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties"); //设置之定义字段
            for(FieldInfo info : fieldInfoList){
                String field = info.getField();
                String dateType = info.getType();
                if(dateType == null || "".equals(dateType.trim())){
                    dateType = "String";
                }
                dateType = dateType.toLowerCase();
                Integer participle = info.getParticiple();
                if("string".equals(dateType)){
                    if(participle == 1) {
                        mapping.startObject(field)
                                .field("type","text")
                                .field("analyzer","ik_smart")
                                .endObject();
                    }else if(participle == 2){
                        mapping.startObject(field)
                                .field("type","text")
                                .field("analyzer","ik_max_word")
                                .endObject();
                    }else {
                        mapping.startObject(field)
                                .field("type","keyword")
                                .field("index","not_analyzed")
                                .endObject();
                    }
 
                }else if("date".equals(dateType)){
                    mapping.startObject(field)
                            .field("type",dateType)
                            .field("format","yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
                            .endObject();
                }else {
                    mapping.startObject(field)
                            .field("type",dateType)
                            .field("index","not_analyzed")
                            .endObject();
                }
 
            }
            mapping.endObject()
                    .endObject();
            cib.addMapping(type, mapping);
            cib.execute().actionGet();
        } catch (IOException e) {
            System.out.println("创建索引发生异常");
        }
    }*/
 
 
    /**
     * 创建索引与mapping模板
     * @param index 索引字段
     * @param type  类型
     * @param client  客户端
     * @throws IOException
     */
    @SuppressWarnings("all")
    public void createMapping(String index, String type,TransportClient client) throws IOException {
 
        CreateIndexRequestBuilder cib=client.admin()
                .indices().prepareCreate(index);
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties") //设置之定义字段
 
 
                .startObject("id")//字段id
                .field("type","integer")//设置数据类型
                .field("index","not_analyzed")
 
                .endObject()
                .startObject("classs")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("courseClass")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("courseClassExam")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("examnum")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("ok")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("room")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("score")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("student")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("updatetime")
                .field("type","integer")
                .field("index","not_analyzed")
                .endObject()
 
                .startObject("desc")
                .field("type","text")
                .field("analyzer","ik_smart")//ik_max_word
                .endObject()
 
                .startObject("name")
                .field("type","string")
                .field("index","not_analyzed")
                .endObject()
                .endObject()
                .endObject();
        cib.addMapping(type, mapping);
        cib.execute().actionGet();
    }
}