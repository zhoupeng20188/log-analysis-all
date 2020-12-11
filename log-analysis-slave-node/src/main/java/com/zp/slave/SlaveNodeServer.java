package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.MsgUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author zp
 * @create 2020/12/9 14:20
 */
@Slf4j
public class SlaveNodeServer {
    private Channel channel;
    private int slaveId;
    private String serverAddr;
    private int serverPort;

    public SlaveNodeServer(int slaveId, String serverAddr, int serverPort) {
        this.slaveId = slaveId;
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start(){
        // 启动时加载index到内存中
        MsgUtil.initIndex();
        nettyStart();
        heartbeatThreadStart();
    }

    private void heartbeatThreadStart() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                                .setType(Consts.MSG_TYPE_HEARTBEAT);
                        channel.writeAndFlush(msgSend);
                        log.info("send heartbeat to master node：" + channel.remoteAddress());
                    }
                },10, 10, TimeUnit.SECONDS);
    }

    public void nettyStart() {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            socketChannel.pipeline().addLast(new ProtobufDecoder(MsgPOJO.Msg.getDefaultInstance()));
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            log.info("slave node-" + slaveId + " is started...");
            // 客户端连接服务端
            ChannelFuture channelFuture = bootstrap.connect(serverAddr, serverPort);

            channel = channelFuture.channel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
