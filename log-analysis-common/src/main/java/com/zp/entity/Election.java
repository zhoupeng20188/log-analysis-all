package com.zp.entity;

public class Election {
    /**
     * 纪元
     */
    public static volatile int term;
    /**
     * 选举轮次
     */
    public static volatile int id;
    /**
     * 当前消息index
     */
    public static volatile int index;
    /**
     * slave数量
     */
    public static int slaveNum;
    /**
     * 票数
     */
    public static volatile int voteCnt;
    /**
     * 是否为leader
     */
    public static volatile boolean isLeader;
    /**
     * 当前slave port
     */
    public static int port;
}
