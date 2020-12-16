package com.zp.slave;

import com.zp.entity.Server;
import com.zp.meta.MetaData;
import com.zp.utils.ServerUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import java.util.Properties;

/**
 * @Author zp
 * @create 2020/12/8 17:53
 */
public class SlaveNodeStartup {
    private static Properties properties = null;
    private static CommandLine commandLine = null;
    public static void main(String[] args) {
        Options options = ServerUtil.buildCommandlineOptions(new Options());
        commandLine = ServerUtil.parseCmdLine("slave", args, buildCommandlineOptions(options), new PosixParser());
        if (null == commandLine) {
            System.exit(-1);
//            return null;
        }
        String arg0 = args[0];
        String arg1 = args[1];
        String arg2 = args[2];
        Server.port = Integer.parseInt(arg1);
        MetaData.fileDir = arg2;
        new SlaveNodeServer(Integer.parseInt(arg0), "127.0.0.1", 9527).start();
    }

    public static Options buildCommandlineOptions(final Options options) {
        Option opt = new Option("c", "configFile", true, "master node config properties file");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("p", "printConfigItem", false, "Print all config item");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }

}
