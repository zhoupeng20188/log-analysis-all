package com.zp.utils;

import com.zp.constrants.Consts;
import com.zp.entity.ProjectMsg;
import com.zp.entity.Server;
import com.zp.meta.MetaData;
import com.zp.protobuf.MsgPOJO;
import io.netty.channel.ChannelHandlerContext;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zp
 * @create 2020/12/11 10:55
 */
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

    public static void storeCommitedIndex(){
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

            if(!excludeCommitedIndex) {
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
            if (!s.contains(String.valueOf(port))) {
                // 返回不包含自己的其它slave的地址
                remoteAddress += s + ",";
            }
        }
        if (!StringUtil.isEmpty(remoteAddress)) {
            remoteAddress = remoteAddress.substring(0, remoteAddress.length() - 1);
        }
        MsgPOJO.Msg.Builder msgSend = MsgPOJO.Msg.newBuilder()
                .setType(Consts.MSG_TYPE_HEARTBEAT_ACK)
                .setContent(remoteAddress);
        ctx.channel().writeAndFlush(msgSend);
    }
}
