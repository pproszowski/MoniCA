package com.example.powder.monica;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEmail extends AppCompatActivity {
    public final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_email);


    }

    public void add(View view) {
        EditText text=findViewById(R.id.newEmailString);

        Intent intent=new Intent(this,UserSettingActivity.class);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(text.getText().toString());
        intent.putExtra("email", "");
        if (matcher.matches()) {
            intent.putExtra("email", text.getText().toString());
        }
        else
            Toast.makeText(getApplicationContext(), "Podaj poprawny email", Toast.LENGTH_SHORT).show();
        String recorderName = getIntent().getExtras().getString("recorderName");
        String meetingName = getIntent().getExtras().getString("Name");
        intent.putExtra("Name", meetingName);
        intent.putExtra("recorderName", recorderName);
        startActivity(intent);
        finish();
    }
}
