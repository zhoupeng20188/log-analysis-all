package com.zp.utils;

import com.google.protobuf.ByteString;
import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.entity.ProjectMsg;
import com.zp.meta.MetaData;
import com.zp.protobuf.ElectionPOJO;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zp
 * @create 2020/12/14 16:37
 */
@Slf4j
public class ElectionUtil {
    public static void handleTypeMaster(Channel channel,
                                        int term,
                                        int index) {
        // 更新master信息
        log.info("更新master node 为" + channel.remoteAddress());
        // 更新term
        Election.term = term;
        HashMap<String, Integer> msgMap = new HashMap<>();
        for (Map.Entry<String, ProjectMsg> entry : MetaData.projectMsgMap.entrySet()) {
            msgMap.put(entry.getKey(), entry.getValue().getCommitedIndex());
        }
//        if (MetaData.globalIndex.get() < index) {
        // 发起获取日志同步请求
        ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                .setType(Consts.MSG_TYPE_LOG_INDEX_COPY_REQUEST)
                .putAllMsgMap(msgMap);
        channel.writeAndFlush(msgSend);
//        }
    }


    public static void handleTypeLogIndexCopyRequest(Channel channel,
                                                     Map<String, Integer> msgMap) {
        byte[] copyBytes = null;
        HashMap<String, Integer> logCopyIndexMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : msgMap.entrySet()) {
            String projectId = entry.getKey();
            Integer commitedIndex = entry.getValue();
            ProjectMsg projectMsg = MetaData.projectMsgMap.get(projectId);
            int commitedIndexLocal = projectMsg.getCommitedIndex();
            if (commitedIndexLocal > commitedIndex) {
                // 此工程需要同步日志的场合
                List<Integer> msgIndexList = projectMsg.getMsgIndexList();
                Integer bytesFrom = msgIndexList.get(commitedIndex);
                Integer bytesTo = msgIndexList.get(commitedIndexLocal);
                // 计算需要同步的字节数并保存
                logCopyIndexMap.put(projectId, bytesTo - bytesFrom);
                // 读取字节数
                byte[] bytes = FileUtil.readBytes(new File(projectId + ".log"), commitedIndex, commitedIndexLocal);
                ByteUtil.appendToTail(copyBytes, bytes);
            }
        }

        ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                .setType(Consts.MSG_TYPE_LOG_COPY_DATA)
                .setIndex(Election.index)
                .setIndexMapLog(FileUtil.convertFileToByteString(new File(Consts.FILE_NAME_MSG_INDEX_MAP)))
                .setMsgMapLog(FileUtil.convertFileToByteString(new File(Consts.FILE_NAME_PROJECT_MSG_MAP)))
                .putAllLogCopyIndexMap(logCopyIndexMap)
                .setLogCopyBytes(ByteString.copyFrom(copyBytes));
        channel.writeAndFlush(msgSend);
    }

    public static void handleLogCopyData(ByteString indexMapLog,
                                         ByteString msgMapLog,
                                         Map<String, Integer> logCopyIndexMap,
                                         ByteString logCopyBytes) {

        // 覆盖indexMap文件
        FileUtil.writeOverride(new File(Consts.FILE_NAME_MSG_INDEX_MAP), indexMapLog);
        // 覆盖msgMap文件
        FileUtil.writeOverride(new File(Consts.FILE_NAME_PROJECT_MSG_MAP), msgMapLog);
        byte[] copyBytes = logCopyBytes.toByteArray();
        for (Map.Entry<String, Integer> entry : logCopyIndexMap.entrySet()) {
            String projectId = entry.getKey();
            Integer bytes = entry.getValue();
            byte[] content = ByteUtil.readBytes(copyBytes, bytes);
            // 追加写入日志文件
            FileUtil.write(new File(projectId + ".log"), content);
        }
    }

    public static void handleTypeLogIndexCopyAck(Channel channel,
                                                 int index,
                                                 String projectId,
                                                 String content) {
        // 强制更新为master的日志index
        Election.index = index;
        // index存盘
        MsgUtil.storeIndex();
        MsgUtil.storeMsg(content, 0, projectId);
    }
}
