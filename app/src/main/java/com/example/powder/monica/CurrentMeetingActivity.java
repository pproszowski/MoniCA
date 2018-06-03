package com.example.powder.monica;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.powder.monica.storage.StorageActivity;
import com.google.cloud.android.speech.SpeechService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;


public class CurrentMeetingActivity extends AppCompatActivity {

    private static final int GET_CHECKED_FILE_NAMES = 1;

    private static final int REQUEST_TAKE_PHOTO = 2;

    private final SpeechService.Listener mSpeechServiceListener = (text, isFinal) -> System.out.println(text);

    private ImageButton recordButton;

    private TextView recordingStatus;

    private TextView sizeText;

    private String recorderName;

    private String meetingName = "";

    private String recordedFileName;

    private double size;

    private String mailSubject;

    private List<String> checkedFileNames = new ArrayList<>();

    private double sizeSelectedItems;

    private SpeechService mSpeechService;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }
    };
    private PriorityBar priorityBar;
    private VoiceRecorder voiceRecorder;

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);


        recordButton.setOnTouchListener((v, event) -> {
            recordButton.performClick();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    recordingStatus.setText(R.string.recording);
                    ((ImageButton) v).setImageResource(R.drawable.ic_mic_red);
                    AppLog.logString("Start Recording");
                    voiceRecorder.startRecording(priorityBar.getChosenPriority());
                    break;
                case MotionEvent.ACTION_UP:
                    AppLog.logString("Stop Recording");
                    ((ImageButton) v).setImageResource(R.drawable.ic_mic_gray);
                    File file = voiceRecorder.stopRecording();
                    convertFile(file);
                    break;
            }
            return false;
        });

    }

    @Override
    protected void onStop() {

        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ftp_setting, menu);
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
                Intent intent = new Intent(this, UserSettingActivity.class);
                intent.putExtra("Name", meetingName);
                intent.putExtra("recorderName", recorderName);
                intent.putExtra("email", "");
                startActivity(intent);
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
        TextView meetingNameText = (TextView) findViewById(R.id.meetingNameField);
        meetingNameText.setText(meetingName);

        voiceRecorder = new VoiceRecorder(meetingName, recorderName);
        priorityBar = new PriorityBar(findViewById(android.R.id.content));


        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }

            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });
    }

    @Override
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
            if (!Objects.equals("email.txt", file.getName())) {
                size += file.length();
            }
        }
        if (size <= 1_048_576) {
            sizeText.setText(String.format("Size : %.2fKB", size / 1024));
        } else {
            sizeText.setText(String.format("Size : %.2fMB", size / 1_048_576));
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

        Intent photoIntent = new Intent(this, MakePhotoActivity.class);
        photoIntent.putExtra("Name", meetingName);
        photoIntent.putExtra("recorderName", recorderName);
        photoIntent.putExtra("choosenPriority", priorityBar.getChosenPriority());
        startActivityForResult(photoIntent, REQUEST_TAKE_PHOTO);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent openExistingMeetingIntent = new Intent(this, MeetingsListActivity.class);
            openExistingMeetingIntent.putExtra("recorderName", recorderName);
            startActivity(openExistingMeetingIntent);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void convertFile(File file) {
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                file.delete();
                recordedFileName = convertedFile.getAbsolutePath();
                try {
                    FileInputStream fis = new FileInputStream(recordedFileName);
                    mSpeechService.recognizeInputStream(fis);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //success
            }

            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
            }
        };
        AndroidAudioConverter.with(this)
                .setFile(file)
                .setFormat(AudioFormat.FLAC)
                .setCallback(callback)
                .convert();

    }
}