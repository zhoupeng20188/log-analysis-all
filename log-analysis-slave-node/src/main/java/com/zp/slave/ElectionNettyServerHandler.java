package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.protobuf.ElectionPOJO;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.ChannelUtil;
import com.zp.utils.ElectionUtil;
import com.zp.utils.MsgUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
public class ElectionNettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelUtil.storeChannel(SlaveNodeServer.slaveClientChannels, SlaveNodeServer.slaveChannelMap, ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ElectionPOJO.Election election = (ElectionPOJO.Election) msg;
        // 消息类型
        int type = election.getType();
        if (type == Consts.MSG_TYPE_ELECTION) {
            // 选举轮次
            int electionId = election.getElectionId();
            // 消息index
            int index = election.getIndex();
            int term = election.getTerm();
            if (Election.id <= electionId
                    && Election.term <= term
                    && Election.index <= index
                    && !Election.isLeader) {
                ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                        .setType(Consts.MSG_TYPE_ELECTION_ACK)
                        .setTerm(Election.term)
                        .setElectionId(Election.id)
                        .setVoteResult(1);
                ctx.channel().writeAndFlush(msgSend);
            }

        } else if (type == Consts.MSG_TYPE_ELECTION_MASTER) {
            // 更新master信息
            ElectionUtil.handleTypeMaster(ctx.channel(), SlaveNodeServer.masterChannel, election.getTerm(), election.getIndex());

        } else if (type == Consts.MSG_TYPE_HEARTBEAT) {
            // 保存slave的地址
            ChannelUtil.storeSlaveAddress(ctx.channel(), SlaveNodeServer.slaveServerList, election.getPort());
            // 发送heartbeat的ack，包括所有slave server的地址
            MsgUtil.sendHeartbeatAck(ctx, SlaveNodeServer.slaveServerList, election.getPort());
        } else if (type == Consts.MSG_TYPE_HEARTBEAT_ACK) {
            log.info("接收到最新的slave集群地址：" + election.getContent());
            SlaveNodeServer.otherSlaveAddrs = election.getContent();
        }
    }
}
