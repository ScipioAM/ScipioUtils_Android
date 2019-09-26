package pa.am.scipioutils_android.android;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Class: FragmentHelper
 * Description:
 * Author: Alan Min
 * CreateTime:2019/9/25
 */
public class FragmentHelper {

    private Fragment currentFragment;

    private FragmentManager fragmentManager;//FragmentManager对象，可通过Activity的getSupportFragmentManager()方法获取

    public FragmentHelper(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * 动态切换
     * @param targetFragment 要切换到的Fragment
     * @param layoutId 父layout的id
     */
    public void switchFragment(@NonNull Fragment targetFragment,
        int layoutId)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!targetFragment.isAdded())
        {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null)
            {
                transaction.hide(currentFragment);
            }
            transaction.add(layoutId, targetFragment,targetFragment.getClass().getName());
        }
        else
        {
            transaction.hide(currentFragment);
            transaction.show(targetFragment);
        }
        currentFragment = targetFragment;//修改当前Fragment的指向
        transaction.commit();
    }

    /**
     * Fragment与Activity交流的监听器，由Activity实现，Fragment调用
     */
    public interface OnFragmentInteractionListener{

        /**
         * Fragment与Activity通信,要切换Fragment时的回调方法
         * @param changeFragmentId 要切换的目标FragmentId，可以是自定义常量，也可以是R里的id
         * @param parameter 携带额外的参数（可为null）
         */
        void onFragmentSwitch(int changeFragmentId, Object parameter);
    }

}
