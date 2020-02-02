package com.murphdev.keysnewb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity {

    Context context = this;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void playNote(View v) {
        Resources res = context.getResources();
        int test = v.getId();
        String noteId = getResources().getResourceEntryName(test);
        int note = res.getIdentifier(noteId, "raw", context.getPackageName());

        mp = MediaPlayer.create(context, note);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });
        mp.start();
    }
}