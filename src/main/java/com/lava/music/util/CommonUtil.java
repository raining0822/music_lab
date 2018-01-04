package com.lava.music.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mac on 2017/8/28.
 */
public class CommonUtil {

    public static String getPicUrlByPicId(String pId){
        StringBuilder stringBuilder = new StringBuilder("http://img1.lavaradio.com/");
        if(pId.startsWith("-")){
            pId = pId.replace("-","");
            stringBuilder.append(pId.substring(0,3))
                    .append("/")
                    .append(pId.substring(3,6))
                    .append("/")
                    .append(pId).append("X").append(".jpg");
        }else{
            stringBuilder.append(pId.substring(0,3))
                    .append("/")
                    .append(pId.substring(3,6))
                    .append("/")
                    .append(pId).append(".jpg");
        }
        return stringBuilder.toString();
    }

    /**
     * MD5加密字符串
     * @param str
     * @return
     */
    public static String MD5(String str){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            /*BASE64Encoder base64Encoder = new BASE64Encoder();
            String result = base64Encoder.encode(md5.digest(str.getBytes("UTF-8")));*/
            md5.update(str.getBytes("UTF-8"));
            return new BigInteger(1, md5.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String randomStr(Integer length){
        String a = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] rands = new char[length];
        for (int i = 0; i < rands.length; i++)
        {
            int rand = (int) (Math.random() * a.length());
            rands[i] = a.charAt(rand);
        }
        return new String(rands);
    }


}
