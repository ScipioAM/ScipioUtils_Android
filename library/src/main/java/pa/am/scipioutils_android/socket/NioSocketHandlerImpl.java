package pa.am.scipioutils_android.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import pa.am.scipioutils_android.socket.listener.ClientConnectListener;
import pa.am.scipioutils_android.socket.listener.NioSocketReadListener;
import pa.am.scipioutils_android.socket.listener.NioSocketWriteListener;
import pa.am.scipioutils_android.socket.listener.ServerAcceptListener;

/**
 * Class: NioSocketServerHandlerImpl
 * Description:
 * Author: Alan Min
 * Create Date: 2019/9/16
 */
public class NioSocketHandlerImpl implements NioSocketHandler {

    private int BUFFER_SIZE = 1024;//缓冲区大小，默认1024字节
    private String CHARSET = "UTF-8";//编解码字符集，默认UTF-8

    private ServerAcceptListener serverAcceptListener;//服务器监听客户端请求的监听器
    private ClientConnectListener clientConnectListener;//客户端连接监听器
    private NioSocketReadListener readListener;//读取操作的监听器
    private NioSocketWriteListener writeListener;//写入操作的监听器

    //同时注册读和写事件
    private int readAndWriteOps=SelectionKey.OP_READ | SelectionKey.OP_WRITE;

    //=========================================================================

    public NioSocketHandlerImpl(int bufferSize, String charset) {
        this.BUFFER_SIZE = bufferSize>0?bufferSize:this.BUFFER_SIZE;
        this.CHARSET = (charset==null||charset.equals(""))?this.CHARSET:charset;
    }

    public NioSocketHandlerImpl(){}

    public NioSocketHandlerImpl(int bufferSize) {
        this(bufferSize,null);
    }

    public NioSocketHandlerImpl(String charset) {
        this(-1,charset);
    }

    //=========================================================================

    /**
     * 服务器使用：处理客户端连接请求
     * @param selectionKey 信道收到连接请求的key（ACCEPT）
     */
    @Override
    public SocketChannel handleAccept(SelectionKey selectionKey) throws IOException {
        //从选择key里获取信道
        ServerSocketChannel ssChannel = (ServerSocketChannel) selectionKey.channel();
        //监听客户端请求
        SocketChannel socketChannel=ssChannel.accept();//阻塞式，直到收到请求
        socketChannel.configureBlocking(false);//非阻塞
        //创建缓冲区，为后续操作做准备
        ByteBuffer byteBuffer=ByteBuffer.allocate(BUFFER_SIZE);
        //收到请求后，同时注册读写事件
        socketChannel.register( selectionKey.selector() , readAndWriteOps , byteBuffer);
        //监听到后的回调
        if(serverAcceptListener!=null)
        {
            serverAcceptListener.onAccepted(selectionKey,socketChannel,byteBuffer);
        }
        return socketChannel;
    }

    /**
     * 客户端使用：处理对服务器的连接操作
     * @param selectionKey 发起连接的选择键(包括SocketChannel)(CONNECT)
     * @param firstMsg 第一次连接要发送的数据，为null则不发送
     */
    @Override
    public void handleConnect(SelectionKey selectionKey,String firstMsg) throws IOException {
        SocketChannel clientChannel= (SocketChannel) selectionKey.channel();
        //连接时的回调
        if(clientConnectListener!=null)
        {
            clientConnectListener.onConnecting(selectionKey);
        }
        if(clientChannel.isConnectionPending())
        {
            clientChannel.finishConnect();//完成连接工作
            //创建缓冲区，为后续的读取操作做准备
            ByteBuffer byteBuffer=ByteBuffer.allocate(BUFFER_SIZE);
            //连接完成后的回调
            if(clientConnectListener!=null)
            {
                clientConnectListener.onFinishedConnect(selectionKey,byteBuffer,firstMsg);
            }
            //发送第一条信息
            if( firstMsg!=null && (!"".equals(firstMsg)) )
            {
                byteBuffer.put(firstMsg.getBytes());
                byteBuffer.flip();
                clientChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            clientChannel.register(selectionKey.selector(),readAndWriteOps,byteBuffer);//注册读和写事件
        }
        else
        {
            throw new IOException("Connection is unfinished");
        }
    }

    /**
     * 客户端使用：处理对服务器的连接操作，默认在完成连接后不发送消息
     * @param selectionKey 发起连接的选择键(包括SocketChannel)(CONNECT)
     */
    @Override
    public void handleConnect(SelectionKey selectionKey) throws IOException {
        handleConnect(selectionKey,null);
    }

    //=========================================================================

    /**
     * 处理读取另一端发来的字符串数据
     * @param selectionKey 信道可读取数据的key（READ）
     * @param feedbackMsg 读取到消息后向另一端反馈的文本，为null则不反馈
     * @return 另一端发来的字符串数据
     */
    @Override
    public String handleRead(SelectionKey selectionKey,String feedbackMsg) throws IOException {
        SocketChannel socketChannel= (SocketChannel) selectionKey.channel();
        //获取之前创建的缓冲区
        ByteBuffer byteBuffer= (ByteBuffer) selectionKey.attachment();
        byteBuffer.clear();

        String receivedStr=null;
        //开始默认的读取处理
        if( socketChannel.read(byteBuffer)==-1 )//没读到内容，关闭连接
        {
            if(readListener!=null)//关闭时的回调
            {
                readListener.onReadCloseChannel(selectionKey,byteBuffer);
            }
            socketChannel.close();
        }
        else//读取内容
        {
            byteBuffer.flip();//将缓冲区从写模式改为读模式，即从信道读入数据到缓冲区
            //获取字符解码者，并解码出字符串结果
            CharsetDecoder decoder= Charset.forName(CHARSET).newDecoder();
            receivedStr=decoder.decode(byteBuffer).toString();
            byteBuffer.clear();

            //读取完后的回调
            if(readListener!=null)
            {
                readListener.onFinishedRead(selectionKey,byteBuffer,receivedStr,feedbackMsg);
            }

            //向另一端发送反馈文本
            if( (feedbackMsg!=null) && (!"".equals(feedbackMsg)) )
            {
                byteBuffer = byteBuffer.put(feedbackMsg.getBytes(CHARSET));
                byteBuffer.flip();//将缓冲区从读模式改为写模式，即从缓冲区写数据到信道
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
            }
        }
        //注册进选择器，继续读取数据
        if (socketChannel.isOpen())
        {
            socketChannel.register(selectionKey.selector(),readAndWriteOps,byteBuffer);
        }
        return receivedStr;
    }

    /**
     * 处理读取另一端发来的字符串数据，默认不反馈消息
     * @param selectionKey 信道可读取数据的key（READ）
     */
    @Override
    public String handleRead(SelectionKey selectionKey) throws IOException {
        return handleRead(selectionKey,null);
    }

    /**
     * 客户端使用：处理对服务器的连接操作
     * @param selectionKey 发起连接的选择键(包括SocketChannel)(CONNECT)
     */
    @Override
    public void handleWrite(SelectionKey selectionKey, String sendMsg) throws IOException {
        SocketChannel socketChannel= (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer= (ByteBuffer) selectionKey.attachment();//获取之前创建的缓冲区
        byteBuffer.clear();

        if(writeListener!=null)//写入前的回调
        {
            writeListener.beforeWrite(selectionKey,byteBuffer,sendMsg);
        }

        byteBuffer = byteBuffer.put(sendMsg.getBytes(CHARSET));
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();
        if (socketChannel.isOpen())
        {
            socketChannel.register(selectionKey.selector(),readAndWriteOps,byteBuffer);
        }
    }

    //=========================================================================

    /**
     * 设置编解码字符集
     */
    @Override
    public void setCharset(String charset){
        CHARSET=charset;
    }

    /**
     * 设置非直接缓冲区的大小
     */
    @Override
    public void setBufferSize(int bufferSize){
        BUFFER_SIZE=bufferSize;
    }

    /**
     * 设置服务器监听连接的监听器
     */
    @Override
    public void setServerAcceptListener(ServerAcceptListener serverAcceptListener) {
        this.serverAcceptListener = serverAcceptListener;
    }

    /**
     * 设置客户端连接的监听器
     */
    @Override
    public void setClientConnectListener(ClientConnectListener clientConnectListener) {
        this.clientConnectListener = clientConnectListener;
    }

    /**
     * 设置读取操作的监听器
     */
    @Override
    public void setReadListener(NioSocketReadListener readListener) {
        this.readListener = readListener;
    }

    /**
     * 设置写入操作的监听器
     */
    @Override
    public void setWriteListener(NioSocketWriteListener writeListener) {
        this.writeListener = writeListener;
    }

}
