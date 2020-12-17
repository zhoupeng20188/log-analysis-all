package com.zp;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.entity.Server;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.ChannelUtil;
import com.zp.utils.ElectionUtil;
import com.zp.utils.MsgUtil;
import com.zp.utils.ThreadUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @Author zp
 * @create 2020/9/1 18:12
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 定义一个channel组，管理所有channel
     */
//    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 开始选举
        ElectionUtil.startElection(ctx.channel(), Server.port);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgPOJO.Msg msgRsrv = (MsgPOJO.Msg) msg;
        // 消息内容
        long msgId = msgRsrv.getMsgId();
        // 消息内容
        String msgContent = msgRsrv.getContent();

        // 消息类型
        int msgType = msgRsrv.getType();
        // projectId
        String projectId = msgRsrv.getProjectId();
        boolean isLeader = msgRsrv.getIsLeader();
        boolean isOldLeader = msgRsrv.getIsOldLeader();
        log.info("客户端消息：" + msg);
        log.info("客户端地址：" + ctx.channel().remoteAddress());
        MsgPOJO.Msg.Builder builder = MsgPOJO.Msg.newBuilder();
        MsgPOJO.Msg.Builder msgSend = null;
        if (msgType == Consts.MSG_TYPE_HEARTBEAT) {
            if (isOldLeader) {
                // 停止向老master发送心跳
                Election.stopHeartbeat = true;
            }
            if (isLeader && Election.isLeader) {
                // 对方是leader时，代表自己是老master重连，给新master发送心跳
                Server.masterChannel = ctx.channel();
                SocketAddress socketAddress = ctx.channel().remoteAddress();
                String[] split = String.valueOf(socketAddress).replace("/", "").split(":");
                log.info("收到新master的连接，准备发送心跳到新master.");
                ThreadUtil.startHeartbeatThread(split[0], Integer.parseInt(split[1]));
                // 更新master状态
                Election.isLeader = false;
            } else {
                // 保存slave的地址
                ChannelUtil.storeSlaveAddress(ctx.channel(), msgRsrv.getPort());
                // 发送heartbeat的ack，包括所有slave server的地址
                MsgUtil.sendHeartbeatAck(ctx, msgRsrv.getPort());
            }
        } else if (msgType == Consts.MSG_TYPE_HEARTBEAT_ACK) {
            if (!isLeader) {
                Election.stopHeartbeat = true;
            } else {
                log.info("接收到最新的slave集群地址：" + msgContent);
                Server.otherSlaveAddrs = msgContent;
            }

        }
//        else if (msgType == Consts.MSG_TYPE_ACTIVE_SLAVE) {
//            log.info("slave：" + ctx.channel().remoteAddress() + " is connected");
////            channelGroup.add(ctx.channel());
//            Server.slaveClientChannels.add(ctx.channel());
//            // 保存slave的地址
//            ChannelUtil.storeSlaveAddress(ctx.channel(), msgRsrv.getPort());
//        }
        else if (msgType == Consts.MSG_TYPE_UNCOMMITED_ACK) {
            log.info("接收到slave：" + ctx.channel().remoteAddress() + "的ack");
            int ackCnt = 0;
            if (Server.ackMap.get(msgId) != null) {
                ackCnt = Server.ackMap.get(msgId) + 1;
            } else {
                ackCnt++;
            }
            Server.ackMap.put(msgId, ackCnt);
            if (ackCnt >= Server.slaveClientChannels.size() / 2 + 1) {
                // 修改本地状态为commited
                log.info("接收到超过半数的ack！成功写入！");
                MsgUtil.changeToCommited(projectId, msgId);
                // 发送commited请求给自己的slave
                for (Channel channel : Server.slaveClientChannels) {
                    msgSend = builder
                            .setMsgId(msgId)
                            .setProjectId(projectId)
                            .setType(Consts.MSG_TYPE_COMMITED)
                            .setContent(msgContent);
                    channel.writeAndFlush(msgSend);
                }
            }

        } else if (msgType == Consts.MSG_TYPE_CLIENT) {
            log.info("接收到客户端的请求！准备写入！");
            // 本地存储消息
            MsgUtil.storeMsg(msgContent, msgId, projectId);

            // 发送uncommited请求给自己的slave
            for (Channel channel : Server.slaveClientChannels) {
                msgSend = builder
                        .setMsgId(msgId)
                        .setType(Consts.MSG_TYPE_UNCOMMITED)
                        .setProjectId(projectId)
                        .setContent(msgContent);
                channel.writeAndFlush(msgSend);
            }
        }

    }
}
