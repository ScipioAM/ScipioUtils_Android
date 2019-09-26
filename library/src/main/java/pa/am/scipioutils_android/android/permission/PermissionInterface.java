package pa.am.scipioutils_android.android.permission;

/**
 * Interface: PermissionInterface
 * Description: 动态权限请求接口
 * Author: Alan Min
 * CreateTime: 2018/11/29
 */
public interface PermissionInterface {

    /**
     * 可设置请求权限请求码
     */
    int getPermissionsRequestCode();

    /**
     * 设置需要请求的权限
     */
    String[] getPermissions();

    /**
     * 请求权限成功回调
     */
    void requestPermissionsSuccess();

    /**
     * 请求权限失败回调
     */
    void requestPermissionsFail();

}
