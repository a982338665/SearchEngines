package pers.li;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class TestSelectOne {

    /**
     * 查询单条数据
     * @throws UnknownHostException
     */
    @Test
    public  void test1() throws UnknownHostException {
        TransportClient client = getClient();
        GetResponse getFields1 = client.prepareGet("lib","user","1").get();
        GetResponse getFields2= client.prepareGet("index1","blog","10").execute().actionGet();
        System.out.println(getFields1.getSourceAsString());
        System.out.println(getFields2.getSourceAsString());

    }
    /**
     * 添加文档：
     * 先创建mapping：
     * PUT /index1
     {
     "settings":{
     "number_of_shards": 3,
     "number_of_replicas": 0
     },
     "mappings":{
     "blog":{
     "properties": {
     "id":{"type": "long"},
     "title":{"type": "text","analyzer": "ik_max_word"},
     "content":{"type": "text","analyzer": "ik_max_word"},
     "url":{"type": "text"},
     "postdate":{"type": "date"}
     }
     }
     }
     }
     ***********************
     *
     * @throws UnknownHostException
     */
    @Test
    public  void test2() throws IOException {
        TransportClient client = getClient();
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                //文档内容id
                .field("id", "1")
                .field("title", "java设计模式标题")
                .field("content","java编程思想...")
                .field("postdate", "2018-02-05")
                .field("url", "www.baidu.com")
                .endObject();
//        IndexResponse indexResponse = client.prepareIndex("index1","blog") //id不写默认为null
        IndexResponse indexResponse = client.prepareIndex("index1","blog","10")
                .setSource(xContentBuilder)
                .get();
        System.out.println(indexResponse.status());

    }

    /**
     * 删除文档
     * @throws IOException
     */
    @Test
    public  void test3() throws IOException {
        TransportClient client = getClient();
        DeleteResponse response = client.prepareDelete("index1","blog","10").get();
        System.out.println(response.status());
        System.out.println(response.getResult());

    }
    /**
     * 更新文档
     * @throws IOException
     */
    @Test
    public  void test4() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = getClient();
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject();

        builder.field("title", "修改后的设计模式标题");
        builder.endObject();

        UpdateRequest update = new UpdateRequest("index1","blog","10");
        update.doc(builder);
        UpdateResponse response = client.update(update).get();
        System.out.println(response.status());

    }
    /**
     * 更新文档 - upsert ：若不存在，则新增，存在则修改
     * @throws IOException
     */
    @Test
    public  void test5() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = getClient();
        //添加文档
        IndexRequest source = new IndexRequest("index1", "blog", "8").source(
                XContentFactory.jsonBuilder()
                        .startObject()
                        //文档内容id
                        .field("id", "2")
                        .field("title", "java设计模式标题-工厂模式")
                        .field("content","java编程思想...")
                        .field("postdate", "2018-02-06")
                        .field("url", "www.baidu.com")
                        .endObject()
        );
        //修改文档：由于id=8的并不存在，故此会执行新增即上面的内容新增
        UpdateRequest request=new UpdateRequest("index1", "blog", "8")
                .doc(
                        XContentFactory.jsonBuilder()
                                .startObject()
                                .field("title", "修改后的设计模式标题")
                                .endObject()
                ).upsert(source);
        UpdateResponse response = client.update(request).get();
        System.out.println(response.status());
    }
    /**
     * mget批量查询
     * @throws IOException
     */
    @Test
    public  void test6() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = getClient();
        MultiGetResponse responses=client.prepareMultiGet()
                .add("index1","blog","8","10")
                .add("lib","user","1").get();
        for (MultiGetItemResponse itme:responses
             ) {
            GetResponse gr=itme.getResponse();
            if(gr!=null&&gr.isExists()){
                System.out.println(gr.getSourceAsString());
            }
        }
    }
    /**
     * bulk批量增删改
     * @throws IOException
     */
    @Test
    public  void test7() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = getClient();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        bulkRequestBuilder.add(client.prepareIndex("lib2","books","8")
            .setSource(
                    XContentFactory.jsonBuilder()
                            .startObject()
                            .field("title", "修改后的设计模式标题")
                            .field("content", "修改后的设计模式...")
                            .endObject()
            )
        );
        bulkRequestBuilder.add(client.prepareIndex("lib2","books","9")
            .setSource(
                    XContentFactory.jsonBuilder()
                            .startObject()
                            .field("title", "修改后的设计模式标题2")
                            .field("content", "修改后的设计模式...2")
                            .endObject()
            )
        );
        BulkResponse bulkItemResponses = bulkRequestBuilder.get();
        System.out.println(bulkItemResponses.status());
    }

    @SuppressWarnings("all")
    private TransportClient getClient() throws UnknownHostException {
        InetSocketTransportAddress node=new InetSocketTransportAddress(
                InetAddress.getByName("192.168.150.135"),9300
        );
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        TransportClient client=new PreBuiltTransportClient(settings);
        //此处可以添加多个客户端实例
        client.addTransportAddress(node);
        return client;
    }
}
