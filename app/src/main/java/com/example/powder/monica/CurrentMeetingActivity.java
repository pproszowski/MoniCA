package com.example.powder.monica;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.powder.monica.storage.StorageActivity;


public class CurrentMeetingActivity extends AppCompatActivity {
    private ImageButton recordButton;
    private TextView recordingStatus;
    private TextView sizeText;
    private TextView sizeOfSelectedItemsText;
    private TextView willNotText;
    private TextView couldText;
    private TextView shouldText;
    private TextView mustText;
    private TextView meetingNameText;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private String recorderName;
    private String meetingName = "";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private static final int output_formats[] = {MediaRecorder.OutputFormat.DEFAULT, MediaRecorder.OutputFormat.THREE_GPP};
    private static final String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};
    private String recordedFileName;
    private double size;
    private String mailSubject;
    private List<String> checkedFileNames = new ArrayList<>();
    private static final int GET_CHECKED_FILE_NAMES = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private String choosenPriority = "WillNot ";
    private String path;
    private double sizeSelectedItems;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ftp_setting, menu);
        //inflater.inflate(R.menu.user_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ftp_settings_action_bar: {


                startActivity(new Intent(CurrentMeetingActivity.this, FTPSettingActivity.class));
                return true;
            }

            case R.id.user_settings_action_bar: {
                //TODO: create UserSettingActivity.class to connect here;
//                startActivity(new Intent(this, UserSettingActivity.class));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_meeting);
        recorderName = getIntent().getExtras().getString("recorderName");
        meetingName = getIntent().getExtras().getString("Name");
        mailSubject = getIntent().getExtras().getString("mailSubject");
        recordButton = findViewById(R.id.recordButton);
        recordingStatus = findViewById(R.id.textView);
        sizeText = findViewById(R.id.sizeText);
        willNotText = findViewById(R.id.willNotText);
        couldText = findViewById(R.id.couldText);
        shouldText = findViewById(R.id.shouldText);
        mustText = findViewById(R.id.mustText);
        meetingNameText = (TextView) findViewById(R.id.meetingNameField);
        meetingNameText.setText(meetingName);
        SeekBar priorityBar = findViewById(R.id.priorityBar);
        SharedPreferences sharedPref = getSharedPreferences("defaultFTP.xml", 0);

        willNotText.setTypeface(null, Typeface.BOLD);
        willNotText.setTextSize(16);


        recordButton.setOnTouchListener((v, event) -> {
            recordButton.performClick();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    recordingStatus.setText(R.string.recording);
                    ((ImageButton) v).setImageResource(R.drawable.ic_mic_red);
                    AppLog.logString("Start Recording");
                    startRecording();
                    break;
                case MotionEvent.ACTION_UP:
                    AppLog.logString("Stop Recording");
                    ((ImageButton) v).setImageResource(R.drawable.ic_mic_gray);
                    stopRecording();
                    break;
            }
            return false;
        });

        priorityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                setPriority(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    protected void onResume() {
        super.onResume();

        size = 0;
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName;

        sizeSelectedItems = 0;
        File directory = new File(path);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            if (checkedFileNames.contains(file.getName())) {
                sizeSelectedItems += file.length();
            }
            if (!"email.txt".equals(file.getName())) {
                size += file.length();
            }
        }
        if(size <= 1048576){
            sizeText.setText(String.format("Size : %.2fKB", size / 1024));
        }
        else {
            sizeText.setText(String.format("Size : %.2fMB", size / 1048576));
        }
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, recorderName + "/" + meetingName);

        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + choosenPriority + "Rec "
                + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "꞉"
                + Calendar.getInstance().get(Calendar.MINUTE) + "꞉"
                + Calendar.getInstance().get(Calendar.SECOND)
                + file_exts[currentFormat]);

    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recordedFileName = getFilename();
        recorder.setOutputFile(recordedFileName);
        AppLog.logString("I CREATE: " + recordedFileName);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = (mr, what, extra) -> AppLog.logString("Error: " + what + ", " + extra);

    private MediaRecorder.OnInfoListener infoListener = (mr, what, extra) -> AppLog.logString("Warning: " + what + ", " + extra);

    private void stopRecording() {
        try {
            recorder.stop();
            size += new File(recordedFileName).length();
            recordingStatus.setText("Record saved!" );
            if(size <= 1048576){
                sizeText.setText(String.format("Size : %.2fKB", size / 1024));
            }
            else {
                sizeText.setText(String.format("Size : %.2fMB", size / 1048576));
            }
        } catch (RuntimeException e) {
            recordingStatus.setText(R.string.record_too_short);
            File file = new File(recordedFileName);
            file.delete();
        }

        if (recorder != null) {
            recorder.reset();
            recorder.release();

            recorder = null;
        }
    }


    public void goToStorage(View view) {
        Intent intent = new Intent(this, StorageActivity.class);
        intent.putExtra("Name", meetingName);
        intent.putExtra("sizeSelectedItems", sizeSelectedItems);
        intent.putExtra("recorderName", recorderName);
        intent.putExtra("meetingName", meetingName);
        intent.putExtra("mailSubject", mailSubject);
        if (checkedFileNames != null && !checkedFileNames.isEmpty()) {
            intent.putStringArrayListExtra("checkedFileNames", (ArrayList<String>) checkedFileNames);
        }
        startActivityForResult(intent, GET_CHECKED_FILE_NAMES);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (requestCode == GET_CHECKED_FILE_NAMES && resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        checkedFileNames = data.getStringArrayListExtra("checkedFileNames");
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
            }


            }
    }

    public void makePhoto(View view) {

        Intent photoIntent = new Intent(this, MakePhoto.class);
        photoIntent.putExtra("Name", meetingName);
        photoIntent.putExtra("recorderName", recorderName);
        photoIntent.putExtra("choosenPriority", choosenPriority);
        startActivityForResult(photoIntent, REQUEST_TAKE_PHOTO);

    }

    public void setPriority(int progress) {
        switch (progress) {
            case 0:
                setChosenPriority(willNotText, couldText, shouldText, mustText);
                choosenPriority = "WillNot ";
                break;

            case 1:
                setChosenPriority(couldText, willNotText, shouldText, mustText);
                choosenPriority = "Could ";
                break;

            case 2:
                setChosenPriority(shouldText, mustText, couldText, willNotText);
                choosenPriority = "Should ";
                break;

            case 3:
                setChosenPriority(mustText, shouldText, willNotText, couldText);
                choosenPriority = "Must ";
                break;
            default:
                break;
        }
    }

    private void setChosenPriority(TextView chosenPriority, TextView a, TextView b, TextView c){
        chosenPriority.setTypeface(null, Typeface.BOLD);
        chosenPriority.setTextSize(16);

        a.setTypeface(null, Typeface.NORMAL);
        a.setTextSize(14);
        b.setTypeface(null, Typeface.NORMAL);
        b.setTextSize(14);
        c.setTypeface(null, Typeface.NORMAL);
        c.setTextSize(14);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent openExistingMeetingIntent = new Intent(this, OpenExistingMeetingActivity.class);
            openExistingMeetingIntent.putExtra("recorderName", recorderName);
            startActivity(openExistingMeetingIntent);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}