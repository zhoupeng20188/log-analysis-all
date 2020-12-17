package com.zp.utils;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.entity.ProjectMsg;
import com.zp.entity.Server;
import com.zp.meta.MetaData;
import com.zp.protobuf.MsgPOJO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zp
 * @create 2020/12/11 10:55
 */
@Slf4j
public class MsgUtil {
    /**
     * 本地存储消息
     *
     * @param msgContent
     * @param msgId
     * @param projectId
     */
    public static void storeMsg(String msgContent,
                                long msgId,
                                String projectId) {
        // 全局index+1
        MetaData.globalIndex.incrementAndGet();
        ProjectMsg projectMsg = MetaData.projectMsgMap.get(projectId);
        if (projectMsg == null) {
            projectMsg = new ProjectMsg();
        }

        AtomicInteger index = projectMsg.getIndex();
        List<Integer> msgIndexList = projectMsg.getMsgIndexList();
        // 该条消息的字节长度
        int msgLength = msgContent.getBytes().length;
        if (index.get() != -1) {
            int lastIndex = index.get();
            if (lastIndex != -1) {
                msgLength += msgIndexList.get(lastIndex);
            }
        }

        int indexNow = index.incrementAndGet();
        MetaData.msgIndexMap.put(msgId, indexNow);
        msgIndexList.add(indexNow, msgLength);
        // 顺序写文件
        File file = new File(MetaData.fileDir + projectId + ".log");
        FileUtil.write(file, msgContent);
        MetaData.projectMsgMap.put(projectId, projectMsg);
    }

    /**
     * 修改本地消息状态为commited
     *
     * @param projectId
     * @param msgId
     */
    public static void changeToCommited(String projectId,
                                        long msgId) {
        // 全局commitedIndex+1
        MetaData.globalCommitedIndex.incrementAndGet();
        ProjectMsg projectMsg = MetaData.projectMsgMap.get(projectId);
        projectMsg.setCommitedIndex(MetaData.msgIndexMap.get(msgId));
        // index存盘
        storeIndex();
    }

    public static void storeIndex() {
        try {
            // 写
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            fos = new FileOutputStream(MetaData.fileDir + Consts.FILE_NAME_MSG_INDEX_MAP);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(MetaData.msgIndexMap);
            fos = new FileOutputStream(MetaData.fileDir + Consts.FILE_NAME_PROJECT_MSG_MAP);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(MetaData.projectMsgMap);
            storeCommitedIndex();
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void storeCommitedIndex() {
        FileUtil.writeOverride(new File(MetaData.fileDir + Consts.FILE_NAME_GLOBAL_COMMITED_INDEX), MetaData.globalCommitedIndex.toString());
    }

    public static void initIndex() {
        initIndex(false);
    }

    public static void initIndex(boolean excludeCommitedIndex) {
        try {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            File file = new File(MetaData.fileDir + Consts.FILE_NAME_MSG_INDEX_MAP);
            if (!file.exists()) {
                file.createNewFile();
            } else if (file.length() > 0) {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                MetaData.msgIndexMap = (ConcurrentHashMap<Long, Integer>) ois.readObject();
            }

            file = new File(MetaData.fileDir + Consts.FILE_NAME_PROJECT_MSG_MAP);
            if (!file.exists()) {
                file.createNewFile();
            } else if (file.length() > 0) {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                MetaData.projectMsgMap = (ConcurrentHashMap<String, ProjectMsg>) ois.readObject();
            }

            if (!excludeCommitedIndex) {
                file = new File(MetaData.fileDir + Consts.FILE_NAME_GLOBAL_COMMITED_INDEX);
                if (!file.exists()) {
                    file.createNewFile();
                } else if (file.length() > 0) {
                    fis = new FileInputStream(file);
                    byte[] bytes = new byte[1];
                    fis.read(bytes, 0, 1);
                    MetaData.globalCommitedIndex = new AtomicInteger(Integer.parseInt(new String(bytes)));
                }
            }

            if (ois != null) {
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendHeartbeatAck(ChannelHandlerContext ctx,
                                        int port) {
        // 发送heartbeat的ack，包括所有slave server的地址
        String remoteAddress = "";
        for (String s : Server.slaveServerList) {
            if (!s.contains(String.valueOf(":" + port))) {
                // 返回不包含自己的其它slave的地址
                remoteAddress += s + ",";
            }
        }
        if (!StringUtil.isEmpty(remoteAddress)) {
            remoteAddress = remoteAddress.substring(0, remoteAddress.length() - 1);
        }

        log.info("准备返回slave集群地址：" + remoteAddress);

        MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                .setType(Consts.MSG_TYPE_HEARTBEAT_ACK)
                .setIsLeader(Election.isLeader)
                .setContent(remoteAddress);
        ctx.channel().writeAndFlush(msgSend);
    }

    public static void handleMsg(ChannelHandlerContext ctx, Object msg) {
        MsgPOJO.Msg msgRsrv = (MsgPOJO.Msg) msg;
        // 消息id
        long msgId = msgRsrv.getMsgId();
        // 消息内容
        String msgContent = msgRsrv.getContent();
        // 消息类型
        int msgType = msgRsrv.getType();
        // projectId
        String projectId = msgRsrv.getProjectId();
        // 对方是否为leader
        boolean isLeader = msgRsrv.getIsLeader();
        boolean isOldLeader = msgRsrv.getIsOldLeader();
        int port = msgRsrv.getPort();
        log.info("接收到消息：" + msg);
        MsgPOJO.Msg.Builder builder = MsgPOJO.Msg.newBuilder();
        MsgPOJO.Msg.Builder msgSend = null;
        if (msgType == Consts.MSG_TYPE_HEARTBEAT) {
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
                ChannelUtil.storeSlaveAddress(ctx.channel(), port);
                // 发送heartbeat的ack，包括所有slave server的地址
                MsgUtil.sendHeartbeatAck(ctx, port);
                // 停止向老master发送心跳
                Election.stopHeartbeat = true;
            }
        } else if (msgType == Consts.MSG_TYPE_HEARTBEAT_ACK) {
            if (!isLeader) {
                Election.stopHeartbeat = true;
            } else {
                log.info("接收到最新的slave集群地址：" + msgContent);
                Server.otherSlaveAddrs = msgContent;
            }
        } else if (msgType == Consts.MSG_TYPE_UNCOMMITED) {
            log.info("接收到master的uncommited请求！");
            // 本地存储消息
            MsgUtil.storeMsg(msgContent, msgId, projectId);
            // 给master发送ACK消息
            msgSend = builder
                    .setMsgId(msgId)
                    .setProjectId(projectId)
                    .setType(Consts.MSG_TYPE_UNCOMMITED_ACK)
                    .setContent(msgContent);
            ctx.channel().writeAndFlush(msgSend);
        } else if (msgType == Consts.MSG_TYPE_COMMITED) {
            log.info("接收到master的commited请求！");
            // 修改本地状态为commited
            MsgUtil.changeToCommited(projectId, msgId);
        } else if (msgType == Consts.MSG_TYPE_UNCOMMITED_ACK) {
            log.info("接收到slave：" + ctx.channel().remoteAddress() + "的ack");
            int ackCnt = 0;
            if (Server.ackMap.get(msgId) != null) {
                ackCnt = Server.ackMap.get(msgId) + 1;
            } else {
                ackCnt++;
            }
            Server.ackMap.put(msgId, ackCnt);
            if (ackCnt >= Server.slaveClientChannels.size() / 2 + 1) {
                // 修改本地状态为commited
                log.info("接收到超过半数的ack！成功写入！");
                MsgUtil.changeToCommited(projectId, msgId);
                // 发送commited请求给自己的slave
                for (Channel channel : Server.slaveClientChannels) {
                    msgSend = builder
                            .setMsgId(msgId)
                            .setProjectId(projectId)
                            .setType(Consts.MSG_TYPE_COMMITED)
                            .setContent(msgContent);
                    channel.writeAndFlush(msgSend);
                }
            }

        } else if (msgType == Consts.MSG_TYPE_CLIENT) {
            log.info("接收到客户端的请求！准备写入！");
            // 本地存储消息
            MsgUtil.storeMsg(msgContent, msgId, projectId);

            // 发送uncommited请求给自己的slave
            for (Channel channel : Server.slaveClientChannels) {
                msgSend = builder
                        .setMsgId(msgId)
                        .setType(Consts.MSG_TYPE_UNCOMMITED)
                        .setProjectId(projectId)
                        .setContent(msgContent);
                channel.writeAndFlush(msgSend);
            }
        } else if (msgType == Consts.MSG_TYPE_ELECTION) {
            // 选举轮次
            int electionId = msgRsrv.getElectionId();
            // 消息index
            int index = msgRsrv.getIndex();
            int term = msgRsrv.getTerm();
            log.info("接收到slave的投票请求，当前term:{},index:{}", Election.term, MetaData.globalCommitedIndex.get());
            if (Election.id <= electionId
                    && Election.term <= term
                    && MetaData.globalCommitedIndex.get() <= index
                    && !Election.isLeader) {
                msgSend = builder
                        .setType(Consts.MSG_TYPE_ELECTION_ACK)
                        .setTerm(Election.term)
                        .setElectionId(Election.id)
                        .setVoteResult(1);
                ctx.channel().writeAndFlush(msgSend);
            }

        } else if (msgType == Consts.MSG_TYPE_ELECTION_ACK) {
            Election.voteCnt++;
            log.info("接收到salve：" + ctx.channel().remoteAddress() + "的投票结果：" + msgRsrv.getVoteResult());
            if (Election.voteCnt >= Election.slaveNum / 2 + 1) {
                Election.isLeader = true;
                // 纪元+1
                Election.term++;
                log.info("slave：" + Election.port + "成为 master! term = " + Election.term);
                // 向其它slave发送成为master的消息
                log.info("slaveClientChannels size = " + Server.slaveClientChannels.size());
                for (Channel slaveChannel : Server.slaveClientChannels) {
                    msgSend = builder
                            .setType(Consts.MSG_TYPE_ELECTION_MASTER)
                            .setTerm(Election.term)
                            .setIndex(MetaData.globalCommitedIndex.get())
                            .setElectionId(Election.id);
                    log.info("向slave：" + slaveChannel.remoteAddress() + "发送成为master的消息");
                    slaveChannel.writeAndFlush(msgSend);
                }
            }
        } else if (msgType == Consts.MSG_TYPE_ELECTION_MASTER) {
            // 更新master信息
            Server.masterChannel = ctx.channel();
            ElectionUtil.handleTypeMaster(ctx.channel(), msgRsrv.getTerm(), msgRsrv.getIndex());
        } else if (msgType == Consts.MSG_TYPE_HEARTBEAT) {
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
                // 更新master状态
                Election.isLeader = false;
                Election.isOldLeader = true;
                ThreadUtil.startHeartbeatThread(split[0], Integer.parseInt(split[1]));
            } else {
                // 保存slave的地址
                ChannelUtil.storeSlaveAddress(ctx.channel(), port);
                // 发送heartbeat的ack，包括所有slave server的地址
                MsgUtil.sendHeartbeatAck(ctx, port);
            }

        }
    }
}
