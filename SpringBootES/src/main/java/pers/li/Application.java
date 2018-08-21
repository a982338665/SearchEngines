package pers.li;


import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

    @Autowired
    private TransportClient client;

    @GetMapping("/")
    public  String  index(){
        return "index";
    }

    /**
     * 查询指定索引，类型下的id=？的文档数据
     * localhost:8080/people/man/1
     * @param index
     * @param type
     * @param id
     * @return
     */
    @GetMapping("/get/{index}/{type}/{id}")
    @ResponseBody
    public ResponseEntity get(@PathVariable("index") String index,@PathVariable("type") String type,@PathVariable("id") String id){
        if(id.isEmpty()){
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        GetResponse getFields = this.client.prepareGet(index,type,id).get();
        if(!getFields.isExists()){
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(getFields.getSource(),HttpStatus.OK);
    }

    /**
     * 新增
     * @param index
     * @param type
     * @param id
     * @param name
     * @param age
     * @param country
     * @param date
     * @return
     */
    @PostMapping("/add/{index}/{type}/{id}")
    @ResponseBody
    public ResponseEntity add(
            @PathVariable("index") String index,
            @PathVariable("type") String type,
//            @PathVariable("id") String id,
            @RequestParam(name = "name")String name,
            @RequestParam(name = "age")String age,
            @RequestParam(name = "country")String country,
            @RequestParam(name = "date")
                    @DateTimeFormat(pattern = "yyyy-mm-dd HH:mm:ss") Date date

    ){
        try {
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("name", name)
                    .field("age", age)
                    .field("country", country)
                    .field("date", date.getTime())
                    .endObject();
            IndexResponse indexResponse = this.client.prepareIndex(index, type)
                    .setSource(xContentBuilder)
                    .get();
            return new ResponseEntity(indexResponse.getId(),HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 删除接口
    @DeleteMapping("/del/{index}/{type}/{id}")
    @ResponseBody
    public ResponseEntity delete(
            @PathVariable("index") String index,
            @PathVariable("type") String type,
            @PathVariable("id") String id
    ) {
        DeleteResponse response = client.prepareDelete(index,type, id).get();
        return new ResponseEntity(response.getResult().toString(), HttpStatus.OK);
    }


    // 更新接口
    @PutMapping("/update/{index}/{type}")
    @ResponseBody
    public ResponseEntity update(
            @PathVariable("index") String index,
            @PathVariable("type") String type,
            @RequestParam(name = "id") String id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "age", required = false) String age,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {

        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().startObject();

            if (name != null) {
                builder.field("name", name);
            }
            if (age != null) {
                builder.field("age", age);
            }
            if (country != null) {
                builder.field("country", country);
            }
            if (date != null) {
                builder.field("publish_date", date.getTime());
            }
            builder.endObject();

            UpdateRequest update = new UpdateRequest(index, type, id);
            update.doc(builder);
            UpdateResponse response = client.update(update).get();
            return new ResponseEntity(response.getResult().toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 复合查询
    @PostMapping("query/{index}/{type}")
    @ResponseBody
    public ResponseEntity query(
            @PathVariable("index") String index,
            @PathVariable("type") String type,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "age", required = false) Integer age,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (name != null) {
            boolQuery.must(QueryBuilders.matchQuery("name", name));
        }

        if (country != null) {
            boolQuery.must(QueryBuilders.matchQuery("country", country));
        }

        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age").from(age);
        if (age != null && age > 0) {
            rangeQuery.to(age);
        }
        boolQuery.filter(rangeQuery);

        SearchRequestBuilder builder = client.prepareSearch(index).setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQuery)
                .setFrom(0)
                .setSize(10);

        SearchResponse response = builder.get();

        List result = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : response.getHits()) {
            result.add(hit.getSourceAsMap());
        }

        return new ResponseEntity(result, HttpStatus.OK);

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }


}
