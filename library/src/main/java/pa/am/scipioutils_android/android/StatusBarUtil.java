package pa.am.scipioutils_android.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;

/**
 * Class: StatusBarHelper
 * Description:
 * Author: Alan Min
 * CreateTime:2020/7/5
 */
public class StatusBarUtil {

    /**
     * 沉浸式状态栏
     * @param color Android5的版本时，指定的颜色
     */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(19)
    public static void translucent(Activity activity, @ColorInt int color) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            // 版本小于4.4，绝对不考虑沉浸式
            return;
        }

        Window window = activity.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    /**
     * 获取状态栏高度
     * @param context 上下文对象（通常是Activity）
     * @return 状态栏的像素高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

}
