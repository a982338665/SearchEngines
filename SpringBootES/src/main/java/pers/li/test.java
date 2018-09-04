package pers.li;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class test {

    public static void main(String[] args) throws UnknownHostException {
        TransportClient client = getClient();
        //指定要备份索引名称
        String index="test-20180824";




    }

    @SuppressWarnings("all")
    public static TransportClient getClient() throws UnknownHostException {
        InetSocketTransportAddress node=new InetSocketTransportAddress(
                InetAddress.getByName("118.31.236.154"),9302
        );
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        TransportClient client=new PreBuiltTransportClient(settings);
        //此处可以添加多个客户端实例
        client.addTransportAddress(node);
        return client;
    }


}
