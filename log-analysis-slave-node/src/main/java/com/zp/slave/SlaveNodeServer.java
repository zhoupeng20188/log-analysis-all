package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.protobuf.ElectionPOJO;
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
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author zp
 * @create 2020/12/9 14:20
 */
@Slf4j
public class SlaveNodeServer {
    public static volatile Channel masterChannel;
    public static volatile String otherSlaveAddrs;
    public static volatile Set<Channel> slaveClientChannels = new HashSet<>();
    public static volatile HashMap<String, Channel> slaveChannelMap = new HashMap<>();
    public static volatile Set<String> slaveServerList = new HashSet<>();
    private int slaveId;
    private String serverAddr;
    private int serverPort;
    private int port;

    public SlaveNodeServer(int slaveId, String serverAddr, int serverPort, int port) {
        this.slaveId = slaveId;
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
        startNettyClient();
        startHeartbeatThread();
        startNettyServer();
    }

    private void startHeartbeatThread() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (!Election.isLeader) {
                            MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                                    .setType(Consts.MSG_TYPE_HEARTBEAT)
                                    .setPort(port);
                            masterChannel.writeAndFlush(msgSend);
                            log.info("send heartbeat to master node：" + masterChannel.remoteAddress());
                        }
                    }
                }, 10, 10, TimeUnit.SECONDS);
    }

    public void startNettyClient() {
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
                            socketChannel.pipeline().addLast(new NettyServerHandler(port));
                        }
                    });
            log.info("slave node-" + slaveId + " is started...");
            // 客户端连接服务端
            ChannelFuture channelFuture = bootstrap.connect(serverAddr, serverPort);

            masterChannel = channelFuture.channel();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
