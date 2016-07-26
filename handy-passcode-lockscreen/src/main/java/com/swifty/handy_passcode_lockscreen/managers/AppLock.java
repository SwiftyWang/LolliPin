package com.swifty.handy_passcode_lockscreen.managers;

import android.app.Activity;


import com.swifty.handy_passcode_lockscreen.PassCodeLockScreen;

import java.util.HashSet;

public abstract class AppLock {
    /**
     * ENABLE_PINLOCK type, uses at firt to define the password
     */
    public static final int ENABLE_PINLOCK = 0;
    /**
     * DISABLE_PINLOCK type, uses to disable the system by asking the current password
     */
    public static final int DISABLE_PINLOCK = 1;
    /**
     * CHANGE_PIN type, uses to change the current password
     */
    public static final int CHANGE_PIN = 2;
    /**
     * CONFIRM_PIN type, used to confirm the new password
     */
    public static final int CONFIRM_PIN = 3;
    /**
     * UNLOCK_PIN type, uses to ask the password to the user, in order to unlock the app
     */
    public static final int UNLOCK_PIN = 4;

    /**
     * LOGO_ID_NONE used to denote when a user has not set a logoId using {@link #setLogoId(int)}
     */
    public static final int LOGO_ID_NONE = -1;

    /**
     * EXTRA_TYPE, uses to pass to the {@link PassCodeLockScreen}
     * to determine in which type it musts be started.
     */
    public static final String EXTRA_TYPE = "type";

    /**
     * DEFAULT_TIMEOUT, define the default timeout returned by {@link #getTimeout()}.
     * If you want to modify it, you can call {@link #setTimeout(long)}. Will be stored using
     * {@link android.content.SharedPreferences}
     */
    public static final long DEFAULT_TIMEOUT = 1000 * 10; // 10sec

    public AppLock() {
    }

    /**
     * Get the timeout used in {@link #shouldLockSceen(Activity)}
     */
    public abstract long getTimeout();

    /**
     * Set the timeout used in {@link #shouldLockSceen(Activity)}
     */
    public abstract void setTimeout(long timeout);

    /**
     * Get logo resource id used by {@link PassCodeLockScreen}
     */
    public abstract int getLogoId();

    /**
     * Set logo resource id used by {@link PassCodeLockScreen}
     */
    public abstract void setLogoId(int logoId);

    /**
     * Get the forgot option used by {@link PassCodeLockScreen}
     */
    public abstract boolean shouldShowForgot();

    /**
     * Set the forgot option used by {@link PassCodeLockScreen}
     */
    public abstract void setShouldShowForgot(boolean showForgot);

    /**
     * Get the only background timeout option used to determine if the time
     * spent in the activity must NOT be taken into account while calculating the timeout.
     */
    public abstract boolean onlyBackgroundTimeout();

    /**
     * Set whether the time spent on the activity must NOT be taken into account when calculating timeout.
     */
    public abstract void setOnlyBackgroundTimeout(boolean onlyBackgroundTimeout);

    /**
     * Enable the {@link com.swifty.handy_passcode_lockscreen.managers.AppLock} by setting
     * {@link com.swifty.handy_passcode_lockscreen.managers.AppLockImpl} as the
     * {@link com.swifty.handy_passcode_lockscreen.interfaces.LifeCycleInterface}
     */
    public abstract void setupPin();

    public abstract void changePin();

    public abstract void enable();

    /**
     * Disable the {@link com.swifty.handy_passcode_lockscreen.managers.AppLock} by removing any
     * {@link com.swifty.handy_passcode_lockscreen.interfaces.LifeCycleInterface}
     */
    public abstract void disable();

    /**
     * Disable the {@link com.swifty.handy_passcode_lockscreen.managers.AppLock} by removing any
     * {@link com.swifty.handy_passcode_lockscreen.interfaces.LifeCycleInterface} and also delete
     * all the previous saved configurations into {@link android.content.SharedPreferences}
     */
    public abstract void disableAndRemoveConfiguration();

    public abstract long getLastActiveMillis();

    /**
     * Set in {@link com.swifty.handy_passcode_lockscreen.interfaces.LifeCycleInterface#onLockCodeDismiss(Activity)}
     * and {@link com.swifty.handy_passcode_lockscreen.interfaces.LifeCycleInterface#onLockCodeShow(Activity)}
     */
    public abstract void setLastActiveMillis();

    /**
     * Set the passcode (store his SHA1 into {@link android.content.SharedPreferences}) using the
     * {@link com.swifty.handy_passcode_lockscreen.encryption.Encryptor} class.
     */
    public abstract boolean setPasscode(String passcode);

    /**
     * Check the passcode by comparing his SHA1 into {@link android.content.SharedPreferences} using the
     * {@link com.swifty.handy_passcode_lockscreen.encryption.Encryptor} class.
     */
    public abstract boolean checkPasscode(String passcode);

    /**
     * Check the {@link android.content.SharedPreferences} to see if a password already exists
     */
    public abstract boolean isPasscodeSet();
}
