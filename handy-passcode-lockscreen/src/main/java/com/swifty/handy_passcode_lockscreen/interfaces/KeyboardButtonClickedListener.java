package com.swifty.handy_passcode_lockscreen.interfaces;


import com.swifty.handy_passcode_lockscreen.enums.KeyboardButtonEnum;

/**
 * Created by stoyan and oliviergoutay on 1/13/15.
 * The {@link com.swifty.handy_passcode_lockscreen.PassCodeLockScreen} will implement
 * this in order to receive events from {@link com.swifty.handy_passcode_lockscreen.views.KeyboardButtonView}
 * and {@link com.swifty.handy_passcode_lockscreen.views.KeyboardView}
 */
public interface KeyboardButtonClickedListener {

    /**
     * Receive the click of a button, just after a {@link android.view.View.OnClickListener} has fired.
     * Called before {@link #onRippleAnimationEnd()}.
     * @param keyboardButtonEnum The organized enum of the clicked button
     */
    public void onKeyboardClick(KeyboardButtonEnum keyboardButtonEnum);

    /**
     * Receive the end of a {@link com.andexert.library.RippleView} animation using a
     * {@link com.andexert.library.RippleAnimationListener} to determine the end.
     * Called after {@link #onKeyboardClick(KeyboardButtonEnum)}.
     */
    public void onRippleAnimationEnd();

}
