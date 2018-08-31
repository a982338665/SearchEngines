package pers.li;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GETClientES {

    @SuppressWarnings("all")
    public static TransportClient getClient() throws UnknownHostException {
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
