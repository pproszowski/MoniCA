package com.example.powder.monica;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageButton;

public class MainMenuActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 200;
    private static String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String recorderName = "AudioRecorder";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageButton buttonNew = findViewById(R.id.button_new);
        ImageButton buttonOpen = findViewById(R.id.button_open);
        ImageButton buttonSettings = findViewById(R.id.button_settings);

        buttonNew.setOnClickListener(view -> {
            Intent newFolderActivity = new Intent(MainMenuActivity.this, NewMeetingActivity.class);
            newFolderActivity.putExtra("recorderName", recorderName);
            startActivity(newFolderActivity);

        });

        buttonOpen.setOnClickListener(view -> {
            Intent openExistingMeetingActivity = new Intent(MainMenuActivity.this, OpenExistingMeetingActivity.class);
            openExistingMeetingActivity.putExtra("recorderName", recorderName);
            startActivity(openExistingMeetingActivity);
        });

        buttonSettings.setOnClickListener(view -> {
            Intent settingsActivity = new Intent(MainMenuActivity.this, FTPSettingActivity.class);
            settingsActivity.putExtra("recorderName", recorderName);
            startActivity(settingsActivity);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        return super.onKeyDown(keyCode, event);
    }
}
