package com.zp.utils;

import com.zp.entity.ProjectMsg;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zp
 * @create 2020/12/11 10:55
 */
public class MsgUtil {
    /**
     * 本地存储消息
     * @param msgContent
     * @param msgIndexMap
     * @param projectMsgMap
     * @param msgId
     * @param projectId
     */
    public static void storeMsg(String msgContent,
                                ConcurrentHashMap msgIndexMap,
                                ConcurrentHashMap<String, ProjectMsg> projectMsgMap,
                                long msgId,
                                String projectId){
        ProjectMsg projectMsg = projectMsgMap.get(projectId);
        if(projectMsg == null){
            projectMsg = new ProjectMsg();
        }

        AtomicInteger index = projectMsg.getIndex();
        List<Integer> msgIndexList = projectMsg.getMsgIndexList();
        // 该条消息的字节长度
        int msgLength = msgContent.getBytes().length;
        if(index.get() != -1) {
            int lastIndex = index.get();
            if (lastIndex != -1) {
                msgLength += msgIndexList.get(lastIndex);
            }
        }

        int indexNow = index.incrementAndGet();
        msgIndexMap.put(msgId, indexNow);
        msgIndexList.add(indexNow, msgLength);
        // 顺序写文件
        File file = new File(projectId + ".log");
        FileUtil.write(file, msgContent);
        projectMsgMap.put(projectId, projectMsg);
    }

    /**
     * 修改本地消息状态为commited
     * @param projectId
     * @param msgId
     * @param msgIndexMap
     * @param projectMsgMap
     */
    public static void changeToCommited(String projectId,
                                        long msgId,
                                        ConcurrentHashMap<Long, Integer> msgIndexMap,
                                        ConcurrentHashMap<String, ProjectMsg> projectMsgMap){
        ProjectMsg projectMsg = projectMsgMap.get(projectId);
        projectMsg.setCommitedIndex(msgIndexMap.get(msgId));
    }
}
