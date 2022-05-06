package tHandler;

import com.mysql.jdbc.MySQLConnection;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Set;

import net.PacketOpCodes;
import net.PacketOpCodesUtil;
import org.reflections.Reflections;
import tModel.billingData;
import tutil.OpCodes;
import tutil.tTool;

@OpCodes(PacketOpCodes.NONE)
public class tHandlerNew {
    private final Int2ObjectMap<Handler> handlers;

    public tHandlerNew(Class<? extends Handler> handlerClass) {
        this.handlers = new Int2ObjectOpenHashMap<>();

        this.registerHandlers(handlerClass);
    }

    public void registerHandlers(Class<? extends Handler> handlerClass) {
        Reflections reflections = new Reflections("tHandler");
        Set<?> handlerClasses = reflections.getSubTypesOf(handlerClass);

        for (Object obj : handlerClasses) {
            Class<?> c = (Class<?>) obj;

            try {
                OpCodes opcode = c.getAnnotation(OpCodes.class);

                if (opcode == null || opcode.disabled() || opcode.value() < 0) {
                    continue;
                }

                Handler packetHandler = (Handler) c.newInstance();

                this.handlers.put(opcode.value(), packetHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean ProcessRequest(billingData request, SocketChannel sc, MySQLConnection connection)
            throws Exception {
        int opt = request.getOpType() & 0xFF;
        String opName = PacketOpCodesUtil.getOpcodeName(opt);
        if (opName.equals("UNKNOWN")) {
            //不支持的操作
            tTool.mLog(String.format("未知类型的操作请求：%s [%d]", opName, opt));
            return false;
        }
        tTool.mLog(String.format("收到请求：%s[%d]", opName, opt));
        try {
            Handler handler = this.handlers.get(opt);
            billingData response = handler.getResponse(request, connection);
            if(response.getOpData() == null)
                return false;
            byte[] bresponse = billingData.packData(response);
            ByteBuffer buffer = ByteBuffer.wrap(bresponse);
            sc.write(buffer);
        } catch (IOException ex) {
            //回送数据时发生错误
            tTool.mLog("!!! error at ProcessRequest 回传数据时发生错误：" + ex.getMessage());
            throw ex;
        }
        return true;
    }
}
