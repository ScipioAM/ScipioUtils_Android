package pa.am.scipioutils_android.http;

import java.io.IOException;

/**
 * Interface: UploadListener
 * Description: 文件上传时的监听器
 * Author: Alan Min
 * Create Date: 2019/9/3
 */
public interface UploadListener {

    /**
     * 上传开始时
     */
    void onStart();

    /**
     * 上传时
     * @param percentage 百分比进度(0-1)
     */
    void onProcess(double percentage);

    /**
     * 上传完成时
     */
    void onCompleted();

    /**
     * 上传出错时
     * @param e IO异常对象
     */
    void onError(IOException e);

}
