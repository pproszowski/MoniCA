package com.example.powder.monica;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.AutoLabelUISettings;
import com.dpizarro.autolabel.library.Label;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewMeetingActivity extends AppCompatActivity {

    public final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    String[] emailString;
    private Button confirmButton;
    private TextInputEditText meetingName;
    private File file;
    private File directory;
    private PrintWriter writer;
    private TextInputEditText inputEmail;
    private String recorderName;
    private String mailSubject;
    private String path;
    private AutoLabelUI mAutoLabel;
    private ImageView addNewEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        //confirmButton = findViewById(R.id.confirmThis);
        meetingName = findViewById(R.id.meetingNameInput);
        inputEmail = findViewById(R.id.emailInput);
        recorderName = getIntent().getExtras().getString("recorderName");
        mAutoLabel = findViewById(R.id.label_view);
        addNewEmailButton = findViewById(R.id.addNewEmailButton);

        addNewEmailButton.setOnClickListener((view) -> {
                    String email = inputEmail.getText().toString();
                    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

                    if (mAutoLabel.getLabelsCounter() <= 20) {
                        if (matcher.matches()) {
                            mAutoLabel.addLabel(email);
                            inputEmail.setText("");
                        } else {
                            Toast.makeText(NewMeetingActivity.this, "Invalid e-mail address!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewMeetingActivity.this, "You can enter only 20 e-mails!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_meeting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createMeetingButton: {

                path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName;
                directory = new File(path);
                if (directory.exists()) {
                    File[] files = directory.listFiles();

                    for (File file : files) {
                        if (meetingName.getText().toString().compareToIgnoreCase(file.getName().toString()) == 0) {
                            Toast.makeText(getApplicationContext(), "Meeting already exsists!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }

                if (meetingName.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Meeting name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                mailSubject = meetingName.getText() + " "
                        + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/"
                        + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/"
                        + Calendar.getInstance().get(Calendar.YEAR) + " godz. "
                        + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "꞉"
                        + Calendar.getInstance().get(Calendar.MINUTE);

                path += "/" + meetingName.getText().toString();
                directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                file = new File(path, "email.txt");

                try {
                    writer = new PrintWriter(path + "/email.txt");
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                if (writer != null) {
                    List<Label> labels = mAutoLabel.getLabels();
                    writer.println(mailSubject);
                    for (int i = 0; i < labels.size(); i++) {
                        writer.print(labels.get(i).getText());
                        writer.print(",");
                    }

                    writer.close();
                }
                Intent newIntent = new Intent(NewMeetingActivity.this, CurrentMeetingActivity.class);
                newIntent.putExtra("Name", meetingName.getText().toString());
                newIntent.putExtra("recorderName", recorderName.toString());
                newIntent.putExtra("mailSubject", mailSubject.toString());
                startActivity(newIntent);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
