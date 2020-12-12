package com.zp.slave;

public class Election {
    /**
     * 纪元
     */
    public static int term;
    /**
     * 选举轮次
     */
    public static int id;
    /**
     * 当前消息index
     */
    public static int index;
    /**
     * slave数量
     */
    public static int slaveNum;
    /**
     * 票数
     */
    public static int voteCnt;
    /**
     * 是否为leader
     */
    public static boolean isLeader;
    /**
     * 当前slave port
     */
    public static int port;
}
