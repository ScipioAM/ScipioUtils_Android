package pa.am.scipioutils_android.socket.listener;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Interface: ClientConnectListener
 * Description:客户端连接监听器
 * Author: Alan Min
 * Create Date: 2019/9/18
 */
public interface ClientConnectListener {

    /**
     * 客户端开始连接，但还未完成连接时的回调
     * @param socketChannelKey 连接信道的选择键
     */
    void onConnecting(SelectionKey socketChannelKey);

    /**
     * 客户端完成连接后的回调
     * @param socketChannelKey 连接信道的选择键
     * @param byteBuffer 对信道进行读写的非直接缓冲区
     * @param firstMsg 建立连接后立马发送的第一条消息，为null则不发送
     */
    void onFinishedConnect(SelectionKey socketChannelKey, ByteBuffer byteBuffer, String firstMsg);

}
