package com.zp.utils;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.entity.Server;
import com.zp.handler.NettyClientHandler;
import com.zp.protobuf.MsgPOJO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author zp
 * @create 2020/12/16 15:16
 */
@Slf4j
public class ThreadUtil {
    public static void startHeartbeatThread(String serverAddr,
                                            int serverPort) {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (!Election.stopHeartbeat) {
                            if (Server.masterChannel.isOpen()) {

                                MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                                        .setType(Consts.MSG_TYPE_HEARTBEAT)
                                        .setPort(Server.port)
                                        .setIsLeader(Election.isLeader);
                                Server.masterChannel.writeAndFlush(msgSend);
                                log.info("send heartbeat to master node：" + Server.masterChannel.remoteAddress());
                            } else {
                                // 重连老master
//                            final EventLoop eventLoop = Server.masterChannel.eventLoop();
//                            eventLoop.schedule(new Runnable() {
//                                @Override
//                                public void run() {
                                NettyUtil.startNettyClient(new NettyClientHandler(Server.port), serverAddr, serverPort);
//                                }
//                            }, 10, TimeUnit.SECONDS);
                            }
                        }
                    }
                }, 0, 10, TimeUnit.SECONDS);
    }
}
