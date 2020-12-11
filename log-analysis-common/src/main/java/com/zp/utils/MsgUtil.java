package com.zp.utils;

import com.zp.entity.ProjectMsg;
import com.zp.meta.MetaData;

import java.io.*;
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
     *
     * @param msgContent
     * @param msgId
     * @param projectId
     */
    public static void storeMsg(String msgContent,
                                long msgId,
                                String projectId) {
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
        File file = new File(projectId + ".log");
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
            fos = new FileOutputStream("msgIndexMap.log");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(MetaData.msgIndexMap);
            fos = new FileOutputStream("projectMsgMap.log");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(MetaData.projectMsgMap);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initIndex() {
        try {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            File file = new File("msgIndexMap.log");
            if (!file.exists()) {
                file.createNewFile();
            } else if (file.length() > 0) {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                MetaData.msgIndexMap = (ConcurrentHashMap<Long, Integer>) ois.readObject();
            }

            file = new File("projectMsgMap.log");
            if (!file.exists()) {
                file.createNewFile();
            } else if (file.length() > 0) {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                MetaData.projectMsgMap = (ConcurrentHashMap<String, ProjectMsg>) ois.readObject();
            }
            if (ois != null) {
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
