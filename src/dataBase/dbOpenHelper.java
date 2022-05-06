package dataBase;

import config.tconfig;
import tutil.tTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class dbOpenHelper {
    private static String driver = "com.mysql.jdbc.Driver";//MySQL 驱动
    /**
     * 连接数据库
     * */
    public static Connection getConn()
        throws Exception{
        Connection conn = null;
        String url = String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true",
                tconfig.db_host, tconfig.db_port, tconfig.db_name);
        if(tconfig.allow_old_password)
            url += "&allowOldPasswords=true";
        try {
            Class.forName(driver);//获取MYSQL驱动
            DriverManager.setLoginTimeout(3);//3s超时
            conn =  DriverManager.getConnection(url, tconfig.db_user, tconfig.db_password);//获取连接
        } catch (Exception ex)
        {
            tTool.mLog(" !!! error at getConn 连接数据库出错：" + ex.getMessage());
            throw ex;
        }
        return conn;
    }

    /**
     * 关闭数据库
     * */
    public static void closeAll(Connection conn){
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {

            }
        }
    }

    public static void closePR(PreparedStatement ps, ResultSet rs)
    {
        try{
            if(ps != null)
                if(!ps.isClosed())
                    ps.close();
            if(rs != null)
                if(!rs.isClosed())
                    rs.close();
        }catch (Exception ex)
        {

        }
    }
}
