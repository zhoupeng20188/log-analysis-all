package com.zp;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author zp
 * @create 2020/9/1 18:18
 */
@Slf4j
public class LogAnalysisClient {

    private Channel channel;

    private String serverAddr;

    private int serverPort;

    public LogAnalysisClient(String serverAddr, int serverPort) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
    }

    public void write(String s) {
        channel.writeAndFlush(s);
    }

    public void start() {
        // 启动netty
        startNetty();

        // log初始设置
        initLog();

        // 启动定时上报线程
        startFixedRateThread();
    }

    private void startNetty() {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("decoder", new StringDecoder());
                            socketChannel.pipeline().addLast("encoder", new StringEncoder());
                        }
                    });
            log.info("log-analysis-client is started...");
            // 客户端连接服务端
            ChannelFuture channelFuture = bootstrap.connect(serverAddr, serverPort);

            channel = channelFuture.channel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startFixedRateThread() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                synchronized (LogStore.class){
                    String content = LogStore.getContent();
//                System.out.println("线程执行。。content="+content + "end..");
                    if (content != null && !content.equals("")) {
                        // 日志不为空时，上报
                        write(LogStore.getContent());
                    }
                    LogStore.clear();
                }

            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void initLog() {
        //备份原有输出流
        PrintStream old = System.out;
        ConsoleStream newStream = new ConsoleStream(old);
        //设置新的输出流
        System.setOut(new PrintStream(newStream));
    }


}
