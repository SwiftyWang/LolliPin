package com.swifty.handy_passcode_lockscreen;

import android.widget.Toast;

/**
 * Created by swifty on 26/7/2016.
 */
public class HandyCodeLockScreen extends PassCodeLockScreen {
    @Override
    public void showForgotDialog() {
        Toast.makeText(this, getString(R.string.not_implement_it_yet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinFailure(int attempts) {
        Toast.makeText(this, "failed times " + attempts, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinSuccess(int attempts) {
        Toast.makeText(this, "success after " + attempts, Toast.LENGTH_SHORT).show();
    }
}
