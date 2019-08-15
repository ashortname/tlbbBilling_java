package dataBase;

import com.mysql.jdbc.MySQLConnection;
import tModel.QueryResult;
import tModel.userAccount;
import tutil.tTool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dbService {

    /***
     * 添加字段
     * @param conn
     * @throws Exception
     */
    public static void appendFields(MySQLConnection conn)
            throws Exception
    {
        Map<String, Boolean> extraFields = new HashMap<>();
        extraFields.put("is_online", false);
        extraFields.put("is_lock", false);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            if(!conn.isClosed() && conn != null)
            {
                String sql = "SHOW COLUMNS FROM account";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next())
                {
                    String key = rs.getString("Field");
                    if(extraFields.containsKey(key)){
                        //标记已存在字段
                        extraFields.put(key, true);
                    }
                }
                //找出需要添加的字段
                List<String> needAdd = new ArrayList<>();
                extraFields.forEach((k, v) ->{
                    if(!v)
                        needAdd.add(k);
                });

                //添加字段
                for(String field : needAdd)
                {
                    String tmpSql = "ALTER TABLE account ADD COLUMN ? smallint(1) UNSIGNED NOT NULL DEFAULT 0";
                    PreparedStatement tps = conn.prepareStatement(tmpSql);
                    tps.setString(1, field);
                    tps.executeUpdate();
                    tps.close();
                }
            }
        }catch (Exception ex)
        {
            tTool.mLog("at appendFields 数据库查询出错：" + ex.getMessage());
            throw ex;
        }finally {
            dbOpenHelper.closePR(ps, rs);
        }
    }

    /***
     * 通过用户名查询用户
     * @param conn
     * @param username
     * @return
     */
    public static QueryResult GetAccountByUsername(MySQLConnection conn, String username)
    {
        userAccount account = new userAccount();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            if(!conn.isClosed() && conn != null)
            {
                String sql = "SELECT id,name,password,question,answer,email,qq,point,is_online,is_lock FROM account WHERE name = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                rs = ps.executeQuery();
                if(rs.wasNull())
                {
                    return new QueryResult(account, userAccount.QueryCode.UserNotFound);
                }
                else {
                    while (rs.next())
                    {
                        account.setId(rs.getInt(1));
                        account.setName(rs.getString(2));
                        account.setPassWord(rs.getString(3));
                        account.setQuestion(rs.getString(4));
                        account.setAnswer(rs.getString(5));
                        account.setEmail(rs.getString(6));
                        account.setQq(rs.getString(7));
                        account.setPoint(rs.getInt(8));
                        account.setIsOnline(rs.getByte(9));
                        account.setIsLock(rs.getByte(10));
                    }
                }
            }
        }catch (Exception ex)
        {
            tTool.mLog("at GetAccountByUsername 查询用户出错：" + ex.getMessage());
            return new QueryResult(account, userAccount.QueryCode.DbError);
        }finally {
            dbOpenHelper.closePR(ps, rs);
        }

        return new QueryResult(account, userAccount.QueryCode.UserFound);
    }

    /***
     * 登陆
     * @param conn
     * @param username
     * @param password
     * @return
     */
    public static byte GetLoginResult(MySQLConnection conn, String username, String password)
    {
        QueryResult result = GetAccountByUsername(conn,username);
        if(result.code == userAccount.QueryCode.UserNotFound)
            //用户不存在
            return 9;
        else  if(result.code == userAccount.QueryCode.DbError)
            //数据库错误
            return 6;
        else if(!result.acc.getPassWord().equals(password))
            return 3;
        else if(result.acc.getIsLock() != 0)
            return 7;
        else if(result.acc.getIsOnline() != 0)
            return 4;
        else
            return 1;
    }

    /***
     * 用户注册
     * @param conn
     * @param username
     * @param password
     * @param sPassword
     * @param email
     * @return
     */
    public static byte GetRegisterResult(MySQLConnection conn, String username, String password, String sPassword, String email)
    {
        QueryResult result = GetAccountByUsername(conn,username);
        PreparedStatement ps = null;
        ResultSet rs = null;
        int rCode = -1;
        if(result.code == userAccount.QueryCode.UserFound)
            //用户存在
            return 4;
        else  if(result.code == userAccount.QueryCode.DbError)
            //数据库错误
            return 4;
        if(email.equals("1@1.com"))
            return 4;
        try{
            if(!conn.isClosed() && conn != null)
            {
                String sql = "INSERT INTO account (name, password, question, email) VALUES (?, ?, ?, ?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, sPassword);
                ps.setString(4, email);

                rCode = ps.executeUpdate();
                if(rCode < 0)
                    return 4;
            }
        }catch (Exception ex)
        {
            tTool.mLog("at GetRegisterResult 用户注册出错：" + ex.getMessage());
            return 4;
        }finally {
            dbOpenHelper.closePR(ps, rs);
        }
        return 1;
    }

    /***
     * 更新用户在线状态
     * @param conn
     * @param username
     * @param isOnline
     * @return
     */
    public static boolean UpdateOnlineStatus(MySQLConnection conn, String username, boolean isOnline)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int rCode = -1;
        try{
            if(!conn.isClosed() && conn != null)
            {
                String sql = "UPDATE account SET is_online = ? WHERE name = ?";
                ps = conn.prepareStatement(sql);
                ps.setBoolean(1, isOnline);
                ps.setString(2, username);

                rCode = ps.executeUpdate();
                if(rCode < 0)
                    return false;
            }
        }catch (Exception ex)
        {
            tTool.mLog("at UpdateOnlineStatus 更新用户状态出错：" + ex.getMessage());
            return false;
        }finally {
            dbOpenHelper.closePR(ps, rs);
        }
        return true;
    }

    /***
     * 点数兑换
     * @param conn
     * @param username
     * @param realPoint
     * @return
     */
    public static boolean ConvertUserPoint(MySQLConnection conn, String username, int realPoint)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int rCode = -1;
        try{
            if(!conn.isClosed() && conn != null)
            {
                String sql = "UPDATE account SET point = point - ? WHERE name = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, realPoint);
                ps.setString(2, username);

                rCode = ps.executeUpdate();
                if(rCode < 0)
                    return false;
            }
        }catch (Exception ex)
        {
            tTool.mLog("at ConvertUserPoint 点数兑换出错：" + ex.getMessage());
            return false;
        }finally {
            dbOpenHelper.closePR(ps, rs);
        }
        return true;
    }
}
