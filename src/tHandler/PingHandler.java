package tHandler;

import com.mysql.jdbc.MySQLConnection;
import tModel.billingData;

public class PingHandler extends Handler {
    @Override
    public billingData getResponse(billingData bData, MySQLConnection connection) {
        billingData response = new billingData();
        response.PrePareResponse(bData);
        response.setOpData(new byte[]{(byte)0x01, (byte)0x00});
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte) 0xA1) & 0xFF).toUpperCase();

    }
}
