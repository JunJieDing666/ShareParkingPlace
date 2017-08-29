package com.jj.tidedemo.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/7/3.
 */
public class StreamUtil {

    /**
     *
     * @param is   流对象
     * @return  流转换成字符串      返回null代表异常
     */
    public static String stream2String(InputStream is) {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        byte [] buffer = new byte[1024];
        int len=-1;
        try {
            if (is!=null){
                while ((len=is.read(buffer))!=-1){
                    bos.write(buffer,0,len);
                }
                return bos.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "无法解析";
    }
}
