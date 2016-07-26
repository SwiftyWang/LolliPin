package com.github.orangegangsters.lollipin;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
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
                LockManager.getInstance().setupLock(MainActivity.this);
            }
        });
        findViewById(R.id.button_unlock_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockManager.getInstance().enableLock();
            }
        });
        findViewById(R.id.button_change_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockManager.getInstance().changePin();
            }
        });
    }
}
