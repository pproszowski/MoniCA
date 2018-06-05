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
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeetingsListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private String recorderName;

    private ListView listView;

    private MeetingsListAdapter adapter;

    private List<MeetingItem> meetingItems = new ArrayList<>();

    private List<String> checkedMeetingNames = new ArrayList<>();

    private static String path;

    private List<File> files;

    private Button checkAllButton;

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
        checkAllButton = findViewById(R.id.checkAllMeetingsButton);

        recorderName = getIntent().getExtras().getString("recorderName");

        path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (File file : dir.listFiles()) {
            String name = file.getName();
            String creationDate = null;
            try {
                creationDate = getCreationDate(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            meetingItems.add(new MeetingItem(name, creationDate));
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

    private String getCreationDate(File file) throws IOException {
        String dateCreation = null;
        File[] files = file.listFiles();
        File creationFile = null;

        for (File candidateFile : files) {
            if ("email.txt".equals(candidateFile.getName())) {
                creationFile = candidateFile;
            }
        }
        if (creationFile == null) {
            return null;
        }

        FileInputStream is;
        BufferedReader reader;

        if (creationFile.exists()) {
            is = new FileInputStream(creationFile);
            reader = new BufferedReader(new InputStreamReader(is));
            dateCreation = reader.readLine().replace(file.getName(), "");
        }

        return dateCreation;
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


    private void updateCheckedList() {
        checkedMeetingNames.clear();
        for (MeetingItem meetingItem : meetingItems) {
            if (meetingItem.isChecked()) {
                checkedMeetingNames.add(meetingItem.getName());
                AppLog.logString("DELETE");
            }
        }
    }

    private void deleteItem(File file) {
        MeetingItem meetingItem = findFileItemInTheListByName(file.getName());
        if (meetingItem!= null) {
            meetingItems.remove(meetingItem);
        }
        String[] children = file.list();
        for (int i = 0; i < children.length; i++)
        {
            new File(file, children[i]).delete();
        }
        file.delete();
        File directory = new File(path);
        files = Arrays.asList(directory.listFiles());
        adapter.remove(meetingItem);
        adapter.notifyDataSetChanged();
    }

    private MeetingItem findFileItemInTheListByName(String name) {
        for (MeetingItem meetingItem : meetingItems) {
            if (meetingItem.getName().equals(name)) {
                return meetingItem;
            }
        }
        return null;
    }

    public void deleteCheckedMeetings(View view) {
        updateCheckedList();
        File directory = new File(path);
        files = Arrays.asList(directory.listFiles());

        AppLog.logString(">>>>>>>>>>>>>>>>>>>>>>>" + files.size());
        AppLog.logString(">>>>>>>>>>>>>>>>>>>>>>>" + path);
        AppLog.logString(">>>>>>>>>>>>>>>>>>>>>>>" + checkedMeetingNames.size());


        if (checkedMeetingNames.size() == 0) {
            Toast.makeText(getApplicationContext(), "Zaznacz pliki do usunięcia", Toast.LENGTH_LONG).show();
        }

        for (File file : files) {
            if (checkedMeetingNames.contains(file.getName())) {
                deleteItem(file);
            }
        }
    }

    public void checkAllMeetings(View view) {
        if (meetingItems.isEmpty()) return;

        if (checkedAll()) {
            unCheckAll();
            checkAllButton.setText("Check All");
        } else {
            checkAll();
            checkAllButton.setText("Uncheck All");
        }
        adapter.notifyDataSetChanged();
    }

    private boolean checkedAll() {
        for(MeetingItem meetingItem : meetingItems){
            if(!meetingItem.isChecked()){
                return false;
            }
        }
        return true;
    }

    private void unCheckAll() {
        for(MeetingItem meetingItem : meetingItems){
            meetingItem.unCheck();
        }
    }

    private void checkAll() {
        for(MeetingItem meetingItem : meetingItems){
            meetingItem.check();
        }
    }
}
