package com.zp.slave;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.entity.ProjectMsg;
import com.zp.meta.MetaData;
import com.zp.protobuf.ElectionPOJO;
import com.zp.protobuf.MsgPOJO;
import com.zp.utils.ChannelUtil;
import com.zp.utils.FileUtil;
import com.zp.utils.MsgUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class ElectionNettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                .setType(Consts.MSG_TYPE_ELECTION)
                .setTerm(Election.term)
                .setElectionId(Election.id);
        ChannelUtil.storeChannel(SlaveNodeServer.slaveClientChannels, SlaveNodeServer.slaveChannelMap, ctx.channel());
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
            // 保存slave的地址
            ChannelUtil.storeSlaveAddress(ctx.channel(), SlaveNodeServer.slaveServerList, election.getPort());
            // 发送heartbeat的ack，包括所有slave server的地址
            MsgUtil.sendHeartbeatAck(ctx, SlaveNodeServer.slaveServerList, election.getPort());
        } else if (type == Consts.MSG_TYPE_HEARTBEAT_ACK) {
            log.info("接收到最新的slave集群地址：" + election.getContent());
            SlaveNodeServer.otherSlaveAddrs = election.getContent();
        } else if (type == Consts.MSG_TYPE_LOG_INDEX_COPY_REQUEST_ACK) {
            String projectId = election.getProjectId();
            int index = election.getIndex();
            ProjectMsg projectMsg = MetaData.projectMsgMap.get(projectId);
            List<Integer> msgIndexList = projectMsg.getMsgIndexList();
            // 消息起始位置
            int from = msgIndexList.get(index);
            int indexNow = Election.index;
            // 消息最后位置
            int last = msgIndexList.get(indexNow);
            int bytes = last - from;
            ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                    .setType(Consts.MSG_TYPE_LOG_COPY_DATA)
                    .setIndex(Election.index)
                    .setIndexMap(FileUtil.convertFileToByteString(new File(Consts.FILE_NAME_MSG_INDEX_MAP)))
                    .setIndexMap(FileUtil.convertFileToByteString(new File(Consts.FILE_NAME_PROJECT_MSG_MAP)))
                    .setContent(FileUtil.read(new File(projectId + ".log"), from, bytes));
            ctx.channel().writeAndFlush(msgSend);
        }
    }
}
