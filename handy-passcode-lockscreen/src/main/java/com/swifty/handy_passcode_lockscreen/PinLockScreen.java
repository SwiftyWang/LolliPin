package com.swifty.handy_passcode_lockscreen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by stoyan and olivier on 1/12/15.
 * You must extend this Activity in order to support this library.
 * Then to enable PinCode blocking, you must call
 */
public class PinLockScreen extends Service {
    private final BroadcastReceiver mPinCancelledReceiver;

    public PinLockScreen() {
        super();
        mPinCancelledReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopSelf();
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(PassCodeLockScreen.ACTION_CANCEL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mPinCancelledReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPinCancelledReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
