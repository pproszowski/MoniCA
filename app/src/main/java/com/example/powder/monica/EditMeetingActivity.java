package com.example.powder.monica;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.Label;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditMeetingActivity extends AppCompatActivity {

    public final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private String recorderName;
    private String meetingName;
    private String path;
    private String mailSubject;
    private AutoLabelUI mAutoLabel;
    private TextInputEditText meetingNameInput;
    private ImageView addNewEmailButton;
    private TextInputEditText inputEmail;
    private PrintWriter writer;
    private List<String> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meeting);

        recorderName = getIntent().getExtras().getString("recorderName");
        meetingName = getIntent().getExtras().getString("Name");
        mailSubject = getIntent().getExtras().getString("mailSubject");
        path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName + "/email.txt";
        mAutoLabel = findViewById(R.id.label_view);
        meetingNameInput = findViewById(R.id.meetingNameInput);
        addNewEmailButton = findViewById(R.id.addNewEmailButton);
        inputEmail = findViewById(R.id.emailInput);


        meetingNameInput.setText(meetingName);

        File file = new File(path);

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (file.exists()) {
            mailSubject = in.nextLine();
            while (in.hasNext()) {
                for (String email : in.next().split(",")) {
                    mAutoLabel.addLabel(email);
                }
            }
        }

        addNewEmailButton.setOnClickListener((view) -> {
                    String email = inputEmail.getText().toString();
                    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

                    if (mAutoLabel.getLabelsCounter() <= 20) {
                        if (matcher.matches()) {
                            mAutoLabel.addLabel(email);
                            inputEmail.setText("");
                        } else {
                            Toast.makeText(EditMeetingActivity.this, "Invalid e-mail address!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditMeetingActivity.this, "You can enter only 20 e-mails!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_meeting_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirmEditMeetingButton: {
                try {
                    writer = new PrintWriter(path);
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                if (writer != null) {
                    List<Label> labels = mAutoLabel.getLabels();
                    addresses = new ArrayList<>();

                    for (int i = 0; i < labels.size(); i++) {
                        addresses.add(labels.get(i).getText());
                        System.out.println(labels.get(i).getText());
                    }

                    writer.println(mailSubject);
                    for (String emailList : addresses) {
                        writer.println(emailList + ",");
                    }
                    writer.close();
                }

                File fileOriginal = new File(Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName);

                if (fileOriginal.isDirectory()) {
                    File fileToChange = new File(Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingNameInput.getText().toString());
                    fileOriginal.renameTo(fileToChange);
                }



                Intent newIntent = new Intent(EditMeetingActivity.this, CurrentMeetingActivity.class);
                newIntent.putExtra("Name", meetingNameInput.getText().toString());
                newIntent.putExtra("recorderName", recorderName.toString());
                newIntent.putExtra("mailSubject", mailSubject.toString());
                startActivity(newIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
