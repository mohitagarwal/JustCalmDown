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
import android.widget.TextView;

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
    private TextView timeLimitText;
    private View playControlButton;
    private Chronometer chronometer;
    private long elapsedTimeBeforePause;

    private int prefTimeLimitInSeconds;
    private String prefTimeLimitDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.slide_in_up, android.R.anim.fade_out);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timeLimitText = (TextView) findViewById(R.id.current_time_limit);
        playControlButton = findViewById(R.id.button_play);

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

        setupUsingAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeSettings();
        timeLimitText.setText("Current time limit is set to " + prefTimeLimitDisplay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAnimation();
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
        if (id == R.id.action_reset) {
            stopAnimation();
            return true;
        } else if (id == R.id.action_about) {
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
        prefTimeLimitInSeconds = Integer.valueOf(prfs.getString(getString(R.string.pref_time), getString(R.string.pref_time_default_value)));
        String[] choices = getResources().getStringArray(R.array.pref_time_array);
        String[] choicesKey = getResources().getStringArray(R.array.pref_time_values);
        int i;
        for (i = 0; i < choicesKey.length; i++) {
            if (choicesKey[i].equals(String.valueOf(prefTimeLimitInSeconds))) {
                break;
            }
        }
        prefTimeLimitDisplay = choices[i];
    }

    private void setupUsingAnimation() {
        gif = (ImageView) findViewById(R.id.main_gif);

        handler = new MyHandler();

        _index = 0;

        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrPauseAnimation();
            }
        });
        playControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrPauseAnimation();
            }
        });
    }

    private void playOrPauseAnimation() {
        if (isPlaying) {
            pauseAnimation();
        } else {
            startAnimation();
        }
    }

    private void pauseAnimation() {
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
        playControlButton.setVisibility(View.VISIBLE);
    }

    private void stopAnimation() {
        if (_timer != null) {
            _timer.cancel();
        }
        _index = 0;
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (chronometer != null) {
            chronometer.stop();
            elapsedTimeBeforePause = 0;
            chronometer.setBase(SystemClock.elapsedRealtime());
        }
        isPlaying = false;
        playControlButton.setVisibility(View.VISIBLE);
    }

    private void startAnimation() {
        initializeSettings();
        isPlaying = true;
        playControlButton.setVisibility(View.GONE);

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
