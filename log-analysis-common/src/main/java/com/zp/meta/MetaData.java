package com.zp.meta;

import com.zp.entity.ProjectMsg;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zp
 * @create 2020/12/11 13:20
 */
public class MetaData {
    /**
     * 消息indexMap，key为msgId，value为index
     */
    public static ConcurrentHashMap<Long, Integer> msgIndexMap = new ConcurrentHashMap<>();

    /**
     * key为projectId，value为ProjectMsg
     */
    public static ConcurrentHashMap<String, ProjectMsg> projectMsgMap = new ConcurrentHashMap<>();

    /**
     * 全局消息id
     */
    public static AtomicInteger globalIndex = new AtomicInteger(-1);
    /**
     * 全局已提交消息id
     */
    public static AtomicInteger globalCommitedIndex = new AtomicInteger(-1);
}
