package com.zp.entity;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author zp
 * @create 2020/12/16 15:10
 */
public class Server {
    public static int id;
    public static volatile Channel masterChannel;
    public static volatile String otherSlaveAddrs;
    public static volatile Set<Channel> slaveClientChannels = new HashSet<>();
    public static volatile HashMap<String, Channel> slaveChannelMap = new HashMap<>();
    public static volatile Set<String> slaveServerList = new HashSet<>();
    public static volatile int port;
}
