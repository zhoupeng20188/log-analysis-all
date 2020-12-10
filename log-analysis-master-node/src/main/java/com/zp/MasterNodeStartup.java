package com.zp;

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
public class MasterNodeStartup {
    private static Properties properties = null;
    private static CommandLine commandLine = null;
    public static void main(String[] args) {
        Options options = ServerUtil.buildCommandlineOptions(new Options());
        commandLine = ServerUtil.parseCmdLine("master", args, buildCommandlineOptions(options), new PosixParser());
        if (null == commandLine) {
            System.exit(-1);
//            return null;
        }
        new MasterNodeServer(1, 9527).start();
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
