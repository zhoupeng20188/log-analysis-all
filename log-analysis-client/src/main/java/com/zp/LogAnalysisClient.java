package com.zp;


import com.zp.constrants.Consts;
import com.zp.handler.NettyClientHandler;
import com.zp.meta.MetaData;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.SnowFlakeUUID;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import sun.rmi.runtime.Log;

import java.io.PrintStream;
import java.util.UUID;
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

    private String projectId;

    private String serverAddr;


    public LogAnalysisClient(String projectId, String serverAddr) {
        this.projectId = projectId;
        this.serverAddr = serverAddr;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public static Logger getLog() {
        return log;
    }

    public void write(String s) {
        MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                .setProjectId(projectId)
                .setType(Consts.MSG_TYPE_CLIENT)
                .setMsgId(SnowFlakeUUID.nextId())
                .setContent(s);
        channel.writeAndFlush(msgSend);
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
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            socketChannel.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            log.info("log-analysis-client is started");
            // 客户端连接服务端
            ChannelFuture channelFuture;

            String[] split = serverAddr.split(",");
            for (String address : split) {
                String[] addr = address.split(":");
                // 尝试建立连接
                channelFuture = bootstrap.connect(addr[0], Integer.parseInt(addr[1]));
                if (MetaData.isConnected) {
                    // 连接建立成功时
                    channel = channelFuture.channel();
                    break;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startFixedRateThread() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                synchronized (LogStore.class) {
                    String content = LogStore.getContent();
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
