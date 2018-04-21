package com.example.powder.monica;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class New extends AppCompatActivity {

    private Button confirm;
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        confirm=findViewById(R.id.confirmThis);
        name=findViewById(R.id.meetingName);
        confirm.setOnClickListener((view) ->
                {
                    Intent newIntent = new Intent(New.this, MainMenuActivity.class);
                    newIntent.putExtra("Name",name.getText());
                    startActivity(newIntent);
                }
        );


    }
}
