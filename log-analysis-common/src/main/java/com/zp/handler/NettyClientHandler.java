package com.zp.handler;

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
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private int port;

    public NettyClientHandler() {
    }

    public NettyClientHandler(int port) {
        this.port = port;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 开始选举
        ElectionUtil.startElection(ctx.channel(), port);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgUtil.handleMsg(ctx, msg);
    }
}
