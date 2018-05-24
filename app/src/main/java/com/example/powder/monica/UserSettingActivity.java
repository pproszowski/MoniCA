package com.example.powder.monica;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class UserSettingActivity extends ListActivity {

        private String recorderName;
        private String meetingName = "";
        private PrintWriter writer;
        private String path;
        private String mailSubject;
        private List<String> addresses;
        private int elementNumber=0;


    @SuppressLint("ResourceType")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            recorderName = getIntent().getExtras().getString("recorderName");
            meetingName = getIntent().getExtras().getString("Name");
            path = Environment.getExternalStorageDirectory().getPath() +"/"+recorderName+"/"+meetingName+"/email.txt";
            File file = new File(path);

            Scanner in = null;
            try {
                in = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            addresses = new ArrayList<>();
            if (file.exists()) {
                mailSubject = in.nextLine();
                addresses.add("DODAJ EMAIL");
                while (in.hasNext()) {
                    for(String email:in.next().split(","))
                    {
                        addresses.add(email);
                    }
                }


                if (addresses.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Brak podanych maili!", Toast.LENGTH_SHORT).show();
                    return;
                }

            setListAdapter(new ArrayAdapter<>(this,R.layout.activity_user_setting_activity,R.id.emailList, addresses));
            ListView listView = getListView();
            listView.setTextFilterEnabled(true);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    writer = new PrintWriter(path);
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                if (writer != null) {
                    elementNumber=0;
                    writer.println(mailSubject);
                    for (String emailList:addresses)
                    {
                        if(elementNumber!=position&&elementNumber!=0)
                        writer.println(emailList+",");
                        elementNumber++;
                    }
                    writer.close();
                }
                        startActivity(getIntent());
                        finish();
            }

            );
        }



    }
}
