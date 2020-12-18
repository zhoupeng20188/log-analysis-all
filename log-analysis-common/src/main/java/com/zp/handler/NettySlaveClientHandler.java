package com.zp.handler;

import com.zp.entity.Server;
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
public class NettySlaveClientHandler extends ChannelInboundHandlerAdapter {

    private int port;

    public NettySlaveClientHandler() {
    }

    public NettySlaveClientHandler(int port) {
        this.port = port;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(Server.slaveClientChannels.contains(ctx.channel())) {
            // 开始选举
            ElectionUtil.startElection(ctx.channel(), port);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgUtil.handleMsg(ctx, msg);
    }
}
