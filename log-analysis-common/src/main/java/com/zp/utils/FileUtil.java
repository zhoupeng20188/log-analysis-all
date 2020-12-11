package com.zp.utils;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * @Author zp
 * @create 2020/12/9 16:45
 */
public class FileUtil {
    public static void write(File file, String s){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(s.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(File file, byte[] bytes){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(File file, ByteBuf byteBuf){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            byte[] req = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(req);
            fileOutputStream.write(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
