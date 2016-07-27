package com.swifty.handy_passcode_lockscreen.managers;

import android.content.Context;

import com.swifty.handy_passcode_lockscreen.DefaultCodeLockScreen;
import com.swifty.handy_passcode_lockscreen.PinLockScreen;
import com.swifty.handy_passcode_lockscreen.PassCodeLockScreen;

/**
 * Allows to handle the {@link com.swifty.handy_passcode_lockscreen.managers.AppLock} from within
 * the actual app calling the library.
 * You must get this static instance by calling {@link #getInstance()}
 */
public class LockManager<T extends PassCodeLockScreen> {

    /**
     * The static singleton instance
     */
    private static LockManager mInstance;
    /**
     * The static singleton instance of {@link com.swifty.handy_passcode_lockscreen.managers.AppLock}
     */
    private static AppLock mAppLocker;

    /**
     * Used to retrieve the static instance
     */
    public static LockManager getInstance() {
        synchronized (LockManager.class) {
            if (mInstance == null) {
                mInstance = new LockManager<>();
            }
        }
        return mInstance;
    }

    public void changePin(Context applicationContext, Class<T> tClass) {
        if (mAppLocker != null) {
            mAppLocker.disable();
        }
        mAppLocker = AppLockImpl.getInstance(applicationContext, tClass);
        mAppLocker.changePin();
    }


    /**
     * setup a new lock.
     */
    public void setupLock(Context applicationContext, Class<T> tClass) {
        if (mAppLocker != null) {
            mAppLocker.disable();
        }
        mAppLocker = AppLockImpl.getInstance(applicationContext, tClass);
        mAppLocker.setupPin();
    }

    /**
     * You must call that into your custom {@link android.app.Application} to enable the
     * {@link PinLockScreen}
     */
    public void enableLock(Context applicationContext, Class<T> tClass) {
        if (mAppLocker != null) {
            mAppLocker.disable();
        }
        mAppLocker = AppLockImpl.getInstance(applicationContext, tClass);
        mAppLocker.enable();
    }

    /**
     * Disables the app lock by calling {@link AppLock#disable()}
     */
    public void disableAppLock() {
        if (mAppLocker != null) {
            mAppLocker.disable();
        }
        mAppLocker = null;
    }

    /**
     * Disables the previous app lock and set a new one
     */
    public void setAppLock(AppLock appLocker) {
        if (mAppLocker != null) {
            mAppLocker.disable();
        }
        mAppLocker = appLocker;
    }

    /**
     * Get the {@link AppLock}. Used for defining custom timeouts etc...
     */
    public AppLock getAppLock() {
        return mAppLocker;
    }
}
