package com.zp;

import com.zp.protobuf.MsgPOJO;
import com.zp.utils.MsgUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zp
 * @create 2020/12/9 14:20
 */
@Slf4j
public class MasterNodeServer {
    private int masterId;
    private int port;

    public MasterNodeServer(int masterId, int port) {
        this.masterId = masterId;
        this.port = port;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void start(){
        // 启动时加载index到内存中
        MsgUtil.initIndex();
        nettyStart();
    }

    public void nettyStart() {
        try {
        // 默认个数为cpu核心数*2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 默认个数为cpu核心数*2
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到的连接数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            socketChannel.pipeline().addLast(new ProtobufDecoder(MsgPOJO.Msg.getDefaultInstance()));
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            log.info("master node-" + masterId + " is ready...");
            // 启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
