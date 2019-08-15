package tHandler;

import com.mysql.jdbc.MySQLConnection;
import com.sun.corba.se.impl.ior.ByteBuffer;
import config.tconfig;
import dataBase.dbService;
import tModel.billingData;
import tutil.tTool;


public class LoginHandler extends Handler {
    @Override
    public billingData getResponse(billingData bData, MySQLConnection connection) {
        billingData response = new billingData();
        response.PrePareResponse(bData);
        ByteBuffer byteBuffer = new ByteBuffer();
        byte[] opData = bData.getOpData();
        //用户名
        int offset = 0;
        byte usernameLength = opData[offset];
        int tmpLength = usernameLength & 0xff;
        offset++;
        byte[] busername = new byte[tmpLength];
        System.arraycopy(opData, offset, busername, 0, tmpLength);
        //密码
        offset+=tmpLength;
        tmpLength = opData[offset] & 0xff;
        offset++;
        byte[] bpass = new byte[tmpLength];
        System.arraycopy(opData, offset, bpass, 0, tmpLength);
        //登录IP
        offset += tmpLength;
        tmpLength = opData[offset] & 0xff;
        offset++;
        byte[] bIp = new byte[tmpLength];
        System.arraycopy(opData, offset, bIp, 0, tmpLength);

        //验证登录
        String username = new String(busername);
        String password = new String(bpass);
        String ip = new String(bIp);
        byte loginResult = dbService.GetLoginResult(connection, username, password);
        if((!tconfig.auto_reg) && (loginResult == 9))
            loginResult = 3;
        tTool.mLog(String.format("user [%s] try to login from %s with code %d", username, ip, loginResult));
        //构造回传数据
        byteBuffer.append(usernameLength);
        for(byte bb : busername)
            byteBuffer.append(bb);
        byteBuffer.append(loginResult);
        byteBuffer.trimToSize();
        response.setOpData(byteBuffer.toArray());
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0xA2) & 0xFF).toUpperCase();
    }
}
