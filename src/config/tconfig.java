package config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONReader;
import tutil.tTool;

public class tconfig {
    public static String ip;
    public static int port;
    public static String db_host;
    public static int db_port;
    public static String db_user;
    public static String db_password;
    public static String db_name;
    public static boolean allow_old_password;
    public static boolean auto_reg;
    public static List<String> allow_ips;
    public static int transfer_number;
    public static boolean isLog;
    public static boolean allow_point;


    public static void loadConfig()
            throws IOException
    {
        //初始化默认值
        ip = "127.0.0.1";
        port = 9870;
        db_host = "127.0.0.1";
        db_port = 3306;
        db_user = "root";
        db_password = "maomao";
        db_name = "web";
        allow_old_password = false;
        allow_ips = new ArrayList<>();
        auto_reg = true;
        transfer_number = 1000;
        isLog = false;
        allow_point = true;

        class tempConfig{
            public  String ip;
            public  int port;
            public  String db_host;
            public  int db_port;
            public  String db_user;
            public  String db_password;
            public  String db_name;
            public  boolean allow_old_password;
            public  boolean auto_reg;
            public  List<String> allow_ips;
            public  int transfer_number;
            public  boolean isLog;
            public boolean allow_point;
        }

        //再从配置文件获取
        String path = tTool.getLocatePath() + "\\config.json";
        JSONReader reader = new JSONReader(new FileReader(path));
        tempConfig tt = reader.readObject(tempConfig.class);


        tconfig.ip = tt.ip;
        tconfig.port = tt.port;
        tconfig.db_host = tt.db_host;
        tconfig.db_port = tt.db_port;
        tconfig.db_user = tt.db_user;
        tconfig.db_password = tt.db_password;
        tconfig.db_name = tt.db_name;
        tconfig.allow_old_password = tt.allow_old_password;
        tconfig.auto_reg = tt.auto_reg;
        tconfig.transfer_number = tt.transfer_number;
        tconfig.allow_ips = tt.allow_ips;
        tconfig.isLog = tt.isLog;
        tconfig.allow_point = tt.allow_point;
    }
}
