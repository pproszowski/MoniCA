package com.example.powder.monica;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AudioOnTouchActivity extends Activity {
    private TouchableButton recordButton;
    private TextView t;
    private TextView sizeText;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final int REQUEST_PERMISSIONS = 200;
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    private static String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String recordedFileName;
    private double size;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        setContentView(R.layout.activity_audio_on_touch);

        recordButton = findViewById(R.id.recordButton);
        t = findViewById(R.id.textView);
        sizeText = findViewById(R.id.sizeText);

        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        t.setText("recording");
                        AppLog.logString("Start Recording");
                        startRecording();
                        break;
                    case MotionEvent.ACTION_UP:
                        AppLog.logString("Stop Recording");
                        stopRecording();
                        break;
                }
                return false;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        String path = Environment.getExternalStorageDirectory().getPath() + "/AudioRecorder";
        size = 0;
        File directory = new File(path);
        File[] files = directory.listFiles();

        for (File file : files) {
            size += file.length();
        }
        sizeText.setText( "Size : " +size/1000+ "KB");
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdir();
        }

        return (file.getAbsolutePath() + "/" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+ Calendar.getInstance().get(Calendar.MINUTE)+":"
                +Calendar.getInstance().get(Calendar.SECOND)+file_exts[currentFormat]) ;


    }

    private void startRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
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

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Warning: " + what + ", " + extra);
        }
    };

    private void stopRecording(){
        try {

            recorder.stop();

            size+=new File(recordedFileName).length();
            t.setText(recordedFileName);
            sizeText.setText( "Size : " +size/1000+ "KB");



        }catch (RuntimeException e){
            AppLog.logString("Stopped recording immediately after start");
            AppLog.logString(recordedFileName + " should be deleted");
            File file = new File(recordedFileName);
            if(file.delete()){
                AppLog.logString(recordedFileName + " has been deleted");
            }
            t.setText("Record too short");
        }

        if(recorder != null){
            recorder.reset();
            recorder.release();

            recorder = null;
        }
    }

    public void goToStorage(View view) {

        Intent intent = new Intent(this, StorageActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }
}
