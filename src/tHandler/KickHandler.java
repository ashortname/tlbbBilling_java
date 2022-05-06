package tHandler;

import com.mysql.jdbc.MySQLConnection;
import tModel.billingData;
import tutil.OpCodes;
import net.PacketOpCodes;

@OpCodes(PacketOpCodes.KickNotify)
public class KickHandler extends Handler {
    @Override
    public billingData getResponse(billingData bData, MySQLConnection connection) {
        billingData response = new billingData();
        response.PrePareResponse(bData);
        response.setOpData(new byte[]{(byte)0x01});
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0xA9) & 0xFF).toUpperCase();
    }
}
