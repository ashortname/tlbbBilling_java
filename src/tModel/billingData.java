package tModel;

import com.sun.corba.se.impl.ior.ByteBuffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class billingData {
    /***
     * 解析数据结果的返回值
     */
    public enum BillingDataCode{
        //读取数据包成功
       BillingReadOk,
        //数据包不完整
        BillingDataNotFull,
        //数据包格式错误
        BillingDataError
    }

    private byte OpType;
    private byte[] MsgID;
    private byte[] OpData;

    public billingData()
    {
        MsgID = new byte[2];
        OpData = null;
    }

    public static byte[] packData(billingData bData)
            throws UnsupportedEncodingException
    {
        ByteBuffer byteBuffer = new ByteBuffer();
        byteBuffer.append((byte)0xAA);
        byteBuffer.append((byte)0x55);

        int lengthP = 3 + bData.getOpData().length;
        byte tmpByte;
        //高8位
        tmpByte = (byte)(lengthP >> 8);
        byteBuffer.append(tmpByte);
        //低8位
        tmpByte = (byte)(lengthP & 0xFF);
        byteBuffer.append(tmpByte);
        //append data
        byteBuffer.append(bData.getOpType());
        byteBuffer.append(bData.getMsgID()[0]);
        byteBuffer.append(bData.getMsgID()[1]);

        if(lengthP > 3)
        {
            for(byte bb : bData.getOpData())
                byteBuffer.append(bb);
        }
        byteBuffer.append((byte)0x55);
        byteBuffer.append((byte)0xAA);

        //裁剪
        byteBuffer.trimToSize();
        return byteBuffer.toArray();
    }

    /***
     * 准备工作
     * @param bData
     */
    public void PrePareResponse(billingData bData)
    {
        setOpType(bData.getOpType());
        setMsgID(bData.getMsgID());
    }

    /***
     * 解析数据
     * @param binaryData
     * @return
     * @throws IOException
     */
    public static billingReadResult ReadBillingData(byte[] binaryData)
            throws IOException
    {
        billingData bData = new billingData();
        int packLength = 0;
        byte[] maskData = {(byte)0xAA, (byte)0x55};
        int binaryDataLength = binaryData.length;
        if(binaryDataLength < 9)
        {
            //数据报总长度小于9
            billingReadResult result = new billingReadResult(bData, BillingDataCode.BillingDataNotFull, packLength);
            return result;
        }
        if((binaryData[0] != maskData[0]) && (binaryData[1] != maskData[1]))
        {
            // 头部数据错误
            billingReadResult result = new billingReadResult(bData, BillingDataCode.BillingDataError, packLength);
            return result;
        }
        int binaryData1 = (binaryData[2] & 0xff);
        int binaryData2 = (binaryData[3] & 0xff);
        int opDataLength = ( binaryData1 << 8) + binaryData2 - 3;
        //System.out.println(String.format("opDataLength: %d, binarydate: %d %d\n", opDataLength, binaryData1, binaryData2));
        packLength = opDataLength + 9;
        if(binaryDataLength < packLength)
        {
            //数据报总长度小于9
            billingReadResult result = new billingReadResult(bData, BillingDataCode.BillingDataNotFull, packLength);
            return result;
        }
        if((binaryData[packLength - 2] != maskData[1]) && (binaryData[packLength - 1] != maskData[0]))
        {
            // 尾部数据错误
            billingReadResult result = new billingReadResult(bData, BillingDataCode.BillingDataError, packLength);
            return result;
        }
        bData.setOpType(binaryData[4]);
        bData.setMsgID(new byte[]{binaryData[5], binaryData[6]});
        if(opDataLength > 0)
        {
            byte[] temp = new byte[opDataLength];
            System.arraycopy(binaryData, 7, temp, 0, temp.length);
            bData.setOpData(temp);
        }
        billingReadResult result = new billingReadResult(bData, BillingDataCode.BillingReadOk, packLength);
        return result;
    }

    public byte getOpType() {
        return OpType;
    }

    public void setOpType(byte opType) {
        OpType = opType;
    }

    public byte[] getMsgID() {
        return MsgID;
    }

    public void setMsgID(byte[] msgID) {
        MsgID = msgID;
    }

    public byte[] getOpData() {
        return OpData;
    }

    public void setOpData(byte[] opData) {
        OpData = opData;
    }
}
