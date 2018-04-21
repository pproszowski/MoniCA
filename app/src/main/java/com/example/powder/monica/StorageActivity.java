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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StorageActivity extends ListActivity {
    private double size;
    private File[] files;
    private List<String> filesNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        String path = Environment.getExternalStorageDirectory().getPath()+"/AudioRecorder";
        File directory = new File(path);
        filesNames = new ArrayList<>();
        files = directory.listFiles();

        for (File file : files) {
            filesNames.add(file.getName()+" size :"+file.length()/1000+"KBytes");
            size+=file.length();

        }

        System.out.println(size/1000);

        setListAdapter(new ArrayAdapter<>(this, R.layout.activity_storage,filesNames));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener((parent, view, position, id) -> {

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(files[position].getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }


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

    /*public void backToRecords(View view) {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }*/
}
