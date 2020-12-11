package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.protobuf.ElectionPOJO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElectionNettyHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                .setType(Consts.MSG_TYPE_ELECTION)
                .setTerm(Election.term)
                .setElectionId(Election.id);
        ctx.channel().writeAndFlush(msgSend);
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
            if (Election.id <= electionId && Election.index <= index) {
                ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                        .setType(Consts.MSG_TYPE_ELECTION_ACK)
                        .setTerm(Election.term)
                        .setElectionId(Election.id)
                        .setVoteResult(1);
                ctx.channel().writeAndFlush(msgSend);
            }

        } else if (type == Consts.MSG_TYPE_ELECTION_ACK) {
            Election.voteCnt++;
            log.info("接收到salve：" + ctx.channel().remoteAddress() + "的投票结果：" + election.getVoteResult());
            if (Election.voteCnt >= Election.slaveNum / 2 + 1) {
                log.info("slave：" + Election.slaveAddr + "成为 master!");

            }
        }
    }
}
