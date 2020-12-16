package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.entity.Server;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.ElectionUtil;
import com.zp.utils.MsgUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zp
 * @create 2020/9/1 18:12
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private int port;

    public NettyServerHandler() {
    }

    public NettyServerHandler(int port) {
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                .setType(Consts.MSG_TYPE_ACTIVE_SLAVE)
                .setPort(port);
        ctx.channel().writeAndFlush(msgSend);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 开始选举
        ElectionUtil.startElection(ctx.channel(), port);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgPOJO.Msg msgRsrv = (MsgPOJO.Msg) msg;
        // 消息id
        long msgId = msgRsrv.getMsgId();
        // 消息内容
        String msgContent = msgRsrv.getContent();
        // 消息类型
        int msgType = msgRsrv.getType();
        // projectId
        String projectId = msgRsrv.getProjectId();
        log.info("接收到master node消息：" + msg);
        log.info("master node地址：" + ctx.channel().remoteAddress());
        if (msgType == Consts.MSG_TYPE_HEARTBEAT_ACK) {
            log.info("接收到最新的slave集群地址：" + msgContent);
            Server.otherSlaveAddrs = msgContent;

        } else if (msgType == Consts.MSG_TYPE_UNCOMMITED) {
            log.info("接收到master的uncommited请求！");
            // 本地存储消息
            MsgUtil.storeMsg(msgContent, msgId, projectId);
            // 给master发送ACK消息
            MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                    .setMsgId(msgId)
                    .setProjectId(projectId)
                    .setType(Consts.MSG_TYPE_UNCOMMITED_ACK)
                    .setContent(msgContent);
            ctx.channel().writeAndFlush(msgSend);
        } else if (msgType == Consts.MSG_TYPE_COMMITED) {
            log.info("接收到master的commited请求！");
            // 修改本地状态为commited
            MsgUtil.changeToCommited(projectId, msgId);
        }
    }
}
