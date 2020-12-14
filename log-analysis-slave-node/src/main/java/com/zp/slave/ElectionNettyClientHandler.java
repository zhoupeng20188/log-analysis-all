package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.protobuf.ElectionPOJO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElectionNettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                .setType(Consts.MSG_TYPE_ELECTION)
                .setTerm(Election.term)
                .setElectionId(Election.id);
        SlaveNodeServer.slaveClientChannels.add(ctx.channel());
        ctx.channel().writeAndFlush(msgSend);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ElectionPOJO.Election election = (ElectionPOJO.Election) msg;
        // 消息类型
        int type = election.getType();
        if (type == Consts.MSG_TYPE_ELECTION_ACK) {
            Election.voteCnt++;
            log.info("接收到salve：" + ctx.channel().remoteAddress() + "的投票结果：" + election.getVoteResult());
            if (Election.voteCnt >= Election.slaveNum / 2 + 1) {
                Election.isLeader = true;
                // 纪元+1
                Election.term++;
                log.info("slave：" + Election.port + "成为 master! term = " + Election.term);
                // 向其它slave发送成为master的消息
                log.info("slaveClientChannels size = " + SlaveNodeServer.slaveClientChannels.size());
                for (Channel slaveChannel : SlaveNodeServer.slaveClientChannels) {
                    ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                            .setType(Consts.MSG_TYPE_ELECTION_MASTER)
                            .setTerm(Election.term)
                            .setElectionId(Election.id);
                    log.info("向slave：" + slaveChannel.remoteAddress() + "发送成为master的消息");
                    slaveChannel.writeAndFlush(msgSend);
                }
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
