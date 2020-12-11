package com.zp.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zp
 * @create 2020/12/11 11:11
 */
public class ProjectMsg implements Serializable {
    private String projectId;
    /**
     * 消息indexList，在index对应的下标中存储的是消息的字节长度（从开始到现在）
     */
    private List<Integer> msgIndexList = new ArrayList<>();

    /**
     * 已提交的index
     */
    private int commitedIndex = -1;
    /**
     * 消息index
     */
    private AtomicInteger index = new AtomicInteger(-1);

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<Integer> getMsgIndexList() {
        return msgIndexList;
    }

    public void setMsgIndexList(List<Integer> msgIndexList) {
        this.msgIndexList = msgIndexList;
    }

    public int getCommitedIndex() {
        return commitedIndex;
    }

    public void setCommitedIndex(int commitedIndex) {
        this.commitedIndex = commitedIndex;
    }

    public AtomicInteger getIndex() {
        return index;
    }

    public void setIndex(AtomicInteger index) {
        this.index = index;
    }
}
