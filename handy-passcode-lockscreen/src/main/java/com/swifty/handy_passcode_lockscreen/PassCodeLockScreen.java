package com.swifty.handy_passcode_lockscreen;

import android.animation.Animator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.swifty.handy_passcode_lockscreen.enums.KeyboardButtonEnum;
import com.swifty.handy_passcode_lockscreen.interfaces.KeyboardButtonClickedListener;
import com.swifty.handy_passcode_lockscreen.managers.AppLock;
import com.swifty.handy_passcode_lockscreen.managers.LockManager;
import com.swifty.handy_passcode_lockscreen.views.KeyboardView;
import com.swifty.handy_passcode_lockscreen.views.PinCodeRoundView;

/**
 * Created by stoyan and olivier on 1/13/15.
 * The activity that appears when the password needs to be set or has to be asked.
 * Call this activity in normal or singleTop mode (not singleTask or singleInstance, it does not work
 * with {@link android.app.Activity#startActivityForResult(Intent, int)}).
 */
public abstract class PassCodeLockScreen extends PinLockScreen implements KeyboardButtonClickedListener, View.OnClickListener {

    public static final String TAG = PassCodeLockScreen.class.getSimpleName();
    public static final String ACTION_CANCEL = TAG + ".actionCancelled";
    private static final int DEFAULT_PIN_LENGTH = 4;

    protected TextView mStepTextView;
    protected TextView mForgotTextView;
    protected PinCodeRoundView mPinCodeRoundView;
    protected KeyboardView mKeyboardView;
    protected ImageView mFingerprintImageView;
    protected TextView mFingerprintTextView;

    protected LockManager mLockManager;

    protected int mType = AppLock.UNLOCK_PIN;
    protected int mAttempts = 1;
    protected String mPinCode;

    protected String mOldPinCode;

    private boolean isCodeSuccessful = false;

    View overLay;
    private WindowManager.LayoutParams params;
    private WindowManager wm;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLayout(intent);
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(getContentView());
    }

    private void setContentView(int contentView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) return;
        }
        hideNavigationBar(this);
        if (overLay == null) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    // Keeps the button presses from going to the background window
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            // Enables the notification to recieve touch events
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            // Draws over status bar
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.CENTER;
            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            overLay = inflater.inflate(contentView, null);
            wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            overLay.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            wm.addView(overLay, params);
        }
    }

    protected abstract void showNavigationBar(Context context);

    protected abstract void hideNavigationBar(Context context);

    /**
     * Init completely the layout, depending of the extra {@link com.swifty.handy_passcode_lockscreen.managers.AppLock#EXTRA_TYPE}
     */
    private void initLayout(Intent intent) {
        if (overLay == null) return;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mType = extras.getInt(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
        }

        mLockManager = LockManager.getInstance();
        mPinCode = "";
        mOldPinCode = "";

        enableAppLockerIfDoesNotExist();

        mStepTextView = (TextView) overLay.findViewById(R.id.pin_code_step_textview);
        mPinCodeRoundView = (PinCodeRoundView) overLay.findViewById(R.id.pin_code_round_view);
        mPinCodeRoundView.setPinLength(this.getPinLength());
        mForgotTextView = (TextView) overLay.findViewById(R.id.pin_code_forgot_textview);
        mForgotTextView.setOnClickListener(this);
        mKeyboardView = (KeyboardView) overLay.findViewById(R.id.pin_code_keyboard_view);
        mKeyboardView.setKeyboardButtonClickedListener(this);

        int logoId = mLockManager.getAppLock().getLogoId();
        ImageView logoImage = ((ImageView) overLay.findViewById(R.id.pin_code_logo_imageview));
        if (logoId != AppLock.LOGO_ID_NONE) {
            logoImage.setVisibility(View.VISIBLE);
            logoImage.setImageResource(logoId);
        }
        mForgotTextView.setText(getForgotText());
        mForgotTextView.setVisibility(mLockManager.getAppLock().shouldShowForgot() ? View.VISIBLE : View.GONE);

        setStepText();
    }

    /**
     * Re enable {@link AppLock} if it has been collected to avoid
     * {@link NullPointerException}.
     */
    @SuppressWarnings("unchecked")
    private void enableAppLockerIfDoesNotExist() {
        try {
            if (mLockManager.getAppLock() == null) {
                mLockManager.setupLock(this, getCustomAppLockActivityClass());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Init the {@link #mStepTextView} based on {@link #mType}
     */
    private void setStepText() {
        mStepTextView.setText(getStepText(mType));
    }

    /**
     * Gets the {@link String} to be used in the {@link #mStepTextView} based on {@link #mType}
     *
     * @param reason The {@link #mType} to return a {@link String} for
     * @return The {@link String} for the {@link PassCodeLockScreen}
     */
    public String getStepText(int reason) {
        String msg = null;
        switch (reason) {
            case AppLock.DISABLE_PINLOCK:
                msg = getString(R.string.pin_code_step_disable, this.getPinLength());
                break;
            case AppLock.ENABLE_PINLOCK:
                msg = getString(R.string.pin_code_step_create, this.getPinLength());
                break;
            case AppLock.CHANGE_PIN:
                msg = getString(R.string.pin_code_step_change, this.getPinLength());
                break;
            case AppLock.UNLOCK_PIN:
                msg = getString(R.string.pin_code_step_unlock, this.getPinLength());
                break;
            case AppLock.CONFIRM_PIN:
                msg = getString(R.string.pin_code_step_enable_confirm, this.getPinLength());
                break;
        }
        return msg;
    }

    public String getForgotText() {
        return null;
    }

    /**
     * Overrides to allow a slide_down animation when finishing
     */
    public void finish() {
        stopSelf();
        //If code successful, reset the timer
        if (isCodeSuccessful) {
            if (mLockManager != null) {
                AppLock appLock = mLockManager.getAppLock();
                if (appLock != null) {
                    appLock.setLastActiveMillis();
                }
            }
        }
        showNavigationBar(this);
        overLay.animate().withLayer().alpha(0f).y(overLay.getMeasuredHeight()).setDuration(300).setListener(listener).start();
    }

    Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            //remove overlays
            overLay.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (overLay.isAttachedToWindow()) {
                    wm.removeView(overLay);
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    /**
     * Add the button clicked to {@link #mPinCode} each time.
     * Refreshes also the {@link com.swifty.handy_passcode_lockscreen.views.PinCodeRoundView}
     */
    @Override
    public void onKeyboardClick(KeyboardButtonEnum keyboardButtonEnum) {
        if (mPinCode.length() < this.getPinLength()) {
            int value = keyboardButtonEnum.getButtonValue();

            if (value == KeyboardButtonEnum.BUTTON_CLEAR.getButtonValue()) {
                if (!mPinCode.isEmpty()) {
                    setPinCode(mPinCode.substring(0, mPinCode.length() - 1));
                } else {
                    setPinCode("");
                }
            } else {
                setPinCode(mPinCode + value);
            }
        }
    }

    /**
     * Called at the end of the animation of the {@link com.andexert.library.RippleView}
     * Calls {@link #onPinCodeInputed} when {@link #mPinCode}
     */
    @Override
    public void onRippleAnimationEnd() {
        if (mPinCode.length() == this.getPinLength()) {
            onPinCodeInputed();
        }
    }

    /**
     * Switch over the {@link #mType} to determine if the password is ok, if we should pass to the next step etc...
     */
    protected void onPinCodeInputed() {
        switch (mType) {
            case AppLock.DISABLE_PINLOCK:
                if (mLockManager.getAppLock().checkPasscode(mPinCode)) {
                    mLockManager.getAppLock().setPasscode(null);
                    onPinCodeSuccess();
                    finish();
                } else {
                    onPinCodeError();
                }
                break;
            case AppLock.ENABLE_PINLOCK:
                mOldPinCode = mPinCode;
                setPinCode("");
                mType = AppLock.CONFIRM_PIN;
                setStepText();
                break;
            case AppLock.CONFIRM_PIN:
                if (mPinCode.equals(mOldPinCode)) {
                    mLockManager.getAppLock().setPasscode(mPinCode);
                    onPinCodeSuccess();
                    finish();
                } else {
                    mOldPinCode = "";
                    setPinCode("");
                    mType = AppLock.ENABLE_PINLOCK;
                    setStepText();
                    onPinCodeError();
                }
                break;
            case AppLock.CHANGE_PIN:
                if (mLockManager.getAppLock().checkPasscode(mPinCode)) {
                    mType = AppLock.ENABLE_PINLOCK;
                    setStepText();
                    setPinCode("");
                    onPinCodeSuccess();
                } else {
                    onPinCodeError();
                }
                break;
            case AppLock.UNLOCK_PIN:
                if (mLockManager.getAppLock().checkPasscode(mPinCode)) {
                    onPinCodeSuccess();
                    finish();
                } else {
                    onPinCodeError();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Displays the information dialog when the user clicks the
     * {@link #mForgotTextView}
     */
    public abstract void showForgotDialog();

    /**
     * Run a shake animation when the password is not valid.
     */
    protected void onPinCodeError() {
        onPinFailure(mAttempts++);
        mPinCode = "";
        mPinCodeRoundView.refresh(mPinCode.length());
        Animation animation = AnimationUtils.loadAnimation(
                PassCodeLockScreen.this, R.anim.shake);
        mKeyboardView.startAnimation(animation);
    }

    protected void onPinCodeSuccess() {
        isCodeSuccessful = true;
        onPinSuccess(mAttempts);
        mAttempts = 1;
    }

    /**
     * Set the pincode and refreshes the {@link com.swifty.handy_passcode_lockscreen.views.PinCodeRoundView}
     */
    public void setPinCode(String pinCode) {
        mPinCode = pinCode;
        mPinCodeRoundView.refresh(mPinCode.length());
    }


    /**
     * Returns the type of this {@link PassCodeLockScreen}
     */
    public int getType() {
        return mType;
    }

    /**
     * When we click on the {@link #mForgotTextView} handle the pop-up
     * dialog
     *
     * @param view {@link #mForgotTextView}
     */
    @Override
    public void onClick(View view) {
        showForgotDialog();
    }

    /**
     * When the user has failed a pin challenge
     *
     * @param attempts the number of attempts the user has used
     */
    public abstract void onPinFailure(int attempts);

    /**
     * When the user has succeeded at a pin challenge
     *
     * @param attempts the number of attempts the user had used
     */
    public abstract void onPinSuccess(int attempts);

    /**
     * Gets the resource id to the {@link View} to be set with {@link #setContentView(int)}.
     * The custom layout must include the following:
     * - {@link TextView} with an id of pin_code_step_textview
     * - {@link TextView} with an id of pin_code_forgot_textview
     * - {@link PinCodeRoundView} with an id of pin_code_round_view
     * - {@link KeyboardView} with an id of pin_code_keyboard_view
     *
     * @return the resource id to the {@link View}
     */
    public int getContentView() {
        return R.layout.activity_pin_code;
    }

    /**
     * Gets the number of digits in the pin code.  Subclasses can override this to change the
     * length of the pin.
     *
     * @return the number of digits in the PIN
     */
    public int getPinLength() {
        return PassCodeLockScreen.DEFAULT_PIN_LENGTH;
    }

    /**
     * Get the current class extending {@link PassCodeLockScreen} to re-enable {@link AppLock}
     * in case it has been collected
     *
     * @return the current class extending {@link PassCodeLockScreen}
     */
    public Class<? extends PassCodeLockScreen> getCustomAppLockActivityClass() {
        return this.getClass();
    }
}
