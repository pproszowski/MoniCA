package com.example.powder.monica;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.Arrays;

public class OpenExistingMeetingActivity extends ListActivity{
    private String recorderName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recorderName = getIntent().getExtras().getString("recorderName");
        String path = Environment.getExternalStorageDirectory().getPath() +"/"+recorderName;
        File file = new File(path);
        String[] meetings = file.list((dir, name) -> new File(dir, name).isDirectory());

        setListAdapter(new ArrayAdapter<>(this, R.layout.activity_meetings,Arrays.asList(meetings)));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent onTouchActivity = new Intent(OpenExistingMeetingActivity.this, AudioOnTouchActivity.class);
            onTouchActivity.putExtra("recorderName", recorderName);
            onTouchActivity.putExtra("Name", meetings[position]);
            startActivity(onTouchActivity);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
            startActivity(mainMenuIntent);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
