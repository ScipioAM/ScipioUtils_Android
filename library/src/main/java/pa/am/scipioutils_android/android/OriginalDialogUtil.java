package pa.am.scipioutils_android.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Class: UavControlThread
 * Description: Dialog封装的工具类
 * Author: Alan Min
 * Createtime: 2018/12/6
 */
public class OriginalDialogUtil {

    private int resStyleId;

    /**
     * 普通对话框的实现方法
     * @param context 上下文
     * @param title 对话框标题
     * @param message 对话框文本
     * @param contentView 对话框内部View
     * @param positiveBtnText 确定按钮文本
     * @param negativeBtnText 取消按钮文本
     * @param positiveCallback 确定按钮点击的回调方法
     * @param negativeCallback 取消按钮点击的回调方法
     * @param cancelable 按返回键是否能退出。默认为true
     */
    private AlertDialog buildDialog(Context context, String title, String message, View contentView,
                                          String positiveBtnText, String negativeBtnText,
                                          DialogInterface.OnClickListener positiveCallback,
                                          DialogInterface.OnClickListener negativeCallback,
                                          Boolean cancelable)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resStyleId);
        builder.setTitle(title == null ? "提示" : title);
        if (message != null) {
            builder.setMessage(message);
        }
        if (contentView != null) {
            builder.setView(contentView);
        }
        if (positiveBtnText != null) {
            builder.setPositiveButton(positiveBtnText, positiveCallback);
        }
        if (negativeBtnText != null) {
            builder.setNegativeButton(negativeBtnText, negativeCallback);
        }
        if (cancelable != null) {
            builder.setCancelable(cancelable);
        }
        else{
            builder.setCancelable(true);
        }
        return builder.show();
    }

    //普通对话框
    public AlertDialog showDialog(Context context, String title, String message,
                                               String positiveBtnText, String negativeBtnText,
                                               DialogInterface.OnClickListener positiveCallback,
                                               DialogInterface.OnClickListener negativeCallback,
                                               Boolean cancelable) {
        return buildDialog(context, title, message, null, positiveBtnText, negativeBtnText, positiveCallback, negativeCallback, cancelable);
    }

    //普通对话框-无点击事件绑定
    public AlertDialog showSimpleDialog(Context context, String title, String message,
                                               String positiveBtnText, String negativeBtnText,
                                               Boolean cancelable)
    {
        return buildDialog(context, title, message, null, positiveBtnText, negativeBtnText, null, null, cancelable);
    }

    public int getResStyleId() {
        return resStyleId;
    }

    public void setResStyleId(int resStyleId) {
        this.resStyleId = resStyleId;
    }
}
