package com.zp;

import com.zp.constrants.Consts;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.MsgUtil;
import com.zp.utils.StringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zp
 * @create 2020/9/1 18:12
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 定义一个channel组，管理所有channel
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * ackMap,key为msgId，value为ack次数
     */
    private static ConcurrentHashMap<Long, Integer> ackMap = new ConcurrentHashMap<>();

    private static List<String> slaveServerList = new ArrayList<>();

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
        log.info("客户端消息：" + msg);
        log.info("客户端地址：" + ctx.channel().remoteAddress());
        MsgPOJO.Msg.Builder builder = MsgPOJO.Msg.newBuilder();
        MsgPOJO.Msg.Builder msgSend = null;
        if (msgType == Consts.MSG_TYPE_HEARTBEAT) {
            // 发送heartbeat的ack，包括所有slave server的地址
            String remoteAddress = "";
            int port = msgRsrv.getPort();
            for (String s : slaveServerList) {
                if (!s.contains(String.valueOf(port))) {
                    // 返回不包含自己的其它slave的地址
                    remoteAddress += s + ",";
                }
            }
            if (!StringUtil.isEmpty(remoteAddress)) {
                remoteAddress = remoteAddress.substring(0, remoteAddress.length() - 1);
            }
            msgSend = builder
                    .setMsgId(msgId)
                    .setProjectId(projectId)
                    .setType(Consts.MSG_TYPE_HEARTBEAT_ACK)
                    .setContent(remoteAddress);
            ctx.channel().writeAndFlush(msgSend);
        } else if (msgType == Consts.MSG_TYPE_ACTIVE_SLAVE) {
            log.info("slave：" + ctx.channel().remoteAddress() + " is connected");
            channelGroup.add(ctx.channel());
            // 获取当前连接的客户端的ip
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = inetSocketAddress.getAddress().getHostAddress();
            int port = msgRsrv.getPort();
            String address = ip + ":" + port;
            slaveServerList.add(address);
        } else if (msgType == Consts.MSG_TYPE_UNCOMMITED_ACK) {
            log.info("接收到slave：" + ctx.channel().remoteAddress() + "的ack");
            int ackCnt = 0;
            if (ackMap.get(msgId) != null) {
                ackCnt = ackMap.get(msgId) + 1;
            } else {
                ackCnt++;
            }
            ackMap.put(msgId, ackCnt);
            if (ackCnt >= channelGroup.size() / 2 + 1) {
                // 修改本地状态为commited
                log.info("接收到超过半数的ack！成功写入！");
                MsgUtil.changeToCommited(projectId, msgId);
                // 发送commited请求给自己的slave
                for (Channel channel : channelGroup) {
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
            for (Channel channel : channelGroup) {
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
