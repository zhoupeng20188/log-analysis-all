package com.zp.utils;

import com.zp.entity.ProjectMsg;
import com.zp.meta.MetaData;

/**
 * @Author zp
 * @create 2020/12/15 9:01
 */
public class MetaDataUtil {
    public static int getIndex(String projectId){
        ProjectMsg projectMsg = MetaData.projectMsgMap.get(projectId);
        return projectMsg.getIndex().get();
    }

    public static int getCommitedIndex(String projectId){
        ProjectMsg projectMsg = MetaData.projectMsgMap.get(projectId);
        return projectMsg.getCommitedIndex();
    }
}
