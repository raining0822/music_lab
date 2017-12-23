package com.lava.music.util;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by mac on 2017/11/6.
 */
public class HttpUtil {

    public static String sendGetRequest(String url){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            StatusLine statusLine =  response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                entity.getContent();
                String content = EntityUtils.toString(entity);
                return content;
            }
            else{
                System.out.println("请求出现异常，Http状态码：" + statusCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPostRequest(){

    }
}
