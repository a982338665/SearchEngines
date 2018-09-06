package pers.es.rebuild;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
    /**
     * 读取类型的所有文件并写入相应文件---写入备份
     * @param type
     * @return
     */
    private static boolean getTypeDataWrite(String type) throws UnknownHostException {

//        HttpGet httpGet=new HttpGet(PropertyUtil.getProperty("uri_first")+PropertyUtil.getProperty("index_name")+"/"+type+"/_search?scroll=1m ");
//        httpGet.addHeader("Content-Type", "application/json");
//        httpGet.
        InetSocketTransportAddress node=new InetSocketTransportAddress(
                InetAddress.getByName("118.31.236.154"),9302
        );

        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();

        TransportClient client=new PreBuiltTransportClient(settings);
        //此处可以添加多个客户端实例
        client.addTransportAddress(node);
        //指定一个index和type
        SearchRequestBuilder search = client.prepareSearch("skykingkong_es_api1").setTypes(type);
        //使用原生排序优化性能
        search.addSort("_doc", SortOrder.ASC);
        //设置每批读取的数据量
        search.setSize(100);
        //默认是查询所有
        search.setQuery(QueryBuilders.queryStringQuery("*:*"));
        //设置 search context 维护1分钟的有效期
        search.setScroll(TimeValue.timeValueMinutes(1));

        //获得首次的查询结果
        SearchResponse scrollResp=search.get();
        //打印命中数量
        System.out.println("命中总数量："+scrollResp.getHits().getTotalHits());
        //打印计数
        int count=1;
        do {
            System.out.println("第"+count+"次打印数据：");
            //读取结果集数据
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                System.out.println(hit.getSource())  ;
            }
            count++;
            //将scorllId循环传递
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(TimeValue.timeValueMinutes(1))
                    .execute().actionGet();

            //当searchHits的数组为空的时候结束循环，至此数据全部读取完毕
        } while(scrollResp.getHits().getHits().length != 0);
        return true;
    }
}
