package com.xyvcard.push.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * @author: WYY
 * @date: 2024/7/8
 * @description：上下文
 */
public class MyContext {
    private static final String TAG = MyContext.class.getSimpleName();

    private Context mContext;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private static class Holder {
        public static MyContext instance = new MyContext();
    }

    public static MyContext getInstance() {
        return Holder.instance;
    }

    private MyContext() {
    }

    public void init(Context context) {
        mContext = context;
    }

    public Context getApplicationContext() {
        return mContext;
    }

    public void runOnMainThread(Runnable runnable) {
        mMainHandler.post(runnable);
    }

    public void runOnMainThread(Runnable runnable, long delay) {
        if (delay == 0) {
            mMainHandler.post(runnable);
        } else {
            mMainHandler.postDelayed(runnable, delay);
        }
    }
}
