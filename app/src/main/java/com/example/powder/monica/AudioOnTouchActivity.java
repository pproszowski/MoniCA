package com.example.powder.monica;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AudioOnTouchActivity extends Activity {
    private TouchableButton recordButton;
    private Button ftpButton;
    private Button sendEmailButton;
    private TextView recordingStatus;
    private TextView sizeText;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private String recorderName;
    private String meetingName = "";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.DEFAULT, MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    private String recordedFileName;
    private double size;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_on_touch);
        recorderName = getIntent().getExtras().getString("recorderName");
        meetingName = getIntent().getExtras().getString("Name");
        AppLog.logString(meetingName);

        recordButton = findViewById(R.id.recordButton);
        recordingStatus = findViewById(R.id.textView);
        sizeText = findViewById(R.id.sizeText);
        ftpButton =findViewById(R.id.ftp);
        sendEmailButton =findViewById(R.id.email);

        ftpButton.setOnClickListener((view)-> new FTP(recorderName, meetingName).execute());

        sendEmailButton.setOnClickListener((view)->{

            ArrayList<Uri> filesUri = new ArrayList<>();
            String path = Environment.getExternalStorageDirectory().getPath() +"/"+recorderName+"/"+ meetingName;
            File directory = new File(path);

            File[] files = directory.listFiles();

            for (File file : files) {
                filesUri.add(Uri.fromFile(file));
            }

            File file = new File (path,"email.txt");

            Scanner in = null;
            try {
                in = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            List<String> addresses = new ArrayList<>();
            if(file.exists()) {

                while(in.hasNext())
                {
                    addresses.add(in.nextLine());
                }


                if(addresses.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Brak podanych maili!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String a[] = new String[0];
                Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
                email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, filesUri);
                email.putExtra(Intent.EXTRA_EMAIL, addresses.toArray(a));
                email.putExtra(Intent.EXTRA_SUBJECT, meetingName);
                email.putExtra(Intent.EXTRA_TEXT, "MoniCA");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
            });

        recordButton.setOnTouchListener((v, event) -> {
            recordButton.performClick();
            switch(event.getAction()){
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
    }

    protected void onResume() {
        super.onResume();

        String path = Environment.getExternalStorageDirectory().getPath() +"/"+recorderName+"/"+ meetingName;


        size = 0;
        File directory = new File(path);

        if(!directory.exists()){
            directory.mkdirs();
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            size += file.length();
        }
        sizeText.setText(String.format("Size : %sKB", size / 1000));
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,recorderName+"/"+ meetingName);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/Rec "
               + Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"꞉"
               + Calendar.getInstance().get(Calendar.MINUTE)+"꞉"
               + Calendar.getInstance().get(Calendar.SECOND)
               + file_exts[currentFormat]);

    }

    private void startRecording(){
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

    private void stopRecording(){
        try {
            recorder.stop();
            size += new File(recordedFileName).length();
            recordingStatus.setText(recordedFileName);
            sizeText.setText(String.format("Size : %sKB", size / 1000));

        }catch (RuntimeException e){
            AppLog.logString("Stopped recording immediately after start");
            AppLog.logString(recordedFileName + " should be deleted");
            File file = new File(recordedFileName);
            if(file.delete()){
                AppLog.logString(recordedFileName + " has been deleted");
            }
            recordingStatus.setText(R.string.record_too_short);
        }

        if(recorder != null){
            recorder.reset();
            recorder.release();

            recorder = null;
        }
    }

    public void goToStorage(View view) {

        Intent intent = new Intent(this, StorageActivity.class);
        intent.putExtra("Name", meetingName);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    public void makePhoto(View view) {
        MakePhoto makePhoto = new MakePhoto(this);
        makePhoto.dispatchTakePictureIntent();
    }
}
