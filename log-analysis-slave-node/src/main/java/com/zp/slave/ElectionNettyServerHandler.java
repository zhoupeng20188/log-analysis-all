package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.protobuf.ElectionPOJO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElectionNettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SlaveNodeServer.slaveClientChannels.add(ctx.channel());
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
            log.info("更新master node 为" + ctx.channel().remoteAddress());
            SlaveNodeServer.masterChannel = ctx.channel();
        } else if (type == Consts.MSG_TYPE_HEARTBEAT) {
            log.info("接收到heart beat");
        }
    }
}
