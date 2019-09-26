package pa.am.scipioutils_android.socket.listener;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Class: NioSocketReadListener
 * Description: nio的socket读操作的监听器
 * Author: Alan Min
 * Create Date: 2019/9/18
 */
public interface NioSocketReadListener {

    /**
     * 完成读取后的回调
     * @param socketChannelKey 另一端信道的选择键
     * @param byteBuffer 缓冲区
     * @param readMsg 对方发过来的消息，可以改写
     * @param feedbackMsg 之前设置中，设定收到消息立马反馈的文本，为null则不立马反馈
     */
    void onFinishedRead(SelectionKey socketChannelKey, ByteBuffer byteBuffer, String readMsg, String feedbackMsg);

    /**
     * 读取不到内容，关闭SocketChannel时
     * @param socketChannelKey 另一端信道的选择键
     * @param byteBuffer 缓冲区
     */
    void onReadCloseChannel(SelectionKey socketChannelKey, ByteBuffer byteBuffer);

}
