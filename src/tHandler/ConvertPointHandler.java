package tHandler;

import com.mysql.jdbc.MySQLConnection;
import com.sun.corba.se.impl.ior.ByteBuffer;
import config.tconfig;
import dataBase.dbService;
import tModel.QueryResult;
import tModel.billingData;
import tModel.userAccount;
import tutil.tTool;

public class ConvertPointHandler extends Handler {
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
        //orderID
        offset += tmpLength;
        byte[] borderId = new byte[21];
        System.arraycopy(opData, offset, borderId, 0, 21);
        //extradata
        offset += 21;
        byte[] bextra = new byte[6];
        System.arraycopy(opData, offset, bextra, 0, 6);
        //跳过本身6字节+兑换点数的后2字节
        offset += 8;
        //获取需要兑换的点数:4u
        int needPoint = 0;
        for(int i = 0; i < 4; i++)
        {
            int tmp = opData[offset] & 0xff;
            offset++;
            if(i < 3)
            {
                tmp = tmp << ((3-i) * 8);
            }
            needPoint += tmp;
        }
        needPoint /= tconfig.transfer_number;
        if(needPoint < 0)
            needPoint = 0;
        int maxPoint = 0xffff;
        int userPoint = 0;
        String username = new String(busername);
        QueryResult qs = dbService.GetAccountByUsername(connection, username);
        if(qs.code == userAccount.QueryCode.UserFound)
        {
            userPoint = qs.acc.getPoint();
            if(userPoint < 0)
                userPoint = 0;
        }

        //最终可兑换的点数
        int realPoint = 0;
        if(needPoint > userPoint)
            realPoint = userPoint;
        else
            realPoint = needPoint;
        //执行兑换
        boolean status = dbService.ConvertUserPoint(connection, username, realPoint);
        if(!status)
        {
            tTool.mLog("!!! error 点数兑换出错！");
        }else{
            tTool.mLog(String.format("user [%s] %s (ip: %s) point total %d, need point %d : %d - %d = %d",
                    username, new String(bcharName), new String(bIp),
                    userPoint, needPoint, userPoint, realPoint, userPoint - realPoint));
        }
        byteBuffer.append(usernameLength);
        for(byte bb : busername)
            byteBuffer.append(bb);
        for(byte bb : borderId)
            byteBuffer.append(bb);
        byte[] tempBytes = new byte[]{0x00, 0x00, 0x00, 0x03, (byte)0xE8};
        for(byte bb : tempBytes)
            byteBuffer.append(bb);
        for(byte bb : bextra)
            byteBuffer.append(bb);
        //点数
        byteBuffer.append((byte)((realPoint & 0xff00) >> 8));
        byteBuffer.append((byte)(realPoint & 0xff));
        byteBuffer.trimToSize();
        response.setOpData(byteBuffer.toArray());
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0xE1) & 0xFF).toUpperCase();
    }
}
