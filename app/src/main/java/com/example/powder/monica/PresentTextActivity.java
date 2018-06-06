package com.example.powder.monica;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PresentTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_text);
        TextView textView = findViewById(R.id.googleSpeechResponseTextView);
        String text = getIntent().getExtras().getString("googleSpeechResponse");
        textView.setText(text);
    }
}
