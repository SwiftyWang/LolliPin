package com.swifty.handy_passcode_lockscreen;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by swifty on 26/7/2016.
 * this class is just for default test, need extends a lockscreen like this in production
 */
public class DefaultCodeLockScreen extends PassCodeLockScreen {
    @Override
    protected void showNavigationBar(Context context) {

    }

    @Override
    protected void hideNavigationBar(Context context) {

    }

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
