package com.zp.handler;

import com.zp.meta.MetaData;
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
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    public NettyClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MetaData.isConnected = true;
        log.info(ctx.channel().remoteAddress() + " is connected");
    }
}
