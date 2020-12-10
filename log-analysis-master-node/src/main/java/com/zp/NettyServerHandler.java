package com.zp;

import com.zp.constrants.Consts;
import com.zp.utils.FileUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组，管理所有channel
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 当连接建立时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush(" [客户端]"+ctx.channel().remoteAddress()+" 加入聊天");
        channelGroup.add(ctx.channel());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("服务端读取线程："+Thread.currentThread().getName());

        log.info("客户端消息："+msg);
        log.info("客户端地址："+ctx.channel().remoteAddress());
        if(msg.equals(Consts.MSG_ACTIVE_SLAVE)){
            log.info("slave："+ ctx.channel().remoteAddress() + " is connected");
            channelGroup.add(ctx.channel());
        }
        else if(msg.startsWith(Consts.MSG_HEAD_ACK)){
            // 修改本地状态为commited
            // 发送commited请求给自己的slave
            for (Channel channel : channelGroup) {
                channel.writeAndFlush(Consts.MSG_HEAD_COMMITED + msg);
            }
        } else {
            // 顺序写文件
            File file = new File("project1.log");
            FileUtil.write(file, msg);
            // 发送uncommited请求给自己的slave
            for (Channel channel : channelGroup) {
                channel.writeAndFlush(Consts.MSG_HEAD_UNCOMMITED + msg);
            }
        }

    }
}
