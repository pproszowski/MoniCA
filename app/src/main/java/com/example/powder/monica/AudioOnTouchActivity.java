package com.example.powder.monica;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.powder.monica.storage.StorageActivity;


public class AudioOnTouchActivity extends Activity {
    private TouchableButton recordButton;
    private TextView recordingStatus;
    private TextView sizeText;
    private TextView sizeOfSelectedItemsText;
    private TextView willNotText;
    private TextView couldText;
    private TextView shouldText;
    private TextView mustText;
    private SeekBar priorityBar;
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
    private String choosenPriority = "WillNot_";


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_on_touch);
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
        priorityBar = findViewById(R.id.priorityBar);
        Button ftpButton = findViewById(R.id.ftp);
        Button sendEmailButton = findViewById(R.id.email);
        sizeOfSelectedItemsText = findViewById(R.id.sizeOfSelectedItemsText);
        ftpButton.setOnClickListener((view) -> new FTP(recorderName, meetingName).execute());

        willNotText.setTypeface(null, Typeface.BOLD);
        willNotText.setTextSize(16);

        sendEmailButton.setOnClickListener((view) -> {

            ArrayList<Uri> filesUri = new ArrayList<>();
            String path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName;
            File directory = new File(path);

            File[] files = directory.listFiles();

            for (File file : files) {
                if (checkedFileNames.contains(file.getName())) {
                    filesUri.add(Uri.fromFile(file));
                }
            }

            File file = new File(path, "email.txt");

            Scanner in = null;
            try {
                in = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            List<String> addresses = new ArrayList<>();
            if (file.exists()) {
                mailSubject = in.nextLine();
                while (in.hasNext()) {
                    addresses.add(in.nextLine());
                }


                if (addresses.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Brak podanych maili!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String a[] = new String[0];
                Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
                email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, filesUri);
                email.putExtra(Intent.EXTRA_EMAIL, addresses.toArray(a));
                email.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
                email.putExtra(Intent.EXTRA_TEXT, "MoniCA");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        recordButton.setOnTouchListener((v, event) -> {
            recordButton.performClick();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    recordingStatus.setText(R.string.recording);
                    AppLog.logString("Start Recording");
                    startRecording();
                    break;
                case MotionEvent.ACTION_UP:
                    AppLog.logString("Stop Recording");
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

        String path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName;


        size = 0;
        double sizeOfSelectedFiles = 0;
        File directory = new File(path);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            if (checkedFileNames.contains(file.getName())) {
                sizeOfSelectedFiles += file.length();
            }
            if (!"email.txt".equals(file.getName())) {
                size += file.length();
            }
        }
        sizeText.setText(String.format("Size : %sKB", size / 1000));
        sizeOfSelectedItemsText.setText(String.format("Selected files size : %sKB", sizeOfSelectedFiles / 1000));
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
            size += new File(choosenPriority + recordedFileName).length();
            recordingStatus.setText(recordedFileName);
            sizeText.setText(String.format("Size : %sKB", size / 1000));
        } catch (RuntimeException e) {
            recordingStatus.setText(R.string.record_too_short);
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
                choosenPriority = "WillNot_";
                willNotText.setTypeface(null, Typeface.BOLD);
                couldText.setTypeface(null, Typeface.NORMAL);
                shouldText.setTypeface(null, Typeface.NORMAL);
                mustText.setTypeface(null, Typeface.NORMAL);

                willNotText.setTextSize(16);
                couldText.setTextSize(14);
                shouldText.setTextSize(14);
                mustText.setTextSize(14);
                break;

            case 1:
                choosenPriority = "Could_";
                willNotText.setTypeface(null, Typeface.NORMAL);
                couldText.setTypeface(null, Typeface.BOLD);
                shouldText.setTypeface(null, Typeface.NORMAL);
                mustText.setTypeface(null, Typeface.NORMAL);

                willNotText.setTextSize(14);
                couldText.setTextSize(16);
                shouldText.setTextSize(14);
                mustText.setTextSize(14);
                break;

            case 2:
                choosenPriority = "Sould_";
                willNotText.setTypeface(null, Typeface.NORMAL);
                couldText.setTypeface(null, Typeface.NORMAL);
                shouldText.setTypeface(null, Typeface.BOLD);
                mustText.setTypeface(null, Typeface.NORMAL);

                willNotText.setTextSize(14);
                couldText.setTextSize(14);
                shouldText.setTextSize(16);
                mustText.setTextSize(14);
                break;

            case 3:
                choosenPriority = "Must_";
                willNotText.setTypeface(null, Typeface.NORMAL);
                couldText.setTypeface(null, Typeface.NORMAL);
                shouldText.setTypeface(null, Typeface.NORMAL);
                mustText.setTypeface(null, Typeface.BOLD);

                willNotText.setTextSize(14);
                couldText.setTextSize(14);
                shouldText.setTextSize(14);
                mustText.setTextSize(16);
                break;

            default:
                break;
        }
    }
}
