package tHandler;

import com.mysql.jdbc.MySQLConnection;
import com.sun.corba.se.impl.ior.ByteBuffer;
import dataBase.dbService;
import tModel.QueryResult;
import tModel.billingData;
import tModel.userAccount;
import tutil.tTool;
import tutil.OpCodes;
import net.PacketOpCodes;

@OpCodes(PacketOpCodes.QueryPointReq)
public class QueryPointHandler extends Handler {
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
        //登录IP
        offset += tmpLength;
        tmpLength = opData[offset] & 0xff;
        offset++;
        byte[] bIp = new byte[tmpLength];
        System.arraycopy(opData, offset, bIp, 0, tmpLength);
        //角色名
        offset += tmpLength;
        tmpLength = opData[offset] & 0xff;
        offset++;
        byte[] bcharName = new byte[tmpLength];
        System.arraycopy(opData, offset, bcharName, 0, tmpLength);

        //更新在线状态
        String username = new String(busername);
        boolean status = dbService.UpdateOnlineStatus(connection, username, true);
        if(!status)
            tTool.mLog("设置用户 [" + username + "] 在线失败！");
        QueryResult qs = dbService.GetAccountByUsername(connection, username);
        int accPoint = 0;
        if(qs.code == userAccount.QueryCode.UserFound)
        {
            accPoint = (qs.acc.getPoint() + 1) * 1000;
        }
        tTool.mLog(String.format("用户 [%s] 角色 【%s】 查询点数 %d (IP : %s)",
                username, new String(bcharName), qs.acc.getPoint(), new String(bIp)));

        byteBuffer.append(usernameLength);
        for(byte bb : busername)
            byteBuffer.append(bb);
        byte tmp;
        tmp = (byte)(accPoint >> 24);
        byteBuffer.append(tmp);
        tmp = (byte)((accPoint >> 16) & 0xff);
        byteBuffer.append(tmp);
        tmp = (byte)((accPoint >> 8) & 0xff);
        byteBuffer.append(tmp);
        tmp = (byte)(accPoint & 0xff);
        byteBuffer.append(tmp);
        byteBuffer.trimToSize();
        response.setOpData(byteBuffer.toArray());
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0xE2) & 0xFF).toUpperCase();
    }
}
