package com.zp.utils;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @Author zp
 * @create 2020/12/14 14:25
 */
public class ChannelUtil {
    public static void storeChannel(Set<Channel> slaveClientChannels,
                                    HashMap<String, Channel> slaveChannelMap,
                                    Channel channel){
        slaveClientChannels.add(channel);
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        String ip = inetSocketAddress.getAddress().getHostAddress();
        int port = inetSocketAddress.getPort();
        String address = ip + ":" + port;
        slaveChannelMap.put(address, channel);
    }

    public static void storeSlaveAddress(Channel channel, List<String> slaveServerList, int port){
        // 获取当前连接的客户端的ip
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        String ip = inetSocketAddress.getAddress().getHostAddress();
        String address = ip + ":" + port;
        slaveServerList.add(address);
    }
}
