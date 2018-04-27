package com.example.powder.monica;

import android.app.ListActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class StorageActivity extends ListActivity {
    private double size;
    private File[] files;
    private List<String> filesNames;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getIntent().getExtras().get("Name").toString();
        overridePendingTransition(0, 0);
        String path = Environment.getExternalStorageDirectory().getPath()+"/AudioRecorder/"+getIntent().getExtras().get("Name").toString()+"/";
        File directory = new File(path);
        filesNames = new ArrayList<>();
        files = directory.listFiles();


        for (File file : files) {
            if(!file.getName().equals("email.txt")){
                filesNames.add(file.getName()+" size: "+file.length()/1000+"KBytes");
                size+=file.length();
            }
            else{
                file.delete();
            }
        }

        System.out.println(size/1000);

        setListAdapter(new ArrayAdapter<>(this, R.layout.activity_storage,filesNames));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener((parent, view, position, id) -> {

                MediaPlayer mediaPlayer = new MediaPlayer();

                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(files[position].getAbsolutePath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(), files[position].getName(), Toast.LENGTH_SHORT).show();

        });

        listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id)->

                {
                    files[position].delete();
                    startActivity(getIntent());
                    finish();
                    return true;
                }

                );
    }

}
