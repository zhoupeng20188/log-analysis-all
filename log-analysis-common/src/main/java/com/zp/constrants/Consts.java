package com.zp.constrants;

/**
 * @Author zp
 * @create 2020/12/10 16:51
 */
public class Consts {
    public static final String FILE_NAME_MSG_INDEX_MAP = "msgIndexMap.log";
    public static final String FILE_NAME_PROJECT_MSG_MAP = "projectMsgMap.log";
    public static final String FILE_NAME_GLOBAL_COMMITED_INDEX = "globalCommitedIndex.log";
    public static final int MSG_TYPE_ACTIVE_SLAVE = 1;
    public static final int MSG_TYPE_UNCOMMITED_ACK = 2;
    public static final int MSG_TYPE_UNCOMMITED = 3;
    public static final int MSG_TYPE_COMMITED = 4;
    public static final int MSG_TYPE_CLIENT = 5;
    public static final int MSG_TYPE_HEARTBEAT = 6;
    public static final int MSG_TYPE_HEARTBEAT_ACK = 7;
    public static final int MSG_TYPE_ELECTION = 8;
    public static final int MSG_TYPE_ELECTION_ACK = 9;
    public static final int MSG_TYPE_ELECTION_MASTER = 10;
    public static final int MSG_TYPE_LOG_INDEX_COPY_REQUEST = 11;
    public static final int MSG_TYPE_LOG_INDEX_COPY_REQUEST_ACK = 12;
    public static final int MSG_TYPE_LOG_COPY_DATA = 13;
}
