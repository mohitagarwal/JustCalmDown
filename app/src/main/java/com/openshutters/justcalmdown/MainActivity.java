package com.openshutters.justcalmdown;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int DELAY = 500;
    private int GAP_BETWEEN_EACH_IMAGE = 1000;
    private int IMAGE_COUNT = 10;

    List<Long> vibrationPattern = Arrays.asList(
            500L, 500L,          // pattern for inhaling 4000
            0L, 1000L,
            0L, 1000L,
            0L, 1000L,

            1000L, 0L,           // small pause

            900L, 100L,           // pattern for exhaling 6000
            900L, 100L,
            900L, 100L,
            900L, 100L,
            900L, 100L);

    int[] images = {
            R.drawable.image0,
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7,
            R.drawable.image8,
            R.drawable.image9
    };

    private ImageView gif;

    private MyHandler handler;
    private Timer _timer;

    private int _index;
    private int timeElapsed = 0;

    boolean isPlaying = false;

    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setupUsingGIF(vibrator);
        setupUsingAnimation(vibrator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAnimation();
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

    private void setupUsingGIF(Vibrator vibrator) {
        /*GifImageButton gif = (GifImageButton) findViewById(R.id.main_gif);
        final GifDrawable gifDrawable = (GifDrawable) gif.getDrawable();

        final MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(gifDrawable);
        mediaController.setAnchorView(gif);

        if(vibrator.hasVibrator()){
            vibrator.vibrate(vibrationPattern, -1);
        }

        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gifDrawable.isPlaying()){
                    gifDrawable.pause();
                } else {
                    gifDrawable.start();
                }
            }
        });*/
    }

    private void setupUsingAnimation(Vibrator vibrator) {
        gif = (ImageView) findViewById(R.id.main_gif);

        handler = new MyHandler();

        _index = 0;

        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    stopAnimation();
                } else {
                    startAnimation(_index);
                }
            }
        });
    }

    private void stopAnimation() {
        if (_timer != null) {
            _timer.cancel();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        isPlaying = false;
    }

    private void startAnimation(int index) {
        isPlaying = true;

        if (vibrator.hasVibrator()) {
            long[] pattern = modifyVibrationPatternBasedOnIndex(_index);
            vibrator.vibrate(pattern, 0);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                _timer = new Timer();
                _timer.schedule(new TickClass(), DELAY, GAP_BETWEEN_EACH_IMAGE);
            }
        }, DELAY);
    }

    private long[] modifyVibrationPatternBasedOnIndex(int index) {
        List<Long> copiedList = new ArrayList<>(vibrationPattern);
        Collections.rotate(copiedList, 2 * index);
        Long[] newPattern = new Long[vibrationPattern.size()];
        copiedList.toArray(newPattern);
        return ArrayUtils.toPrimitive(newPattern);
    }

    private class TickClass extends TimerTask {
        @Override
        public void run() {
            if (_index >= 10) {
                _index = 0;
            }
            handler.sendEmptyMessage(_index);
            _index++;
            timeElapsed++;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                Bitmap bmp = BitmapFactory.decodeStream(MainActivity.this.getAssets().open("image" + _index + ".png"));
                gif.setImageBitmap(bmp);

                Log.v("Loading Image: ", _index + "");
            } catch (IOException e) {
                Log.v("Exception in Handler ", e.getMessage());
            }
        }
    }
}
