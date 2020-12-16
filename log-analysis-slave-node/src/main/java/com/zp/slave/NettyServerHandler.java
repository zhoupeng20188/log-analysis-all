package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.meta.MetaData;
import com.zp.protobuf.ElectionPOJO;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.MetaDataUtil;
import com.zp.utils.MsgUtil;
import com.zp.utils.RandomUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
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
        log.info("master："+ ctx.channel().remoteAddress() + " is down");
        int sleepTime = RandomUtil.electRandom();
        log.info("preparing to elect new leader,sleepTIme=" + sleepTime + "ms");
        Thread.sleep(sleepTime);
        // 选举轮次+1
        Election.id++;
        Election.port = port;
        // 先投自己一票
        Election.voteCnt++;
        // 发送投票请求给其它slave
        String[] slaveAddr = SlaveNodeServer.otherSlaveAddrs.split(",");
        for (String s : slaveAddr) {
            String[] split = s.split(":");
            String ip = split[0];
            String port = split[1];
            startNettyClient(ip, port);
        }
    }

    public void startNettyClient(String ip, String port) {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //配置Protobuf编码工具ProtobufVarint32LengthFieldPrepender与ProtobufEncoder
                            socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            //配置Protobuf解码工具ProtobufVarint32FrameDecoder与ProtobufDecoder
                            socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            socketChannel.pipeline().addLast(new ProtobufDecoder(ElectionPOJO.Election.getDefaultInstance()));
                            socketChannel.pipeline().addLast(new ElectionNettyClientHandler());
                        }
                    });
            // 客户端连接服务端
            ChannelFuture channelFuture = bootstrap.connect(ip, Integer.parseInt(port));


        } catch (Exception e) {
            e.printStackTrace();
        }
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
            SlaveNodeServer.otherSlaveAddrs = msgContent;

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
