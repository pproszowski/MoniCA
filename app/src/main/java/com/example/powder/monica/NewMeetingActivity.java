package com.example.powder.monica;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class New extends AppCompatActivity {

    private Button confirmButton;
    private TextInputEditText meetingName;
    private File file;
    private File directory;
    private PrintWriter writer;
    private TextInputEditText inputEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        confirmButton = findViewById(R.id.confirmThis);
        meetingName = findViewById(R.id.meetingNameInput);
        inputEmail = findViewById(R.id.emailInput);

        confirmButton.setOnClickListener((view) ->
                {
                    if(meetingName.getText().length() == 0){
                        Toast.makeText(getApplicationContext(), "Nazwa spotkania nie może być pusta!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String path = Environment.getExternalStorageDirectory().getPath()+"/AudioRecorder/"+ meetingName.getText().toString();
                    directory = new File(path);
                    if(!directory.exists()) {
                    directory.mkdirs();
                    }

                    file = new File(path, "email.txt");

                    try {
                        writer = new PrintWriter(path+"/email.txt");
                    } catch (FileNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    if(writer !=null) {
                        writer.println(inputEmail.getText().toString());
                        writer.close();
                    }
                    Intent newIntent = new Intent(New.this, AudioOnTouchActivity.class);
                    newIntent.putExtra("Name", meetingName.getText());
                    startActivity(newIntent);
                }
        );

    }
}
