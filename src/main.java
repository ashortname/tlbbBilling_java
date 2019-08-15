import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.mysql.jdbc.MySQLConnection;
import config.tconfig;
import dataBase.dbOpenHelper;
import dataBase.dbService;
import tutil.tTool;
import tModel.billingData;
import tModel.billingReadResult;

public class main {
    public static void main(String[] args) {
        MySQLConnection connection = null;
        try{
            System.out.println("\n\n*************************************************************");
            System.out.println("*             Welcome to use this billing service           *");
            System.out.println("*************************************************************");

            //加载配置
            tTool.mLog(" === 开始加载配置文件...");
            tconfig.loadConfig();
            tTool.mLog(" === 配置文件加载完毕...");

            //连接数据库
             tTool.mLog(" === 开始连接数据库...");
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
            tTool.mLog(String.format(" ---> Server start at %s:%d ", tconfig.ip, tconfig.port));
            //保存
            tTool.server = serverSocketChannel;
            tTool.selector = selector;

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
                            String ip = sc.getRemoteAddress().toString().split(":")[0].substring(1);
                            if(!tTool.isIPAllowed(ip))
                            {
                                tTool.mLog("IP " + ip + " is not allowed here.");
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
                            boolean bb = sc.socket().getKeepAlive();
                            long bytesRead = 0;
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            if((bytesRead = sc.read(buffer)) > 0 && sc.isOpen())
                            {
                                buffer.flip();
                                byte[] temp = new byte[buffer.limit()];
                                buffer.get(temp, 0, buffer.limit());

                                /*System.out.print("Get data : ");
                                for(byte tt:temp)
                                    System.out.print("0x"+Integer.toHexString(tt & 0xff).toUpperCase() + " ");
                                System.out.println(" ");*/

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
                                    tHandler.tHandler.ProcessRequest(request, sc, connection);
                                }
                            }
                        }else {
                            System.out.println("====Else====");
                        }
                    }catch (Exception ex)
                    {
                        System.out.println("!!! error " + ex.getMessage());
                        continue;
                    }finally {
                        //移除
                        it.remove();
                    }
                }
           }
        }catch (Exception ex)
        {
            tTool.mLog("Error : " + ex.getMessage());
            System.out.println("退出...");
        }finally {
           dbOpenHelper.closeAll(connection);
       }

        tTool.mLog(" ---> 正常退出...");
    }
}
