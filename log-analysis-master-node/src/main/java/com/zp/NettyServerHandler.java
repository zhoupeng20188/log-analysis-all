package com.zp;

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
import java.util.Date;

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


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("服务端读取线程：" + Thread.currentThread().getName());
        MsgPOJO.Msg msgRsrv = (MsgPOJO.Msg) msg;
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
        if (msgType == Consts.MSG_TYPE_ACTIVE_SLAVE) {
            log.info("slave：" + ctx.channel().remoteAddress() + " is connected");
            channelGroup.add(ctx.channel());
        } else if (msgType == Consts.MSG_TYPE_ACK) {
            // 修改本地状态为commited
            // 发送commited请求给自己的slave
            for (Channel channel : channelGroup) {
                msgSend = builder
                        .setType(Consts.MSG_TYPE_COMMITED).setContent(msgContent);
                channel.writeAndFlush(msgSend);
            }
        } else if (msgType == Consts.MSG_TYPE_CLIENT) {
            // 顺序写文件
            File file = new File(projectId + ".log");
            FileUtil.write(file, msgContent);
            // 发送uncommited请求给自己的slave
            for (Channel channel : channelGroup) {
                msgSend = builder
                        .setType(Consts.MSG_TYPE_UNCOMMITED).setContent(msgContent);
                channel.writeAndFlush(msgSend);
            }
        }

    }
}
