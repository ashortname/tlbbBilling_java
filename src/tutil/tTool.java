package tutil;

import config.tconfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class tTool {
    public static ServerSocketChannel server = null;
    public static Selector selector = null;

    /**
     * 构造函数
     */
    public tTool(){

    }


    /****
     * 简单的日志记录，并保存到文件
     * @param msg
     * @throws IOException
     */
    public static void mLog(String msg)
    {
        OutputStreamWriter osw = null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = String.format("[ %s ] %s \n", sdf.format(new Date().getTime()), msg);
            System.out.print(str);
            if(tconfig.isLog)
            {
                String path = getLocatePath();
                osw = new OutputStreamWriter(new FileOutputStream(path + "\\log.log", true));
                osw.write(str);
            }
        }catch (Exception ex)
        {
            System.out.print("日志记录出现错误：" + ex.getMessage());
        }finally {
            try{
                if(osw != null)
                    osw.close();
            }catch (Exception ex)
            {

            }
        }
    }

    /***
     * 获取路径
     * @return
     * @throws IOException
     */
    public static String getLocatePath()
            throws IOException
    {
        File file = new File("");
        return file.getCanonicalPath();
    }

    /***
     * 判断IP是否允许连接
     * @param ip
     * @return
     */
    public static boolean isIPAllowed(String ip)
    {
        if(tconfig.allow_ips.size() == 0)
            return true;
        for(String str : tconfig.allow_ips)
        {
            if(str.trim().equals(ip.trim()))
                return true;
        }
        return false;
    }
}
