package pers.es.rebuild;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

/**
 * @author:luofeng
 * @createTime : 2018/9/20 16:32
 */
public class AuthInfo {

    /**
     * nginx 基础认证信息
     * @return
     */
    @SuppressWarnings("all")
    public static String GETBase64Auth(){
        String encoding = null;
        try {
//          encoding = DatatypeConverter.printBase64Binary("username:password".getBytes("UTF-8"));
            encoding = DatatypeConverter.printBase64Binary((
                    PropertyUtil.getProperty("username") +
                    ":" +
                    PropertyUtil.getProperty("pwd")
            ).getBytes("UTF-8"));
            encoding="Basic "+encoding;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoding;
    };

    public static void main(String[] args) {
        System.out.println(GETBase64Auth());
    }
}
