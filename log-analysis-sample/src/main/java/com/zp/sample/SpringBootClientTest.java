package com.zp.sample;

import com.zp.LogAnalysisClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author zp
 * @create 2020/12/9 16:13
 */
@SpringBootApplication
public class SpringBootClientTest {
    public static void main(String[] args) {
        LogAnalysisClient logAnalysisClient = new LogAnalysisClient("test01",
                "127.0.0.1:9527,127.0.0.1:19527,127.0.0.1:29527");
        logAnalysisClient.start();
        SpringApplication.run(SpringBootClientTest.class, args);
    }
}
