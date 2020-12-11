package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.FileUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @Author zp
 * @create 2020/9/1 18:12
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                .setType(Consts.MSG_TYPE_ACTIVE_SLAVE);
        ctx.channel().writeAndFlush(msgSend);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgPOJO.Msg msgRsrv = (MsgPOJO.Msg) msg;
        // 消息内容
        String msgContent = msgRsrv.getContent();
        // 消息类型
        int msgType = msgRsrv.getType();
        // projectId
        String projectId = msgRsrv.getProjectId();
        log.info("接收到master node消息：" + msg);
        log.info("master node地址：" + ctx.channel().remoteAddress());
        if (msgType == Consts.MSG_TYPE_UNCOMMITED) {
            // 顺序写文件
            File file = new File(projectId + ".log");
            FileUtil.write(file, msgContent);
            // 给master发送ACK消息
            MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                    .setType(Consts.MSG_TYPE_ACK).setContent(msgContent);
            ctx.channel().writeAndFlush(msgSend);
        }

    }

}
