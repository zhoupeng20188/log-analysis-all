package com.zp.utils;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.entity.Server;
import com.zp.handler.NettySlaveClientHandler;
import com.zp.protobuf.MsgPOJO;
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
                                log.debug("send heartbeat to master node：{}", Server.masterChannel.remoteAddress());
                            } else {
                                NettyUtil.startNettyClient(new NettySlaveClientHandler(Server.port), serverAddr, serverPort);
                            }
                        }
                    }
                }, 0, 10, TimeUnit.SECONDS);
    }
}
