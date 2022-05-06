package tHandler;

import com.mysql.jdbc.MySQLConnection;
import com.sun.corba.se.impl.ior.ByteBuffer;
import dataBase.dbService;
import tModel.billingData;
import tutil.tTool;
import tutil.OpCodes;
import net.PacketOpCodes;

@OpCodes(PacketOpCodes.UserRegisterReq)
public class RegisterHandler extends Handler {
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
        //超级密码
        offset+=tmpLength;
        tmpLength = opData[offset] & 0xff;
        offset++;
        byte[] bspass = new byte[tmpLength];
        System.arraycopy(opData, offset, bspass, 0, tmpLength);
        //超级密码
        offset+=tmpLength;
        tmpLength = opData[offset] & 0xff;
        offset++;
        byte[] bpass = new byte[tmpLength];
        System.arraycopy(opData, offset, bpass, 0, tmpLength);
        //注册IP
        offset += tmpLength;
        tmpLength = opData[offset] & 0xff;
        offset++;
        byte[] bIp = new byte[tmpLength];
        System.arraycopy(opData, offset, bIp, 0, tmpLength);
        //email
        offset += tmpLength;
        tmpLength = opData[offset] & 0xff;
        offset++;
        byte[] bemail = new byte[tmpLength];
        System.arraycopy(opData, offset, bemail, 0, tmpLength);

        //注册
        String username = new String(busername);
        String spass = new String(bspass);
        String pass = new String(bpass);
        String ip = new String(bIp);
        String email = new String(bemail);

        byte regResult = dbService.GetRegisterResult(connection, username, pass, spass, email);
        if(regResult != 1)
            tTool.mLog("新用户 [" + username + "] 注册失败！");
        else
            tTool.mLog("新用户 [" + username + "] 注册成功！注册IP : " + ip);
        byteBuffer.append(usernameLength);
        for(byte bb : busername)
            byteBuffer.append(bb);
        byteBuffer.append(regResult);
        byteBuffer.trimToSize();
        response.setOpData(byteBuffer.toArray());
        return response;
    }

    @Override
    public String getType() {
        return Integer.toHexString(((byte)0xF1) & 0xFF).toUpperCase();
    }
}
