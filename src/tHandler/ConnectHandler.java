package tHandler;

import com.mysql.jdbc.MySQLConnection;
import tModel.billingData;
import tutil.OpCodes;
import net.PacketOpCodes;

@OpCodes(PacketOpCodes.ConnectReq)
public class ConnectHandler extends Handler {
    @Override
    public billingData getResponse(billingData bData, MySQLConnection connection) {
        billingData response = new billingData();
        response.PrePareResponse(bData);
        response.setOpData(new byte[]{(byte)0x20, (byte)0x00});
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0xA0) & 0xFF).toUpperCase();
    }
}
