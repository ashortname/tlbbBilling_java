package tHandler;

import com.mysql.jdbc.MySQLConnection;
import com.sun.corba.se.impl.ior.ByteBuffer;
import tModel.billingData;
import tutil.tTool;

public class CostLogHandler extends Handler {
    @Override
    public billingData getResponse(billingData bData, MySQLConnection connection) {
        billingData response = new billingData();
        response.PrePareResponse(bData);
        ByteBuffer byteBuffer = new ByteBuffer();
        byte[] opData = bData.getOpData();
        byte[] tmp = new byte[21];
        System.arraycopy(opData, 0, tmp, 0, 21);
        for(int i = 0; i < 21; i++)
            byteBuffer.append(opData[i]);
        byteBuffer.append((byte)0x01);
        byteBuffer.trimToSize();
        response.setOpData(byteBuffer.toArray());
        tTool.mLog("CostLog - mSerial key = " + new String(tmp));
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0xC5) & 0xFF).toUpperCase();
    }
}
