package com.example.powder.monica;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
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
    private final String emailContent = "\nLegenda do notatek:\n" +
            "Notatki zaczynają się prefixami, które świadczą o ważności informacji\n" +
            "1) Must - oznacza krytyczne wymaganie, które musi zostać spełnione na początku, aby projekt mógł się powieść\n" +
            "2) Should -  wymaganie istotne dla powodzenia projektu, jednak nie są konieczne w aktualnej fazie cyklu projektu\n" +
            "3) Could - wymaganie mniej krytyczne i często są postrzegane jako takie, które dobrze żeby były. " +
            "Kilka takich spełnionych wymagań w projekcie może zwiększyć zadowolenie klienta przy równoczesnym niskim koszcie ich dostarczenia.\n" +
            "4) Will not - informacje, które w chwilii obecnej nie są wymagane, ale mogą się stać np. w kolejnym cyklu projektu";

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
        SeekBar priorityBar = findViewById(R.id.priorityBar);
        Button ftpButton = findViewById(R.id.ftp);
        Button sendEmailButton = findViewById(R.id.email);
        sizeOfSelectedItemsText = findViewById(R.id.sizeOfSelectedItemsText);
        ftpButton.setOnClickListener((view) -> new FTP(recorderName, meetingName).execute());

        willNotText.setTypeface(null, Typeface.BOLD);
        willNotText.setTextSize(16);

        sendEmailButton.setOnClickListener((view) -> {


            if(sizeSelectedItems <= 10000000) {
                ArrayList<Uri> filesUri = new ArrayList<>();
                path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName;
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
                    email.putExtra(Intent.EXTRA_TEXT, emailContent);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Rozmiar zaznaczonych plików większy niż 10 MB.", Toast.LENGTH_SHORT).show();
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
        sizeText.setText(String.format("Size : %sKB", size / 1000));
        sizeOfSelectedItemsText.setText(String.format("Selected files size : %sKB", sizeSelectedItems / 1000));
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
            recordingStatus.setText(recordedFileName);
            sizeText.setText(String.format("Size : %sKB", size / 1000));
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
                setChosenPriority(willNotText, couldText);
                choosenPriority = "WillNot ";
                break;

            case 1:
                if(Objects.equals("WillNot ", choosenPriority)){
                    setChosenPriority(couldText, willNotText);
                }else{
                    setChosenPriority(couldText, shouldText);
                }
                choosenPriority = "Could ";
                break;

            case 2:
                if(Objects.equals("Could ", choosenPriority)){
                    setChosenPriority(shouldText, couldText);
                }else{
                    setChosenPriority(shouldText, mustText);
                }
                choosenPriority = "Should ";
                break;

            case 3:
                setChosenPriority(mustText, shouldText);
                choosenPriority = "Must ";
                break;
            default:
                break;
        }
    }

    private void setChosenPriority(TextView chosenPriority, TextView previous){
        chosenPriority.setTypeface(null, Typeface.BOLD);
        previous.setTypeface(null, Typeface.NORMAL);

        chosenPriority.setTextSize(16);
        previous.setTextSize(14);
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
