package com.jj.tidedemo.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/7/15.
 */
public class Md5Utils {
    public static String encode(String psd) {
        psd = psd + "mobilesafe";
        try {
            //1.指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //2.将要加密的数据转换成byte数组，并进行随机哈希过程，长度为16
            byte[] psdB = digest.digest(psd.getBytes());
            //3.循环遍历该数组，使其生成32位字符串，写法固定，并将其拼接起来
            StringBuilder sb = new StringBuilder();
            for (byte b : psdB) {
                //各8位，拼接成16位，组成两个字符
                int i = b & 0xff;
                //将i转换成十六进制
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
