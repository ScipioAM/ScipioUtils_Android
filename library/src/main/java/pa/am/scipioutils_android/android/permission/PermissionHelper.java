package pa.am.scipioutils_android.android.permission;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

/**
 * Class: PermissionHelper
 * Description: 动态权限请求的工具类
 * Author: Alan Min
 * Createtime: 2018/11/29
 */
public class PermissionHelper {

    private Activity activity;
    private PermissionInterface pmsInterface;

    public PermissionHelper(@NonNull Activity activity, @NonNull PermissionInterface pmsInterface)
    {
        this.activity=activity;
        this.pmsInterface=pmsInterface;
    }

    /**
     * 申请权限
     * 方法内部已经对Android M 或以上版本进行了判断，外部使用不再需要重复判断。
     * 如果设备还不是M或以上版本，则也会回调到requestPermissionsSuccess方法。
     */
    public void requestPermissions()
    {
        //获取需要申请的权限
        String[] deniedPermissions=PermissionUtil.getDeniedPermissions(activity,pmsInterface.getPermissions());
        if(deniedPermissions!=null && deniedPermissions.length>0)//开始申请权限
            PermissionUtil.requestPermissions(activity,deniedPermissions,pmsInterface.getPermissionsRequestCode());
        else//请求成功
            pmsInterface.requestPermissionsSuccess();
    }

    /**
     * 处理授权（或拒绝授权）后的操作
     * 在Activity中的onRequestPermissionsResult中调用
     * @param requestCode 自定义请求码
     * @param permissions 需要授权的权限组
     * @param grantResults 操作后的结果集
     */
    public boolean requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode==pmsInterface.getPermissionsRequestCode())
        {
            boolean isAllGranted = true;//是否已全部授权
            for(int result:grantResults)
            {
                if(result==PackageManager.PERMISSION_DENIED)
                {
                    isAllGranted=false;
                    break;
                }
            }//end of for
            if(isAllGranted)
                pmsInterface.requestPermissionsSuccess();
            else
                pmsInterface.requestPermissionsFail();
            return true;
        }
        return false;
    }

}
