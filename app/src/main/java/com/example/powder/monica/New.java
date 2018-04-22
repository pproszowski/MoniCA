package com.example.powder.monica;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class New extends AppCompatActivity {

    private Button confirm;
    private EditText name;
    private File plik;
    private File directory;
    private PrintWriter zapis;
    private TextInputEditText emailName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        confirm=findViewById(R.id.confirmThis);
        name=findViewById(R.id.meetingName);
        emailName=findViewById(R.id.email);
        confirm.setOnClickListener((view) ->
                {
                    String path = Environment.getExternalStorageDirectory().getPath()+"/AudioRecorder/"+name.getText().toString();
                    directory=new File(path);
                    if(!directory.exists()) {
                    directory.mkdirs();
                    }

                    plik = new File(path, "email.txt");

                    try {
                        zapis = new PrintWriter(path+"/email.txt");
                    } catch (FileNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                    zapis.println(emailName.getText().toString());
                    zapis.close();

                    Intent newIntent = new Intent(New.this, MainMenuActivity.class);
                    newIntent.putExtra("Name",name.getText());
                    startActivity(newIntent);
                }
        );


    }
}
