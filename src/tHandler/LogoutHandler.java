package tHandler;

import com.mysql.jdbc.MySQLConnection;
import com.sun.corba.se.impl.ior.ByteBuffer;
import dataBase.dbService;
import tModel.billingData;
import tutil.tTool;

public class LogoutHandler extends Handler {
    @Override
    public billingData getResponse(billingData bData, MySQLConnection connection) {
        billingData response = new billingData();
        response.PrePareResponse(bData);
        ByteBuffer byteBuffer = new ByteBuffer();
        byte[] opData = bData.getOpData();
        int offset = 0;
        byte userNameLength = opData[offset];
        int tmpLength = userNameLength & 0xff;
        offset++;
        byte[] busername = new byte[tmpLength];
        System.arraycopy(opData, offset, busername, 0, tmpLength);

        String username = new String(busername);

        boolean status = dbService.UpdateOnlineStatus(connection, username, false);
        if(!status)
        {
            tTool.mLog("!!! error set " + username + " to offline failed!");
        }else{
            tTool.mLog("user [" + username + "] logout game");
        }
        byteBuffer.append(userNameLength);
        for(byte bb : busername)
            byteBuffer.append(bb);
        byteBuffer.append((byte)0x01);
        byteBuffer.trimToSize();
        response.setOpData(byteBuffer.toArray());
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0xA4) & 0xFF).toUpperCase();
    }
}
