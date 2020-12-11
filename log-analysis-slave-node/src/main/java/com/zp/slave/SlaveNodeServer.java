package com.zp.slave;

import com.zp.protobuf.MsgPOJO;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zp
 * @create 2020/12/9 14:20
 */
@Slf4j
public class SlaveNodeServer {
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

    public void start() {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
