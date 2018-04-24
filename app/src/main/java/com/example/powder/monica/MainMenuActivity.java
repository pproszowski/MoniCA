package com.example.powder.monica;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 200;
    private static String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Button buttonNew, buttonOpen, buttonExit;
    private static final String recorderName = "AudioRecorder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        buttonNew = findViewById(R.id.button_new);
        buttonOpen = findViewById(R.id.button_open);
        buttonExit = findViewById(R.id.button_exit);

        buttonNew.setOnClickListener(view -> {
            Intent newFolderActivity = new Intent(MainMenuActivity.this, NewMeetingActivity.class);
            newFolderActivity.putExtra("recorderName", "AudioRecorder");
            startActivity(newFolderActivity);

        });

        buttonOpen.setOnClickListener(view -> {
            Intent openExistingMeetingActivity = new Intent(MainMenuActivity.this, OpenExistingMeetingActivity.class);
            openExistingMeetingActivity.putExtra("recorderName", "AudioRecorder");
            startActivity(openExistingMeetingActivity);
        });

        buttonExit.setOnClickListener(view -> {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });





    }



}
