package pa.am.scipioutils_android.socket;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import pa.am.scipioutils_android.socket.listener.ClientConnectListener;
import pa.am.scipioutils_android.socket.listener.NioSocketReadListener;
import pa.am.scipioutils_android.socket.listener.NioSocketWriteListener;
import pa.am.scipioutils_android.socket.listener.ServerAcceptListener;

/**
 * Interface: NioSocketServerHandler
 * Description:
 * Author: Alan Min
 * Create Date: 2019/9/16
 */
public interface NioSocketHandler {

    /**
     * 服务器使用：处理客户端连接请求
     * @param selectionKey 收到连接请求的选择键(包括SocketChannel)(ACCEPT)
     */
    SocketChannel handleAccept(SelectionKey selectionKey) throws IOException;

    /**
     * 客户端使用：处理对服务器的连接操作
     * @param selectionKey 发起连接的选择键(包括SocketChannel)(CONNECT)
     * @param firstMsg 第一次连接要发送的数据，为null则不发送
     */
    void handleConnect(SelectionKey selectionKey, String firstMsg) throws IOException;

    /**
     * 客户端使用：处理对服务器的连接操作，默认在完成连接后不发送消息
     * @param selectionKey 发起连接的选择键(包括SocketChannel)(CONNECT)
     */
    void handleConnect(SelectionKey selectionKey) throws IOException;

    //=========================================================================

    /**
     * 读取另一端发来的字符串数据
     * @param selectionKey 可读取数据的选择键(包括SocketChannel)(READ)
     * @param feedbackMsg 读取到消息后向另一端反馈的文本，为null则不反馈
     * @return 另一端发来的字符串数据
     */
    String handleRead(SelectionKey selectionKey, String feedbackMsg) throws IOException;

    /**
     * 读取另一端发来的字符串数据，默认不反馈消息
     * @param selectionKey 可读取数据的选择键(包括SocketChannel)(READ)
     */
    String handleRead(SelectionKey selectionKey) throws IOException;

    /**
     * 向另一端发送数据
     * @param selectionKey 可写入数据的选择键(包括SocketChannel)(WRITE)
     * @param sendMsg 要发送的数据
     */
    void handleWrite(SelectionKey selectionKey, String sendMsg) throws IOException;

    //=========================================================================

    /**
     * 设置编解码字符集
     */
    void setCharset(String charset);

    /**
     * 设置非直接缓冲区的大小
     */
    void setBufferSize(int bufferSize);

    /**
     * 设置服务器监听连接的监听器
     */
    void setServerAcceptListener(ServerAcceptListener serverAcceptListener);

    /**
     * 设置客户端连接的监听器
     */
    void setClientConnectListener(ClientConnectListener clientConnectListener);

    /**
     * 设置读取操作的监听器
     */
    void setReadListener(NioSocketReadListener readListener);

    /**
     * 设置写入操作的监听器
     */
    void setWriteListener(NioSocketWriteListener writeListener);

}
