package tHandler;

import com.mysql.jdbc.MySQLConnection;
import tModel.billingData;
import tutil.OpCodes;
import net.PacketOpCodes;

@OpCodes(PacketOpCodes.NONE)
public interface Ihandler{
    public billingData getResponse(billingData bData, MySQLConnection connection);
    public String getType();
}
