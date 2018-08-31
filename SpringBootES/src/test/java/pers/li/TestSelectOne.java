package pers.li;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TestSelectOne {

    /**
     * 查询单条数据
     * @throws UnknownHostException
     */
    @Test
    public  void test1() throws UnknownHostException {
        TransportClient client = GETClientES.getClient();
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
        TransportClient client = GETClientES.getClient();
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
        TransportClient client = GETClientES.getClient();
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
        TransportClient client = GETClientES.getClient();
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
        TransportClient client = GETClientES.getClient();
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
        TransportClient client = GETClientES.getClient();
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
        TransportClient client = GETClientES.getClient();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        bulkRequestBuilder.add(client.prepareIndex("lib3","books","21")
            .setSource(
                    XContentFactory.jsonBuilder()
                            .startObject()
                            .field("title", "修改后的设计模式标题")
                            .field("content", "修改后的设计模式...")
                            .endObject()
            )
        );
        bulkRequestBuilder.add(client.prepareIndex("lib3","books","20")
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
        if(bulkItemResponses.hasFailures()){
            System.out.println("failed！");
        }
    }
    /**
     * 查询删除 --删除标题中含有工厂的
     * @throws IOException
     */
    @Test
    public  void test8() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = GETClientES.getClient();
        BulkByScrollResponse bulkByScrollResponse = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("title", "设计模式"))
                .source("index1")
                .get();
        //返回删除文档的个数
        long deleted = bulkByScrollResponse.getDeleted();
        System.out.println(deleted);
    }
    /**
     * 查询所有 match_all
     * @throws IOException
     */
    @Test
    public  void test9() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = GETClientES.getClient();
        QueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        SearchResponse qr = client.prepareSearch("lib3")
                .setQuery(matchAllQueryBuilder)
                //默认查询10个，此处指定为34个
                .setSize(3).get();
        SearchHits hits=qr.getHits();
        for (SearchHit hit:hits
             ) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key:sourceAsMap.keySet()
                 ) {
                System.out.println(key+"="+ sourceAsMap.get(key));
            }
        }
    }
    /**
     * 查询 match_query
     * @throws IOException
     */
    @Test
    @SuppressWarnings("all")
    public  void test10() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = GETClientES.getClient();
        QueryBuilder matchAllQueryBuilder = QueryBuilders.matchQuery("interest","唱歌");
        SearchResponse qr = client.prepareSearch("lib2")
                .setQuery(matchAllQueryBuilder)
                //默认查询10个，此处指定为34个
                .setSize(3).get();
        SearchHits hits=qr.getHits();
        for (SearchHit hit:hits
             ) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key:sourceAsMap.keySet()
                 ) {
                System.out.println(key+"="+ sourceAsMap.get(key));
            }
        }
    }
    /**
     * 查询 multimatchQuery --可以指定多个字段查询
     * @throws IOException
     */
    @Test
    @SuppressWarnings("all")
    public  void test11() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = GETClientES.getClient();
        QueryBuilder matchAllQueryBuilder = QueryBuilders
                //第一个参数为搜索文本，之后的为字段名称
                .multiMatchQuery("i唱歌","interest","address");
        SearchResponse qr = client.prepareSearch("lib2")
                .setQuery(matchAllQueryBuilder)
                //默认查询10个，此处指定为34个
                .setSize(3).get();
        SearchHits hits=qr.getHits();
        for (SearchHit hit:hits
             ) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key:sourceAsMap.keySet()
                 ) {
                System.out.println(key+"="+ sourceAsMap.get(key));
            }
        }
    }
    /**
     * 查询 term --
     * @throws IOException
     */
    @Test
    @SuppressWarnings("all")
    public  void test12() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = GETClientES.getClient();
        QueryBuilder matchAllQueryBuilder = QueryBuilders
                .termQuery("interest","唱歌");
        SearchResponse qr = client.prepareSearch("lib2")
                .setQuery(matchAllQueryBuilder)
                //默认查询10个，此处指定为34个
                .setSize(3).get();
        SearchHits hits=qr.getHits();
        for (SearchHit hit:hits
             ) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key:sourceAsMap.keySet()
                 ) {
                System.out.println(key+"="+ sourceAsMap.get(key));
            }
        }
    }
    /**
     * 查询 terms--
     * @throws IOException
     */
    @Test
    @SuppressWarnings("all")
    public  void test13() throws IOException, ExecutionException, InterruptedException {
        TransportClient client =GETClientES.getClient();
        QueryBuilder matchAllQueryBuilder = QueryBuilders
                //第一个参数为搜索文本，之后的为字段名称
                .termsQuery("interest","唱歌","旅游");
        SearchResponse qr = client.prepareSearch("lib2")
                .setQuery(matchAllQueryBuilder)
                //默认查询10个，此处指定为34个
                .setSize(3).get();
        SearchHits hits=qr.getHits();
        for (SearchHit hit:hits
             ) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key:sourceAsMap.keySet()
                 ) {
                System.out.println(key+"="+ sourceAsMap.get(key));
            }
        }
    }

    /**
     * 查询 range
     * @throws IOException
     */
    @Test
    @SuppressWarnings("all")
    public  void test14() throws IOException, ExecutionException, InterruptedException {
        TransportClient client =GETClientES.getClient();
        //查询 range
        //QueryBuilder matchAllQueryBuilder = QueryBuilders.rangeQuery("birthday").from("1992-08-02").to("1995-05-05").format("yyyy-mm-dd");
        //查询  prefix
        //QueryBuilder matchAllQueryBuilder = QueryBuilders.prefixQuery("name","zhao");
        //查询 wildcard --模糊查询-通配符
        //QueryBuilder matchAllQueryBuilder = QueryBuilders.wildcardQuery("name","zhao");
        //查询 fuzzy --模糊查询-写错也可以匹配
        //QueryBuilder matchAllQueryBuilder = QueryBuilders.fuzzyQuery("name","zhao");
        //查询 type 按类型查询，查询该类型下的所有数据
        //QueryBuilder matchAllQueryBuilder = QueryBuilders.typeQuery("blog");
        //查询 ids
        //QueryBuilder matchAllQueryBuilder = QueryBuilders.idsQuery("1","2","3");
        //查询 commonTermsQuery
        //QueryBuilder matchAllQueryBuilder = QueryBuilders.commonTermsQuery("name","lanzhou");
        //查询 query_string   --全文检索含有lanzhou 并且 不含有呵呵哒的数据 精确查询
        //QueryBuilder matchAllQueryBuilder = QueryBuilders.queryStringQuery("+lanzhou -呵呵哒");
        //查询 query_string   --全文检索含有lanzhou 或者 不含有呵呵哒的数据 精确查询
        QueryBuilder matchAllQueryBuilder = QueryBuilders.simpleQueryStringQuery("+lanzhou -呵呵哒");
        SearchResponse qr = client.prepareSearch("lib2")
                .setQuery(matchAllQueryBuilder)
                //默认查询10个，此处指定为34个
                .setSize(3).get();
        SearchHits hits=qr.getHits();
        for (SearchHit hit:hits
             ) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key:sourceAsMap.keySet()
                 ) {
                System.out.println(key+"="+ sourceAsMap.get(key));
            }
        }
    }
    /**
     * 聚合查询 --max，Min avg sum cardinality
     * @throws IOException
     */
    @Test
    @SuppressWarnings("all")
    public  void test15() throws IOException, ExecutionException, InterruptedException {
        TransportClient client =GETClientES.getClient();
        //最大值
        //AggregationBuilder field = AggregationBuilders.max("aggMax").field("age");
        //最小值
        AggregationBuilder field = AggregationBuilders.min("aggMax").field("age");
        SearchResponse qr = client.prepareSearch("lib2").addAggregation(field).get();
        //最大值
        //Max max = qr.getAggregations().get("aggMax");
        //最小值
        Min max = qr.getAggregations().get("aggMax");
        double value = max.getValue();
        System.out.println(value);
    }
    /**
     * 组合查询 boolQuery  constantScoreQuery
     * @throws IOException
     */
    @Test
    @SuppressWarnings("all")
    public  void test16() throws IOException, ExecutionException, InterruptedException {
        TransportClient client =GETClientES.getClient();
//        QueryBuilder matchAllQueryBuilder = QueryBuilders.boolQuery()
//                .must(QueryBuilders.matchQuery("interest","swimming"))
//                .mustNot(QueryBuilders.matchQuery("interest","旅游"))
//                .should(QueryBuilders.matchQuery("address","北京"))
//                .filter( QueryBuilders.rangeQuery("birthday").from("1992-08-02").to("1995-05-05").format("yyyy-mm-dd"));
        ConstantScoreQueryBuilder matchAllQueryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("name", "liulong"));
        SearchResponse qr = client.prepareSearch("lib2")
                .setQuery(matchAllQueryBuilder)
                //默认查询10个，此处指定为34个
                .setSize(3).get();
        SearchHits hits=qr.getHits();
        for (SearchHit hit:hits
                ) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key:sourceAsMap.keySet()
                    ) {
                System.out.println(key+"="+ sourceAsMap.get(key));
            }
        }

    }





}
