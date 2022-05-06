import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import com.mysql.jdbc.MySQLConnection;
import config.tconfig;
import dataBase.dbOpenHelper;
import dataBase.dbService;
import tutil.tTool;
import tModel.billingData;
import tModel.billingReadResult;
import tHandler.tHandlerNew;
import tHandler.tHandler;
import net.PacketOpCodes;
import tHandler.Handler;


public class main {
    public static void main(String[] args) {
        if(args.length > 0)
        {
            for (String arg : args
                 ) {
                if(arg.equals("-d")) {
                    tTool.DEBUG = true;
                    System.out.println("===开启了DEBUG模式===");
                }
            }

        }
        MySQLConnection connection = null;
        try{
            System.out.println("\n\n*************************************************************");
            System.out.println("*                        qosj@qq.com                        *");
            System.out.println("*            Welcome to use this billing program            *");
            System.out.println("*     GitHub:https://github.com/ashortname/billing_java     *");
            System.out.println("*************************************************************");
            System.out.println(" ");

            //加载配置
            tconfig.CheckConfig();
            tconfig.loadConfig();
            tTool.mLog("");
            tTool.mLog("-------------------------------------------");
            tTool.mLog(" === 配置文件加载完毕...");

            //连接数据库
             tTool.mLog(" === 正在连接数据库...");
             connection = (MySQLConnection)dbOpenHelper.getConn();
             tTool.mLog(" === 数据库版本：" + connection.getServerVersion());
             //数据库初始化
             tTool.mLog(" === 执行数据库初始化...");
             dbService.appendFields(connection);


            //配置服务端
            tTool.mLog(" ---> 开启监听...");
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(tconfig.ip, tconfig.port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            tTool.mLog(String.format(" ---> 验证服务器开始监听 %s:%d ", tconfig.ip, tconfig.port));
            //保存
            tTool.setServerAndSelector(serverSocketChannel, selector);
            //处理
            tHandlerNew handler = new tHandlerNew(Handler.class);
//            tHandler handler = new tHandlerNew(Handler.class);
            //接收
            while(serverSocketChannel.isOpen())
            {
                int num = selector.select();
                if(num < 1)
                    continue;
                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();
                while (it.hasNext())
                {
                    try{
                        SelectionKey key = (SelectionKey) it.next();
                        if((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT)
                        {
                            ServerSocketChannel serverChanel = (ServerSocketChannel)key.channel();
                            SocketChannel sc = serverChanel.accept();
                            //获取IP并作判断
                            String ip = tTool.getIPAndPORT(sc)[0].substring(1);
                            if(!tTool.isIPAllowed(ip))
                            {
                                tTool.mLog("！！！ 未授权IP：[" + ip + "] 试图连接验证服务器！");
                                sc.close();
                                continue;
                            }
                            sc.configureBlocking( false );
                            // 把新连接注册到选择器
                            SelectionKey newKey = sc.register(selector,
                                    SelectionKey.OP_READ);
                            //设置保持长连接
                            sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                            //it.remove(); //注册完成
                            tTool.mLog("收到来自 " + ip + " 的连接");
                        }else if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ)
                        {
                            SocketChannel sc = (SocketChannel)key.channel(); //获取
                            long bytesRead = 0;
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            if((bytesRead = sc.read(buffer)) > 0 && sc.isOpen())
                            {
                                buffer.flip();
                                byte[] temp = new byte[buffer.limit()];
                                buffer.get(temp, 0, buffer.limit());

                                if(tTool.DEBUG) {
                                    System.out.print("Get data : ");
                                    for (byte tt : temp)
                                        System.out.print("0x" + Integer.toHexString(tt & 0xff).toUpperCase() + " ");
                                    System.out.println(" ");
                                }

                                buffer.clear();
                                billingReadResult bResult = billingData.ReadBillingData(temp);
                                if(bResult.code == billingData.BillingDataCode.BillingDataError)
                                {
                                    tTool.mLog("未知的数据格式");
                                }else if(bResult.code == billingData.BillingDataCode.BillingDataNotFull)
                                {
                                    tTool.mLog("数据长度异常");
                                }else {
                                    //处理数据包
                                    billingData request = bResult.bresult;
                                    //调试
                                    if(tTool.DEBUG) {
                                        System.out.println("\n------DEBUG DATA------");
                                        System.out.print("收到数据：");
                                        for (byte tt : request.getOpData())
                                            System.out.print("0x" + Integer.toHexString(tt & 0xff).toUpperCase() + " ");
                                    }
//                                    tHandler.ProcessRequest(request, sc, connection);
                                    handler.ProcessRequest(request, sc, connection);
                                }
                            }
                            if(bytesRead <= 0)
                            {
                                tTool.mLog(" !!! IP：[" + tTool.getIPAndPORT(sc)[0] + "] 断开连接！");
                                sc.close();
                            }
                        }else {
                            System.out.println("====Else====");
                        }
                    }catch (Exception ex)
                    {
                        //ex.printStackTrace();
                        System.out.println(ex.getMessage());
                    }finally {
                        //移除
                        it.remove();
                    }
                }
           }
        }catch (Exception ex)
        {
            //tTool.mLog("Error : " + ex.getMessage());
            System.out.println("！！！ 错误退出...");
            return;
        }finally {
           dbOpenHelper.closeAll(connection);
           tTool.closeSever();
       }

        tTool.mLog(" ---> 正常退出...");
    }
}
