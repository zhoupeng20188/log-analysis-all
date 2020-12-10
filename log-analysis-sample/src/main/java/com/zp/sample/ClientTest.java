package com.zp.sample;

import com.zp.LogAnalysisClient;

/**
 * @Author zp
 * @create 2020/12/9 15:20
 */
public class ClientTest {
    public static void main(String[] args) throws InterruptedException {
        LogAnalysisClient logAnalysisClient = new LogAnalysisClient("127.0.0.1", 9527);
        logAnalysisClient.start();
        System.out.println("client test ...");
        System.out.println("client test222 ...");
        System.out.println("client test333 ...");
        Thread.sleep(1500);
        System.out.println("client test444...");
        System.out.println("client test555...");
        System.out.println("client test666...");
    }
}
