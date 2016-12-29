package com.openshutters.justcalmdown;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int DELAY = 0;
    private int GAP_BETWEEN_EACH_IMAGE = 1000;

    long[] vibrationPattern = {
            900L, 100L,          // pattern for inhaling 4000
            0L, 1000L,
            0L, 1000L,
            0L, 1000L,

            1000L, 0L,           // small pause

            900L, 100L,           // pattern for exhaling 6000
            900L, 100L,
            900L, 100L,
            900L, 100L,
            900L, 100L,
            1000L, 0L};

    private MyHandler handler;
    private Timer _timer;
    private int _index;
    boolean isPlaying = false;

    Vibrator vibrator;

    private ImageView gif;
    private Chronometer chronometer;
    private long elapsedTimeBeforePause;

    private int prefTimeLimitInSeconds;
    private boolean prefVibrateOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        initializeSettings();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chronometer = (Chronometer) findViewById(R.id.timer);

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long countUp = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                if (countUp >= prefTimeLimitInSeconds) {
                    stopAnimation();
                }
            }
        });

        setupUsingAnimation(vibrator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        resetAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeSettings() {
        SharedPreferences prfs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefTimeLimitInSeconds = Integer.valueOf(prfs.getString("pref_time", "60"));
        prefVibrateOnly = prfs.getBoolean("pref_vibrate", false);
    }

    private void setupUsingAnimation(Vibrator vibrator) {
        gif = (ImageView) findViewById(R.id.main_gif);

        handler = new MyHandler();

        _index = 0;

        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    resetAnimation();
                } else {
                    startAnimation(_index);
                }
            }
        });
    }

    private void resetAnimation() {
        if (_timer != null) {
            _timer.cancel();
            _index = 0;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (chronometer != null && isPlaying) {
            chronometer.stop();
            elapsedTimeBeforePause = SystemClock.elapsedRealtime() - chronometer.getBase();
        }
        isPlaying = false;
    }

    private void stopAnimation() {
        if (_timer != null) {
            _timer.cancel();
            _index = 0;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (chronometer != null) {
            chronometer.stop();
            elapsedTimeBeforePause = SystemClock.elapsedRealtime();
        }
        isPlaying = false;
    }

    private void startAnimation(int index) {
        initializeSettings();
        isPlaying = true;

        _timer = new Timer();
        _timer.schedule(new TickClass(), DELAY, GAP_BETWEEN_EACH_IMAGE);

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationPattern, 0);
        }

        chronometer.setBase(SystemClock.elapsedRealtime() - elapsedTimeBeforePause);
        chronometer.start();
    }

    private class TickClass extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(_index);
            if (_index >= 10) {
                _index = -1;
            }
            _index++;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                Bitmap bmp = BitmapFactory.decodeStream(MainActivity.this.getAssets().open("image" + msg.what + ".png"));
                gif.setImageBitmap(bmp);

                Log.v("Loading Image: ", msg.what + "");
            } catch (IOException e) {
                Log.v("Exception in Handler ", e.getMessage());
            }
        }
    }
}
