package com.zp.utils;

/**
 * @Author zp
 * @create 2020/12/15 17:09
 */
public class ByteUtil {
    /**
     * 将一个byte[]追加到一个byte[]的后面
     * @param from
     * @param to
     * @return
     */
    public static byte[] appendToTail(byte[] from, byte[] to) {
        int fromLength = from.length;
        if(to == null){
            return from;
        }
        int toLength = to.length;
        byte[] bytes = new byte[fromLength + toLength];
        for (int i = 0; i < from.length; i++) {
            bytes[i] = from[i];
        }
        for (int i = 0; i < to.length; i++) {
            bytes[i + fromLength] = from[i];
        }
        return bytes;
    }

    /**
     * 按字节数读取byte[]
     * @param bytes
     * @param length
     * @return
     */
    public static byte[] readBytes(byte[] bytes, int length){
        byte[] read = new byte[length];
        for (int i = 0; i < length; i++) {
            read[i] = bytes[i];
        }
        return read;
    }
}
