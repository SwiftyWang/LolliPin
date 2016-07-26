package com.swifty.handy_passcode_lockscreen.interfaces;

import android.app.Activity;

import com.swifty.handy_passcode_lockscreen.PinLockScreen;
import com.swifty.handy_passcode_lockscreen.PassCodeLockScreen;

/**
 * Created by stoyan on 1/12/15.
 * Allows to follow the LifeCycle of the {@link PinLockScreen}
 * Implemented by {@link com.swifty.handy_passcode_lockscreen.managers.AppLockImpl} in order to
 * determine when the app was launched for the last time and when to launch the
 * {@link PassCodeLockScreen}
 */
public interface LifeCycleInterface {

    /**
     * Called in {@link Activity#onResume()}
     */
    public void onLockCodeShow(Activity activity);

    /**
     * Called in {@link Activity#onPause()}
     */
    public void onLockCodeDismiss(Activity activity);
}
