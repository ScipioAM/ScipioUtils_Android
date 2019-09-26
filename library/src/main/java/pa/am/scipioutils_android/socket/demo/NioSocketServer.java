package pa.am.scipioutils_android.socket.demo;

import android.annotation.TargetApi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pa.am.scipioutils_android.socket.NioSocketHandler;
import pa.am.scipioutils_android.socket.NioSocketHandlerImpl;
import pa.am.scipioutils_android.socket.listener.ServerAcceptListener;

/**
 * Class: NioSocketServer
 * Description:
 * Author: Alan Min
 * Create Date: 2019/9/16
 */
public class NioSocketServer {

    private boolean isRunning=true;//是否运行
    private boolean isFirstAccept=true;//是否第一次监听客户端请求

    private Selector selector;//nio选择器
    private NioSocketHandler handler;//nio相关操作处理者
    private ExecutorService executorService;//线程池
    private NioControlThread controlThread;

    private String writeMsg;//要输出的消息

    //=========================================================================

    /**
     * 服务器运行入口
     */
    public void start()
    {
        System.out.println("[服务器]开始运行");
        try {
            init();//初始化
            serverMainThread();//运行主线程
        }catch (Exception e){
            if( !(e instanceof ClosedSelectorException) )
                e.printStackTrace();
        }
        if(!executorService.isShutdown())
        {
            System.out.println("[服务器]关闭线程池");
            executorService.shutdownNow();
        }
        System.out.println("[服务器]停止运行,Goodbye :)");
        System.exit(0);
    }//end of start()

    //=========================================================================

    /**
     * 初始化
     */
    @TargetApi(24)
    private void init() throws IOException
    {
        Scanner scanner=new Scanner(System.in);
        System.out.print("请输入要监听的端口：");
        int port=scanner.nextInt();

        //创建服务器socket信道并绑定监听端口
        ServerSocketChannel ssChannel=ServerSocketChannel.open();
        ssChannel.configureBlocking(false);//非阻塞模式
        ssChannel.bind(new InetSocketAddress(port));
        //创建选择器
        selector=Selector.open();
        //将通道注册到选择器上
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);//注册选择器要监听的操作：监听客户端请求（accept）

        //创建消息处理者
        handler=new NioSocketHandlerImpl();//默认缓冲区大小为1024
        //创建无界缓冲线程池
        executorService= Executors.newCachedThreadPool();
        //创建控制台子线程
        controlThread=new NioControlThread(this);
        controlThread.setOutputPrefix("[服务器]");

        //设置收到客户端连接后的回调处理
        handler.setServerAcceptListener(new ServerAcceptListener() {
            @Override
            public void onAccepted(SelectionKey serverSocketChannelKey, SocketChannel clientChannel, ByteBuffer byteBuffer) {
                try {
                    System.out.println("[服务器]客户端的请求已建立,IP为：" + clientChannel.getRemoteAddress());
                    if(isFirstAccept)
                    {
                        executorService.execute(controlThread);//启动控制台子线程
                        isFirstAccept=false;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 服务器主线程
     */
    private void serverMainThread() throws IOException {
        System.out.println("[服务器]开始监听客户端请求... ...");
        while (isRunning)
        {
            selector.select();
            Iterator<SelectionKey> keyIterator=selector.selectedKeys().iterator();//获取已就绪的键
            handleSelectedKeys(keyIterator);//处理已就绪的键
        }//end of outside while
        System.out.println("[服务器]退出nio主线程");
    }

    /**
     * 对已就绪键的处理
     */
    @TargetApi(24)
    private void handleSelectedKeys(Iterator<SelectionKey> keyIterator)
    {
        while (keyIterator.hasNext())
        {
            SelectionKey key = keyIterator.next();
            try {
                if(!key.isValid())
                    continue;
                //可以开始接收客户端请求
                if (key.isAcceptable()) {
                    handler.handleAccept(key);//监听操作
                }
                //可以从客户端读取数据
                if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    //读取操作
                    String receivedStr = handler.handleRead(key, "received,time:" + System.currentTimeMillis());
                    if (receivedStr != null)
                    {
                        if( receivedStr.equals("clientClose") )
                            System.out.println("[服务器]IP为{" + socketChannel.getRemoteAddress() + "}的客户端已下线");
                        else
                            System.out.println("[服务器]收到客户端IP为{" + socketChannel.getRemoteAddress() + "}发来的消息：" + receivedStr);
                    }
                }
                if(key.isWritable())
                {
                    if( writeMsg!=null && (!"".equals(writeMsg)) )
                    {
                        handler.handleWrite(key,writeMsg);
                        System.out.println("[服务器]已发送消息："+writeMsg);
                        writeMsg=null;
                    }
                }
            }catch (Exception e){
                key.cancel();
                if( !(e instanceof CancelledKeyException) )
                    e.printStackTrace();
            }
            keyIterator.remove();//处理完后移除当前使用的key
        }//end of inside while
    }

    //=========================================================================

    /**
     * 供控制台子线程修改输入消息
     */
    public void setWriteMsg(String msg) {
        this.writeMsg=msg;
    }

    /**
     * 关闭服务器
     */
    public void close() throws IOException {
        isRunning=false;
        //把键依次取消掉
        Set<SelectionKey> keySet=selector.selectedKeys();
        for( SelectionKey key : keySet )
        {
            key.cancel();
        }
        selector.close();
    }

}
