package tHandler;

import com.mysql.jdbc.MySQLConnection;
import com.sun.corba.se.impl.ior.ByteBuffer;
import tModel.billingData;
import tutil.tTool;

public class KeepHandler extends Handler {
    @Override
    public billingData getResponse(billingData bData, MySQLConnection connection) {
        billingData response = new billingData();
        response.PrePareResponse(bData);
        ByteBuffer byteBuffer = new ByteBuffer();
        byte[] opData = bData.getOpData();

        byte userNameLength = opData[0];
        byte[] busername = new byte[userNameLength & 0xff];
        System.arraycopy(opData, 1, busername, 0, busername.length);

        int offset = (userNameLength & 0xff) + 1;
        int playerLevel = opData[offset] & 0xff;
        offset++;
        playerLevel += opData[offset] & 0xff;

        String username = new String(busername);

        tTool.mLog("keep : user [" + username + "] level " + (playerLevel & 0xff));
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
        return Integer.toHexString(((byte)0xA6) & 0xFF).toUpperCase();
    }
}
