package com.example.powder.monica;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeetingsListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private String recorderName;
    private ListView listView;
    private MeetingsListAdapter adapter;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meetings_list);
        listView = (ListView) findViewById(R.id.meetingsListView);

        recorderName = getIntent().getExtras().getString("recorderName");
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<MeetingItem> meetingItems = new ArrayList<>();

        for (File file : dir.listFiles()) {
            String name = file.getName();
            String lastModifyDate = new Date(file.lastModified()).toString();
            meetingItems.add(new MeetingItem(name, lastModifyDate));
        }

        adapter = new MeetingsListAdapter(this, meetingItems);
        listView.setAdapter(adapter);

        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent onTouchActivity = new Intent(MeetingsListActivity.this, CurrentMeetingActivity.class);
            onTouchActivity.putExtra("recorderName", recorderName);
            onTouchActivity.putExtra("Name", meetingItems.get(position).getName());
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }
}
