package pa.am.scipioutils_android.socket.listener;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Class: ServerAcceptListener
 * Description:服务器监听客户端连接请求的监听器
 * Author: Alan Min
 * Create Date: 2019/9/18
 */
public interface ServerAcceptListener {

    /**
     * 当监听到客户端请求后（建立连接后，已经注册了读写概念到选择器）
     * @param serverSocketChannelKey 服务器socket的选择键，其内含信道是ServerSocketChannel
     * @param clientChannel 建立连接的客户端信道
     * @param byteBuffer 创建的非直接缓冲区
     */
    void onAccepted(SelectionKey serverSocketChannelKey, SocketChannel clientChannel, ByteBuffer byteBuffer);

}
