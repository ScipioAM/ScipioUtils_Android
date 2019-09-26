package pa.am.scipioutils_android.android;

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Class: SweetDialogUtil
 * Description: 第三方Dialog的工具类
 * Author: Alan Min
 * Createtime:2018/12/21
 *
 * 源码地址：https://github.com/pedant/sweet-alert-dialog
 * 额外说明：由于作者原工程有点坑，所以需要用其他人修改的工程才行，具体在app的build.gradle里
 * 在dependencies里添加：implementation 'com.github.f0ris.sweetalert:library:1.5.1'
 */
public class SweetDialogUtil {

    /**
     * 创建Dialog
     * @param context Application上下文
     * @param dialogType 对话框类型，通过SweetAlertDialog.XXX_TYPE指定
     * @param title 标题文本
     * @param content 内容文本
     * @param confirmBtnText 确定按钮文本
     * @param cancelBtnText 取消按钮文本
     * @param confirmCallback 确定按钮Click方法
     * @param cancelCallback 取消按钮Click方法
     * @param cancelable 是否可被取消
     * @param iconResId 自定义图标的资源ID
     */
    public static SweetAlertDialog buildDialog(Context context, int dialogType,
                                   String title, String content,
                                   String confirmBtnText, String cancelBtnText,
                                   SweetAlertDialog.OnSweetClickListener confirmCallback,
                                   SweetAlertDialog.OnSweetClickListener cancelCallback,
                                   Boolean cancelable,
                                   Integer iconResId)
    {
        SweetAlertDialog dialog=new SweetAlertDialog(context,dialogType);
        dialog.setTitleText(title);
        if(content!=null)
            dialog.setContentText(content);
        if(confirmBtnText!=null)
            dialog.setConfirmText(confirmBtnText);
        if(confirmCallback!=null)
            dialog.setConfirmClickListener(confirmCallback);
        if(cancelBtnText!=null)
            dialog.setCancelText(cancelBtnText);
        if(cancelCallback!=null)
            dialog.setCancelClickListener(cancelCallback);
        if(iconResId!=null)
            dialog.setCustomImage(iconResId);

        if(cancelable!=null)
            dialog.setCancelable(cancelable);
        else
            dialog.setCancelable(true);
        return dialog;
    }

    /**
     * 单独为Dialog设置点击监听器
     * @param dialog Dialog对象
     * @param confirmCallback 确定按钮点击回调
     * @param cancelCallback 取消按钮点击回调
     */
    public static void setClickListener(SweetAlertDialog dialog,
                                        SweetAlertDialog.OnSweetClickListener confirmCallback,
                                        SweetAlertDialog.OnSweetClickListener cancelCallback)
    {
        dialog.setConfirmClickListener(confirmCallback);
        dialog.setCancelClickListener(cancelCallback);
    }

    /**
     * 显示普通对话框
     */
    public static SweetAlertDialog showBasicDialog(boolean isShow,Context context, String title, String content,
                                      String confirmBtnText, String cancelBtnText,
                                      SweetAlertDialog.OnSweetClickListener confirmCallback,
                                      SweetAlertDialog.OnSweetClickListener cancelCallback)
    {
        SweetAlertDialog dialog=buildDialog(context,SweetAlertDialog.NORMAL_TYPE, title,content,
                confirmBtnText, cancelBtnText,confirmCallback,cancelCallback,true,null);
        if(isShow)
            dialog.show();
        return dialog;
    }

    /**
     * 显示成功对话框
     */
    public static SweetAlertDialog showSuccessDialog(boolean isShow,Context context, String title, String content,
                                       String confirmBtnText, String cancelBtnText,
                                       SweetAlertDialog.OnSweetClickListener confirmCallback,
                                       SweetAlertDialog.OnSweetClickListener cancelCallback)
    {
        SweetAlertDialog dialog=buildDialog(context,SweetAlertDialog.SUCCESS_TYPE, title,content,
                confirmBtnText, cancelBtnText,confirmCallback,cancelCallback,true,null);
        if(isShow)
            dialog.show();
        return dialog;
    }

    /**
     * 显示错误对话框
     */
    public static SweetAlertDialog showErrorDialog(boolean isShow,Context context, String title, String content,
                                         String confirmBtnText, String cancelBtnText,
                                         SweetAlertDialog.OnSweetClickListener confirmCallback,
                                         SweetAlertDialog.OnSweetClickListener cancelCallback)
    {
        SweetAlertDialog dialog=buildDialog(context,SweetAlertDialog.ERROR_TYPE, title,content,
                confirmBtnText, cancelBtnText,confirmCallback,cancelCallback,true,null);
        if(isShow)
            dialog.show();
        return dialog;
    }

    /**
     * 显示警告对话框
     */
    public static SweetAlertDialog showWarningDialog(boolean isShow,Context context, String title, String content,
                                         String confirmBtnText, String cancelBtnText,
                                         SweetAlertDialog.OnSweetClickListener confirmCallback,
                                         SweetAlertDialog.OnSweetClickListener cancelCallback)
    {
        SweetAlertDialog dialog=buildDialog(context,SweetAlertDialog.WARNING_TYPE, title,content,
                confirmBtnText, cancelBtnText,confirmCallback,cancelCallback,true,null);
        if(isShow)
            dialog.show();
        return dialog;
    }

    /**
     * 显示自定义图标的对话框
     */
    public static void showCustomImgDialog(Context context, String title, String content,
                                         int resId,String confirmBtnText, String cancelBtnText,
                                         SweetAlertDialog.OnSweetClickListener confirmCallback,
                                         SweetAlertDialog.OnSweetClickListener cancelCallback)
    {
        SweetAlertDialog dialog=buildDialog(context,SweetAlertDialog.CUSTOM_IMAGE_TYPE, title,content,
                confirmBtnText, cancelBtnText,confirmCallback,cancelCallback,true,resId);
        dialog.show();
    }

    /**
     * 显示进度框
     * @param progressText 进度框文本
     * @param continueMillis 持续时间（为null则不自身定时控制其解散，而是交由其他地方控制）
     */
    public static SweetAlertDialog showProgressDialog(boolean isShow,Context context, String progressText,Long continueMillis)
    {
        final SweetAlertDialog progressDialog=buildDialog(context,SweetAlertDialog.PROGRESS_TYPE,
                progressText,null,null,null,
                null, null,true,null);
        try {
            if(isShow)
            {
                progressDialog.show();
                if(continueMillis!=null)//设置暂停时间
                {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismissWithAnimation();
                        }
                    },continueMillis);
                }//end of if
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return progressDialog;
    }

}
