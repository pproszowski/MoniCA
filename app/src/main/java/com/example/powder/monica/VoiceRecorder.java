package com.example.powder.monica;

import android.media.MediaRecorder;
import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class VoiceRecorder {

    private MediaRecorder.OnErrorListener errorListener = (mr, what, extra) -> AppLog.logString("Error: " + what + ", " + extra);

    private MediaRecorder.OnInfoListener infoListener = (mr, what, extra) -> AppLog.logString("Warning: " + what + ", " + extra);

    public static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";

    public static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";

    public static final int output_formats[] = {MediaRecorder.OutputFormat.DEFAULT, MediaRecorder.OutputFormat.THREE_GPP};

    public static final String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};

    private int currentFormat = 0;

    private MediaRecorder recorder = null;

    private String recordedFileName;

    private String meetingName;

    private String recorderName;

    private String chosenPriority;

    public VoiceRecorder(String meetingName, String recorderName){
        this.meetingName = meetingName;
        this.recorderName = recorderName;
    }

    public void startRecording(String chosenPriority) {
        this.chosenPriority = chosenPriority;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setAudioSamplingRate(16_000);
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

    public File stopRecording() {

        File file = new File(recordedFileName);
        recorder.stop();

        if (recorder != null) {
            recorder.reset();
            recorder.release();
            recorder = null;
        }

        return file;
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, recorderName + "/" + meetingName);
        String time = "hh:mm:ss";
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + chosenPriority + "Rec "
                + DateFormat.format(time, Calendar.getInstance().getTime())
                + file_exts[currentFormat]);

    }

}
