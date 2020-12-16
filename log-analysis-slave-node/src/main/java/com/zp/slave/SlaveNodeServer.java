package com.zp.slave;

import com.zp.entity.Server;
import com.zp.handler.ElectionNettyServerHandler;
import com.zp.handler.NettyClientHandler;
import com.zp.protobuf.ElectionPOJO;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.MsgUtil;
import com.zp.utils.NettyUtil;
import com.zp.utils.ThreadUtil;
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
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
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
        // 启动时加载index到内存中
        MsgUtil.initIndex();
        NettyUtil.startNettyClient(new NettyClientHandler(Server.port), serverAddr, serverPort);
        ThreadUtil.startHeartbeatThread(serverAddr, serverPort);
        startNettyServer();
    }

    public void startNettyServer() {
        try {
            // 默认个数为cpu核心数*2
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            // 默认个数为cpu核心数*2
            EventLoopGroup workerGroup = new NioEventLoopGroup(1);

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到的连接数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //配置Protobuf编码工具ProtobufVarint32LengthFieldPrepender与ProtobufEncoder
                            socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            //配置Protobuf解码工具ProtobufVarint32FrameDecoder与ProtobufDecoder
                            socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            socketChannel.pipeline().addLast(new ProtobufDecoder(ElectionPOJO.Election.getDefaultInstance()));
                            socketChannel.pipeline().addLast(new ElectionNettyServerHandler());
                        }
                    });

            log.info("slave node server started");
            // 启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(Server.port).sync();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
