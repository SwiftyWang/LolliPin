package com.github.orangegangsters.lollipin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.swifty.handy_passcode_lockscreen.managers.LockManager;

import lollipin.orangegangsters.github.com.lollipin.R;

/**
 * Created by swifty on 26/7/2016.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AudioManager mode = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        findViewById(R.id.button_enable_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockManager.getInstance().setupLock(MainActivity.this.getApplicationContext(), null);
            }
        });
        findViewById(R.id.button_unlock_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockManager.getInstance().enableLock(MainActivity.this.getApplicationContext(), null);
            }
        });
        findViewById(R.id.button_change_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockManager.getInstance().changePin(MainActivity.this.getApplicationContext(), null);
            }
        });
        checkPermission();
    }

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    finish();
                }
            }
        }
    }
}
