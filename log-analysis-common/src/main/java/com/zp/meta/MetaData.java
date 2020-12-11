package com.zp.meta;

import com.zp.entity.ProjectMsg;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zp
 * @create 2020/12/11 13:20
 */
public class MetaData {
    /**
     * 消息indexMap，key为msgId，value为index
     */
    public static ConcurrentHashMap<Long, Integer> msgIndexMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ProjectMsg> projectMsgMap = new ConcurrentHashMap<>();
}
