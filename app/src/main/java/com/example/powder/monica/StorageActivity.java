package com.example.powder.monica;

import android.app.Activity;
import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageActivity extends ListActivity {
    private File[] files;
    private List<String> filesNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String path = Environment.getExternalStorageDirectory().getPath()+"/AudioRecorder";
        File directory = new File(path);
        filesNames = new ArrayList<>();
        files = directory.listFiles();
        for (File file : files) {
            filesNames.add(file.getName());
        }

        setListAdapter(new ArrayAdapter<>(this, R.layout.activity_storage,filesNames));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String absolutePath;
                for(File file : files){
                    if(file.getName().contentEquals(((TextView) view).getText())){
                        absolutePath = file.getAbsolutePath();
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(absolutePath);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void backToRecords(View view) {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
