package com.swifty.handy_passcode_lockscreen.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.swifty.handy_passcode_lockscreen.DefaultCodeLockScreen;
import com.swifty.handy_passcode_lockscreen.PassCodeLockScreen;
import com.swifty.handy_passcode_lockscreen.encryption.Encryptor;
import com.swifty.handy_passcode_lockscreen.enums.Algorithm;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by swifty on 26/7/2016.
 */
public class AppLockImpl<T extends PassCodeLockScreen> extends AppLock {

    private static final String LAST_ACTIVE_MILLIS_PREFERENCE_KEY = "LAST_ACTIVE_MILLIS_PREFERENCE_KEY";
    /**
     * The {@link android.content.SharedPreferences} key used to store the password
     */
    private static final String PASSWORD_PREFERENCE_KEY = "PASSCODE";
    /**
     * The {@link android.content.SharedPreferences} key used to store the {@link Algorithm}
     */
    private static final String PASSWORD_ALGORITHM_PREFERENCE_KEY = "ALGORITHM";

    /**
     * The {@link android.content.SharedPreferences} key used to store the timeout
     */
    private static final String TIMEOUT_MILLIS_PREFERENCE_KEY = "TIMEOUT_MILLIS_PREFERENCE_KEY";
    /**
     * The {@link android.content.SharedPreferences} key used to store the logo resource id
     */
    private static final String LOGO_ID_PREFERENCE_KEY = "LOGO_ID_PREFERENCE_KEY";
    /**
     * The {@link android.content.SharedPreferences} key used to store the forgot option
     */
    private static final String SHOW_FORGOT_PREFERENCE_KEY = "SHOW_FORGOT_PREFERENCE_KEY";

    /**
     * The {@link android.content.SharedPreferences} key used to store the only background timeout option
     */
    private static final String ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY = "ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY";

    /**
     * The {@link android.content.SharedPreferences} key used to store the dynamically generated password salt
     */
    private static final String PASSWORD_SALT_PREFERENCE_KEY = "PASSWORD_SALT_PREFERENCE_KEY";
    /**
     * The {@link SharedPreferences} key used to store whether the caller has enabled fingerprint authentication.
     * This value defaults to true for backwards compatibility.
     */
    private static final String FINGERPRINT_AUTH_ENABLED_PREFERENCE_KEY = "FINGERPRINT_AUTH_ENABLED_PREFERENCE_KEY";
    /**
     * The default password salt
     */
    private static final String DEFAULT_PASSWORD_SALT = "7xn7@c$";
    /**
     * The key algorithm used to generating the dynamic salt
     */
    private static final String KEY_ALGORITHM = "PBEWithMD5AndDES";
    /**
     * The key length of the salt
     */
    private static final int KEY_LENGTH = 256;
    /**
     * The number of iterations used to generate a dynamic salt
     */
    private static final int KEY_ITERATIONS = 20;

    private static AppLockImpl mInstance;
    private final Context context;
    private final SharedPreferences mSharedPreferences;
    private Intent lockScreenIntent;
    private final Class<T> lockScreen;

    private AppLockImpl(Context context, Class<T> lockScreen) {
        super();
        this.lockScreen = lockScreen;
        this.context = context;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static AppLockImpl getInstance(Context context, Class<? extends PassCodeLockScreen> tClass) {
        synchronized (LockManager.class) {
            if (mInstance == null) {
                if(tClass ==null) tClass = DefaultCodeLockScreen.class;
                mInstance = new AppLockImpl<>(context, tClass);
            }
        }
        return mInstance;
    }

    @Override
    public long getTimeout() {
        return mSharedPreferences.getLong(TIMEOUT_MILLIS_PREFERENCE_KEY, DEFAULT_TIMEOUT);
    }

    @Override
    public void setTimeout(long timeout) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(TIMEOUT_MILLIS_PREFERENCE_KEY, timeout);
        editor.apply();
    }

    @Override
    public int getLogoId() {
        return mSharedPreferences.getInt(LOGO_ID_PREFERENCE_KEY, LOGO_ID_NONE);
    }

    @Override
    public void setLogoId(int logoId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(LOGO_ID_PREFERENCE_KEY, logoId);
        editor.apply();
    }

    @Override
    public boolean shouldShowForgot() {
        return mSharedPreferences.getBoolean(SHOW_FORGOT_PREFERENCE_KEY, false);
    }

    @Override
    public void setShouldShowForgot(boolean showForgot) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(SHOW_FORGOT_PREFERENCE_KEY, showForgot);
        editor.apply();
    }

    @Override
    public boolean onlyBackgroundTimeout() {
        return mSharedPreferences.getBoolean(ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY, false);
    }

    @Override
    public void setOnlyBackgroundTimeout(boolean onlyBackgroundTimeout) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY, onlyBackgroundTimeout);
        editor.apply();
    }

    @Override
    public void setupPin() {
        lockScreenIntent = new Intent(context, lockScreen);
        lockScreenIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
        context.startService(lockScreenIntent);
    }

    @Override
    public void changePin() {
        lockScreenIntent = new Intent(context, lockScreen);
        lockScreenIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.CHANGE_PIN);
        context.startService(lockScreenIntent);
    }

    @Override
    public void enable() {
        lockScreenIntent = new Intent(context, lockScreen);
        lockScreenIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
        context.startService(lockScreenIntent);
    }

    @Override
    public void disable() {
        if (lockScreenIntent != null)
            context.stopService(lockScreenIntent);
    }

    @Override
    public void disableAndRemoveConfiguration() {
        mSharedPreferences.edit().remove(PASSWORD_PREFERENCE_KEY)
                .remove(LAST_ACTIVE_MILLIS_PREFERENCE_KEY)
                .remove(PASSWORD_ALGORITHM_PREFERENCE_KEY)
                .remove(TIMEOUT_MILLIS_PREFERENCE_KEY)
                .remove(LOGO_ID_PREFERENCE_KEY)
                .remove(SHOW_FORGOT_PREFERENCE_KEY)
                .remove(FINGERPRINT_AUTH_ENABLED_PREFERENCE_KEY)
                .remove(ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY)
                .apply();
    }

    @Override
    public long getLastActiveMillis() {
        return mSharedPreferences.getLong(LAST_ACTIVE_MILLIS_PREFERENCE_KEY, 0);
    }

    @Override
    public void setLastActiveMillis() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(LAST_ACTIVE_MILLIS_PREFERENCE_KEY, System.currentTimeMillis());
        editor.apply();
    }

    public String getSalt() {
        String salt = mSharedPreferences.getString(PASSWORD_SALT_PREFERENCE_KEY, null);
        if (salt == null) {
            salt = generateSalt();
            setSalt(salt);
        }
        return salt;
    }

    private void setSalt(String salt) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PASSWORD_SALT_PREFERENCE_KEY, salt);
        editor.apply();
    }

    private String generateSalt() {
        byte[] salt = new byte[KEY_LENGTH];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(System.currentTimeMillis() + new Random().nextInt());
            sr.nextBytes(salt);
            return Arrays.toString(salt);
        } catch (Exception e) {
            salt = DEFAULT_PASSWORD_SALT.getBytes();
        }
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    /**
     * Set the algorithm used in {@link #setPasscode(String)}
     */
    private void setAlgorithm(Algorithm algorithm) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PASSWORD_ALGORITHM_PREFERENCE_KEY, algorithm.getValue());
        editor.apply();
    }

    @Override
    public boolean setPasscode(String passcode) {
        String salt = getSalt();
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if (passcode == null) {
            editor.remove(PASSWORD_PREFERENCE_KEY);
            editor.apply();
            this.disable();
        } else {
            passcode = salt + passcode + salt;
            setAlgorithm(Algorithm.SHA256);
            passcode = Encryptor.getSHA(passcode, Algorithm.SHA256);
            editor.putString(PASSWORD_PREFERENCE_KEY, passcode);
            editor.apply();
            this.enable();
        }

        return true;
    }

    @Override
    public boolean checkPasscode(String passcode) {
        Algorithm algorithm = Algorithm.getFromText(mSharedPreferences.getString(PASSWORD_ALGORITHM_PREFERENCE_KEY, ""));

        String salt = getSalt();
        passcode = salt + passcode + salt;
        passcode = Encryptor.getSHA(passcode, algorithm);
        String storedPasscode = "";

        if (mSharedPreferences.contains(PASSWORD_PREFERENCE_KEY)) {
            storedPasscode = mSharedPreferences.getString(PASSWORD_PREFERENCE_KEY, "");
        }

        if (storedPasscode.equalsIgnoreCase(passcode)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isPasscodeSet() {
        if (mSharedPreferences.contains(PASSWORD_PREFERENCE_KEY)) {
            return true;
        }
        return false;
    }
}
