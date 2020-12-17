package com.zp.utils;

import com.zp.entity.Server;
import com.zp.protobuf.MsgPOJO;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zp
 * @create 2020/12/16 15:46
 */
@Slf4j
public class NettyUtil {
    public static void startNettyClient(ChannelInboundHandlerAdapter nettyHandler,
                                        String serverAddr,
                                        int serverPort) {
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
                            socketChannel.pipeline().addLast(nettyHandler);
                        }
                    });
            // 客户端连接服务端
            ChannelFuture channelFuture = bootstrap.connect(serverAddr, serverPort);

            Server.masterChannel = channelFuture.channel();
            log.info("slave node-{} is started...", Server.id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
