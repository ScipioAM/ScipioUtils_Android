package pa.am.scipioutils_android.android;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by AlanMin on 2017-12-05.
 */
public class SysApplication extends Application {
    private List<Activity> mList = new LinkedList<>();

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    // exit Application
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
