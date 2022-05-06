package tHandler;

import com.mysql.jdbc.MySQLConnection;
import dataBase.dbOpenHelper;
import tModel.billingData;
import tutil.tTool;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import tutil.OpCodes;
import net.PacketOpCodes;

@OpCodes(PacketOpCodes.CloseReq)
public class CloseHandler extends Handler {
    @Override
    public billingData getResponse(billingData bData, MySQLConnection connection) {
        billingData response = new billingData();
        response.PrePareResponse(bData);
        response.setOpData(new byte[]{0, 0});
        try{
            Iterator it = tTool.getKeys();
            if(it != null)
            {
                while(it.hasNext())
                {
                    SelectionKey key = (SelectionKey) it.next();
                    SocketChannel sc = (SocketChannel)key.channel();
                    try{
                        sc.close();
                    }catch (Exception ex)
                    {
                        continue;
                    }
                    it.remove();
                }
            }
        }catch (Exception ex)
        {

        }
        tTool.closeSever();
        dbOpenHelper.closeAll(connection);
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0x00) & 0xFF).toUpperCase();
    }
}
