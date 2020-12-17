package com.zp.handler;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.entity.Server;
import com.zp.meta.MetaData;
import com.zp.protobuf.ElectionPOJO;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.ChannelUtil;
import com.zp.utils.ElectionUtil;
import com.zp.utils.MsgUtil;
import com.zp.utils.ThreadUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;

@Slf4j
public class ElectionNettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgPOJO.Msg election = (MsgPOJO.Msg) msg;
        // 消息类型
        int type = election.getType();
        boolean isLeader = election.getIsLeader();
        boolean isOldLeader = election.getIsOldLeader();
        log.info("接收到消息：" + msg);
        if (type == Consts.MSG_TYPE_ELECTION) {
            // 选举轮次
            int electionId = election.getElectionId();
            // 消息index
            int index = election.getIndex();
            int term = election.getTerm();
            log.info("接收到slave的投票请求，当前term:{},index:{}", Election.term, MetaData.globalCommitedIndex.get());
            if (Election.id <= electionId
                    && Election.term <= term
                    && MetaData.globalCommitedIndex.get() <= index
                    && !Election.isLeader) {
//                ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                        .setType(Consts.MSG_TYPE_ELECTION_ACK)
                        .setTerm(Election.term)
                        .setElectionId(Election.id)
                        .setVoteResult(1);
                ctx.channel().writeAndFlush(msgSend);
            }

        } else if (type == Consts.MSG_TYPE_ELECTION_MASTER) {
            // 更新master信息
            Server.masterChannel = ctx.channel();
            ElectionUtil.handleTypeMaster(ctx.channel(), election.getTerm(), election.getIndex());

        } else if (type == Consts.MSG_TYPE_HEARTBEAT) {
            if (isOldLeader) {
                // 停止向老master发送心跳
                Election.stopHeartbeat = true;
            }
            if (isLeader && Election.isLeader) {
                // 对方是leader时，代表自己是老master重连，给新master发送心跳
                Server.masterChannel = ctx.channel();
                SocketAddress socketAddress = ctx.channel().remoteAddress();
                String[] split = String.valueOf(socketAddress).replace("/", "").split(":");
                log.info("收到新master的连接，准备发送心跳到新master.");
                ThreadUtil.startHeartbeatThread(split[0], Integer.parseInt(split[1]));
                // 更新master状态
                Election.isLeader = false;
            } else {
                // 保存slave的地址
                ChannelUtil.storeSlaveAddress(ctx.channel(), election.getPort());
                // 发送heartbeat的ack，包括所有slave server的地址
                MsgUtil.sendHeartbeatAck(ctx, election.getPort());
            }
        } else if (type == Consts.MSG_TYPE_HEARTBEAT_ACK) {
            if (!isLeader) {
                Election.stopHeartbeat = true;
            } else {
                log.info("接收到最新的slave集群地址：" + election.getContent());
                Server.otherSlaveAddrs = election.getContent();
            }
        } else if (type == Consts.MSG_TYPE_LOG_INDEX_COPY_REQUEST) {
            Map<String, Integer> msgMapMap = election.getMsgMapMap();
            ElectionUtil.handleTypeLogIndexCopyRequest(ctx.channel(), msgMapMap);
        } else if (type == Consts.MSG_TYPE_LOG_COPY_DATA) {
            ElectionUtil.handleLogCopyData(election.getIndexMapLog(),
                    election.getMsgMapLog(),
                    election.getLogCopyIndexMapMap(),
                    election.getLogCopyBytes(),
                    election.getIndex());
        }
    }
}
