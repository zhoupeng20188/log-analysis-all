package com.zp.utils;

import com.zp.entity.Server;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
        SocketAddress remoteAddress = channel.remoteAddress();
        String address = String.valueOf(remoteAddress).replaceAll("/","");
        slaveChannelMap.put(address, channel);
    }

    public static void storeSlaveAddress(Channel channel, int port){
        // 获取当前连接的客户端的ip
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        String ip = inetSocketAddress.getAddress().getHostAddress();
        String address = ip + ":" + port;
        Server.slaveServerList.add(address);
        Server.slaveClientChannels.add(channel);
    }
}
