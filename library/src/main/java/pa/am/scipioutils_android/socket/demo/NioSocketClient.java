package pa.am.scipioutils_android.socket.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pa.am.scipioutils_android.socket.NioSocketHandler;
import pa.am.scipioutils_android.socket.NioSocketHandlerImpl;

/**
 * Class: NioSocketClient
 * Description:
 * Author: Alan Min
 * Create Date: 2019/9/16
 */
public class NioSocketClient {

    private static final String CLOSE_CMD="clientClose";

    private boolean isRunning=true;//是否运行
    private boolean isFirstConnect=true;//是否第一次连接服务器

    private Selector selector;//nio选择器
    private NioSocketHandler handler;//nio操作者
    private SocketChannel clientChannel;//该客户端与服务器连接的信道

    private ExecutorService executorService;//线程池
    private NioControlThread controlThread;//控制台子线程

    private String writeMsg;

    //============================================================================

    /**
     * 客户端运行入口
     */
    public void start()
    {
        System.out.println("[客户端]开始运行");
        try {
            init();
            clientMainThread();
        }catch (Exception e){
            if( !(e instanceof ClosedSelectorException) )
                e.printStackTrace();
        }
        if(!executorService.isShutdown())
        {
            System.out.println("[客户端]关闭线程池");
            executorService.shutdownNow();
        }
        System.out.println("[客户端]停止运行,Goodbye :)");
        System.exit(0);
    }

    //============================================================================

    /**
     * 初始化
     */
    private void init() throws IOException
    {
        Scanner scanner=new Scanner(System.in);
        System.out.print("请输入要连接的服务器IP：");
        String host=scanner.next();
        System.out.print("请输入要连接服务器的端口：");
        int port=scanner.nextInt();

        //创建socket信道并连接目标服务器
        clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);//非阻塞模式
        //创建选择器
        selector = Selector.open();
        //将当前信道注册到选择器
        clientChannel.register(selector, SelectionKey.OP_CONNECT);//注册了连接事件
        //开始连接
        clientChannel.connect(new InetSocketAddress(host, port));
        System.out.println("[客户端]开始连接目标服务器");

        handler=new NioSocketHandlerImpl();
        //创建无界缓冲线程池
        executorService= Executors.newCachedThreadPool();
        //创建控制台子线程
        controlThread=new NioControlThread(this);
        controlThread.setOutputPrefix("[客户端]");
        controlThread.setClientCloseCmd(CLOSE_CMD);//定义通知服务器，自己要关闭的字符串
    }

    /**
     * 客户端主子线程
     */
    private void clientMainThread() throws IOException
    {
        while (isRunning)
        {
            selector.select();//阻塞式，如果没有已就绪的事件就阻塞
            //获取已准备就绪的集合
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext())
            {
                SelectionKey key=keyIterator.next();
//                if(!key.isValid())
//                    continue;
                //连接服务器
                if(key.isConnectable())
                {
                    handler.handleConnect(key,"This is first message from client");
                    System.out.println("[客户端]已完成连接服务器");
                    if(isFirstConnect)
                    {
                        executorService.execute(controlThread);
                        isFirstConnect=false;
                    }
                }
                //读取内容（从服务器读）
                if(key.isReadable())
                {
                    String receivedStr=handler.handleRead(key);//读取操作
                    if(receivedStr!=null)
                        System.out.println("[客户端]收到服务器发来的消息："+receivedStr);
                }
                //写入内容（向服务器写）
                if(key.isWritable())
                {
                    if( writeMsg!=null && (!"".equals(writeMsg)) )
                    {
                        handler.handleWrite(key,writeMsg);
                        if( writeMsg.equals(CLOSE_CMD) )
                            close();
                        else
                            System.out.println("[客户端]已发送消息："+writeMsg);
                        writeMsg=null;
                    }
                }
                keyIterator.remove();
            }//end of inside while
        }//end of outside for
        System.out.println("[客户端]退出nio主线程");
    }//end of clientMainThread()

    //=========================================================================

    /**
     * 供控制台子线程修改输入消息
     */
    public void setWriteMsg(String msg) {
        this.writeMsg=msg;
    }

    /**
     * 关闭客户端
     */
    public void close() throws IOException {
        System.out.println("[客户端]执行关闭");
        isRunning=false;
        clientChannel.close();
    }

}
