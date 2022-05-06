package tHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.MySQLConnection;
import config.tconfig;
import tModel.billingData;
import tutil.OpCodes;
import tutil.tTool;
import net.PacketOpCodes;

@OpCodes(PacketOpCodes.NONE)
public class tHandler {
    private static Map<String, Object> handlers = null;

    public static boolean ProcessRequest(billingData request, SocketChannel sc, MySQLConnection connection)
            throws Exception
    {
        if(handlers == null)
            loadHandlers();
        String opt = Integer.toHexString(request.getOpType() & 0xFF).toUpperCase();
        if(!handlers.containsKey(opt))
        {
            //不支持的操作
            tTool.mLog("未知类型的操作请求：" + opt);
            return false;
        }
        if(tTool.DEBUG) {
            System.out.println("\n收到请求 Type：" + billingData.BillingOpt.get(opt));
            System.out.println("收到请求 String：" + new String(request.getOpData()));
            System.out.println("------DEBUG DATA------");
        }
        try{
            Handler handler = (Handler)handlers.get(opt);
            billingData response = handler.getResponse(request, connection);
            if(response.getOpData() == null)
                return false;
            byte[] bresponse = billingData.packData(response);
            ByteBuffer buffer = ByteBuffer.wrap(bresponse);
            sc.write(buffer);
        }catch (IOException ex)
        {
            //回送数据时发生错误
            tTool.mLog("!!! error at ProcessRequest 回传数据时发生错误：" + ex.getMessage());
            throw ex;
        }
        return true;
    }

    /***
     * 加载所有的handler
     */
   private static void loadHandlers()
    {
        handlers = new HashMap<>();
        CloseHandler cloH = new CloseHandler();
        ConnectHandler conH = new ConnectHandler();
        ConvertPointHandler convH = new ConvertPointHandler();
        CostLogHandler costH = new CostLogHandler();
        EnterGameHandler entH = new EnterGameHandler();
        KeepHandler keepH = new KeepHandler();
        KickHandler kickH = new KickHandler();
        LoginHandler logH = new LoginHandler();
        LogoutHandler logoH = new LogoutHandler();
        PingHandler pinH = new PingHandler();
        QueryPointHandler querH = new QueryPointHandler();
        RegisterHandler regH = new RegisterHandler();

        handlers.put(cloH.getType(), cloH);
        handlers.put(conH.getType(), conH);
        handlers.put(costH.getType(), costH);
        handlers.put(entH.getType(), entH);
        handlers.put(keepH.getType(), keepH);
        handlers.put(kickH.getType(), kickH);
        handlers.put(logH.getType(), logH);
        handlers.put(logoH.getType(), logoH);
        handlers.put(pinH.getType(), pinH);
        handlers.put(regH.getType(), regH);
        //是否开启点数兑换功能
        if(tconfig.allow_point)
        {
            handlers.put(convH.getType(), convH);
            handlers.put(querH.getType(), querH);
        }
    }
}
