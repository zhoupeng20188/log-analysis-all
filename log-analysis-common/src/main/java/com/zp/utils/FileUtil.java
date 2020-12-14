package com.zp.utils;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * @Author zp
 * @create 2020/12/9 16:45
 */
public class FileUtil {
    public static void write(File file, String s) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(s.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(File file, byte[] bytes) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(File file, ByteBuf byteBuf) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            byte[] req = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(req);
            fileOutputStream.write(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read(File file, int start, int bytes) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[bytes];
            fileInputStream.read(buffer, start, bytes);
            return new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] convertFileToByteArray(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int bytes = (int) file.length();
            byte[] buffer = new byte[(int) file.length()];
            fileInputStream.read(buffer, 0, bytes);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ByteString convertFileToByteString(File file) {
        return ByteString.copyFrom(convertFileToByteArray(file));

    }
}
