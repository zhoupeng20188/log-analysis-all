package com.zp;

import com.zp.constrants.Consts;
import com.zp.utils.FileUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @Author zp
 * @create 2020/9/1 18:12
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("服务端读取线程："+Thread.currentThread().getName());

        log.info("客户端消息："+msg);
        log.info("客户端地址："+ctx.channel().remoteAddress());
        if(msg.startsWith(Consts.MSG_HEAD_ACK)){
            // 修改本地状态为commited
            // 发送commited请求给自己的slave
            ctx.writeAndFlush(Consts.MSG_HEAD_COMMITED + msg);
        } else {
            // 顺序写文件
            File file = new File("project1.log");
            FileUtil.write(file, msg);
            // 发送uncommited请求给自己的slave
            ctx.writeAndFlush(Consts.MSG_HEAD_UNCOMMITED + msg);
        }

    }
}
