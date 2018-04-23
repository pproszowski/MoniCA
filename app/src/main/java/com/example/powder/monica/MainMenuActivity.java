package com.example.powder.monica;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    Button buttonNew, buttonOpen, buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        buttonNew = (Button) findViewById(R.id.button_new);
        buttonOpen = (Button) findViewById(R.id.button_open);
        buttonExit = (Button) findViewById(R.id.button_exit);

        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//TODO: WPISAĆ NAZWĘ NOWEGO ACTIVITY, KTÓRE ZOSTANIE WYWOŁANE PO WCIŚNIECIU PRZYCISKU NEW
/*
                Intent newFolderActivity = new Intent(MainMenuActivity.this, <NOWEACTIVITY>.class);
                startActivity(newFolderActivity);
*/
            }
        });

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//TODO: WPISAĆ NAZWĘ NOWEGO ACTIVITY, KTÓRE ZOSTANIE WYWOŁANE PO WCIŚNIECIU PRZYCISKU OPEN
/*
                Intent openFolderActivity = new Intent(MainMenuActivity.this, <OPENACTIVITY>.class);
                startActivity(openFolderActivity);
*/
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });





    }



}
