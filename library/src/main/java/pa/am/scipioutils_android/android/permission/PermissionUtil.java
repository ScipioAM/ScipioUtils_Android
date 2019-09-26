package pa.am.scipioutils_android.android.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: PermissionUtil
 * Description: 动态权限请求的内部工具类
 * Author: Alan Min
 * Createtime: 2018/11/29
 */
public class PermissionUtil {

    /**
     * 判断单个权限是否获取
     * @param permission 指定的权限
     */
    public static boolean hasPermission(Context context, String permission)
    {
        //确定Android版本是23及以上的
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //未授权
            return ( context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED );
        }
        return true;
    }

    /**
     * 弹出对话框请求权限
     * @param activity 弹出对话框时，当前的Activity
     * @param permissions 需要请求的权限
     * @param requestCode 请求码，返回结果时需要用到
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, requestCode);
        }
    }

    /**
     * 在指定的权限组里，返回缺失的权限
     * @param permissions 指定需要检查的权限组
     * 如果没有则返回null
     */
    public static String[] getDeniedPermissions(Context context, String[] permissions)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            List<String> deniedPmsList=new ArrayList<>();
            for(String pms:permissions)
            {
                if(!hasPermission(context,pms))
                    deniedPmsList.add(pms);
            }
            if(deniedPmsList.size()>0)
                return deniedPmsList.toArray(new String[deniedPmsList.size()]);
        }
        return null;
    }

}
