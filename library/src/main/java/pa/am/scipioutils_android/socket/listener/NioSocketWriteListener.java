package pa.am.scipioutils_android.socket.listener;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Interface: NioSocketWriteListener
 * Description:nio的socket写操作的监听器
 * Author: Alan Min
 * Create Date: 2019/9/18
 */
public interface NioSocketWriteListener {

    /**
     * 开始写入前
     * @param socketChannelKey 另一端信道的选择键
     * @param byteBuffer 缓冲区
     * @param sendMsg 要写入的数据
     */
    void beforeWrite(SelectionKey socketChannelKey, ByteBuffer byteBuffer, String sendMsg);

}
