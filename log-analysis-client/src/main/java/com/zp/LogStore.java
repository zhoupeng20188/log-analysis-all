package com.zp;

/**
 * @Author zp
 * @create 2020/12/8 16:47
 */
public class LogStore {
    private static StringBuilder sb = new StringBuilder();
    public static synchronized String getContent(){
        return sb.toString();
    }

    public static synchronized void addContent(String s){
        sb.append(s);
    }

    public static synchronized void clear(){
        sb.delete(0, sb.length());
    }
}
