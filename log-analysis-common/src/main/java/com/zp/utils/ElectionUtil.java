package com.zp.utils;

import com.zp.constrants.Consts;
import com.zp.entity.Election;
import com.zp.protobuf.ElectionPOJO;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zp
 * @create 2020/12/14 16:37
 */
@Slf4j
public class ElectionUtil {
    public static void handleTypeMaster(Channel channel,
                                        Channel masterChannel,
                                        int term,
                                        int index) {
        // 更新master信息
        log.info("更新master node 为" + channel.remoteAddress());
        masterChannel = channel;
        // 更新term
        Election.term = term;
        if (Election.index < index) {
            // 发起获取日志同步请求
            ElectionPOJO.Election.Builder msgSend = ElectionPOJO.Election.newBuilder()
                    .setType(Consts.MSG_TYPE_LOG_INDEX_COPY_REQUEST)
                    .setIndex(Election.index);
            channel.writeAndFlush(msgSend);
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
        MsgUtil.storeMsg(content,0,projectId);
    }
}
