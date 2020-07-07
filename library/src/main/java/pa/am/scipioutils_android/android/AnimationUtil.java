package pa.am.scipioutils_android.android;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import pa.am.scipioutils_android.android.FragmentHelper.OnFragmentInteractionListener;

/**
 * Class: AnimUtil
 * Description: 动画效果工具类
 * 涉及第三方动画库1：https://github.com/XunMengWinter/CircularAnim
 * Author: Alan Min
 * CreateTime:2019/10/5
 */
public class AnimationUtil {

    /**
     * 跳转Activity - 水波动画转场
     * @param originalActivity 原本的Activity
     * @param targetActivity 要跳转到的Activity
     * @param targetView 触发的View
     * @param colorRes 水波颜色（R.color.xxx）
     * @param duration 开始和结束动画的持续时长，单位毫秒(可为null,默认618毫秒)
     * @param animDelayMillis 开始和结束动画间隔时长，单位毫秒(可为null，默认1秒)
     */
    public static void jumpActivity (Activity originalActivity, Class<? extends Activity> targetActivity,
                                     View targetView, int colorRes,
                                     Long duration, Long animDelayMillis)
    {
        CircularAnim.FullActivityBuilder builder = CircularAnim.fullActivity(originalActivity,targetView);
        builder.colorOrImageRes(colorRes);
        if(duration!=null){
            builder.duration(duration);
        }
        if(animDelayMillis!=null){
            builder.setAnimDelayMillis(animDelayMillis);
        }
        builder.go( ()->{
            originalActivity.startActivity(new Intent(originalActivity, targetActivity));
            originalActivity.finish();
        } );
    }

    /**
     * 跳转Activity - 水波动画转场(默认持续时长,200毫秒间隔时长)
     */
    public static void jumpActivity (Activity originalActivity, Class<? extends Activity> targetActivity,
                                     View targetView, int colorRes)
    {
        jumpActivity(originalActivity, targetActivity, targetView, colorRes, null, 200L);
    }

    /**
     * 切换Fragment - 水波动画转场
     * @param activity 原本的Activity
     * @param targetView 触发的View
     * @param colorRes 水波颜色（R.color.xxx）
     * @param listener 跳转监听器
     * @param fragmentId 要跳转到的Fragment的ID（GlobalConst.FRAGMENT_xxx）
     * @param duration 开始和结束动画的持续时长，单位毫秒(可为null,默认618毫秒)
     * @param animDelayMillis 开始和结束动画间隔时长，单位毫秒(可为null，默认1秒)
     */
    public static void switchFragment (Activity activity, View targetView, int colorRes,
                                       OnFragmentInteractionListener listener, int fragmentId,
                                       Long duration, Long animDelayMillis)
    {
        CircularAnim.FullActivityBuilder builder=CircularAnim.fullActivity(activity,targetView);
        builder.colorOrImageRes(colorRes);
        if(duration!=null){
            builder.duration(duration);
        }
        if(animDelayMillis!=null){
            builder.setAnimDelayMillis(animDelayMillis);
        }
        builder.go( ()-> listener.onFragmentSwitch(fragmentId,null));
    }

    /**
     * 切换Fragment - 水波动画转场(默认持续时长,200毫秒间隔时长)
     */
    public static void switchFragment (Activity activity, View targetView, int colorRes,
                                       OnFragmentInteractionListener listener, int fragmentId)
    {
        switchFragment(activity,targetView,colorRes,listener,fragmentId,null,200L);
    }

}
