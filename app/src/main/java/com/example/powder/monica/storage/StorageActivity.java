package com.example.powder.monica.storage;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.Toast;

import com.example.powder.monica.OnSwipeTouchListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StorageActivity extends ListActivity {
    private File[] files;
    private Set<FileItem> fileItems = new LinkedHashSet<>();
    private String name;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        name = getIntent().getExtras().getString("Name");
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/AudioRecorder/" + name + "/";
        File directory = new File(path);
        files = directory.listFiles();


        for (File file : files) {
            if(!file.getName().equals("email.txt")){
                String name = file.getName();
                fileItems.add(new FileItem(name, file.length(), false));
            }
        }

        try{
            List<String> checkedFileNames = getIntent().getExtras().getStringArrayList("checkedFileNames");
            if(checkedFileNames != null){
              selectCheckBoxes(checkedFileNames);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        setListAdapter(new StorageArrayAdapter(this, new ArrayList<>(fileItems)));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

            listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(), listView) {

                @Override
                public void onClick() {
                    super.onClick();
                    int position = getPosition() + 1;
                    File file = files[position];
                    if(file.getName().contains("jpg")){
                        openImage(file, listView.getContext());
                    }else{
                        openAudio(file);
                    }
                }

                @Override
                public void onLongClick() {
                    super.onLongClick();
                }


                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    if(files.length < 2){
                        return;
                    }
                    int position = getPosition() + 1;
                    File file = files[position];
                    deleteItem(file);
                    Toast.makeText(getApplicationContext(), "Usunięto " + file.getName(), Toast.LENGTH_LONG).show();
            }

                @Override
                public void onSwipeRight() {
                    super.onSwipeRight();
                    if(files.length < 2){
                        Toast.makeText(getApplicationContext(), "Brak plików w folderze." , Toast.LENGTH_LONG).show();
                        return;
                    }
                    addArchive(files, path);
                    Toast.makeText(getApplicationContext(), "Dodano archiwum zip", Toast.LENGTH_LONG).show();
                }
            });
    }

    private void openImage(File file, Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                        android.support.v4.content.FileProvider.getUriForFile(context,
                                getPackageName() + ".fileprovider", file)
                        : Uri.fromFile(file), "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void openAudio(File file) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            String filePath = file.getAbsolutePath();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Odtwarzam... " + file.getName() ,Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(File file) {
        FileItem fileItem =findFileItemInTheSetByName(file.getName());
        if(fileItem != null){
            fileItems.remove(fileItem);
        }
        file.delete();
        startActivity(getIntent());
        finish();
    }

    private void addArchive(File[] files, String path) {
        ZipManager archive = new ZipManager();
        List<String> fileNamesList = new ArrayList<>();
        for(File f : files){
            fileNamesList.add(path + f.getName());
        }
        String [ ] fileNames = fileNamesList.toArray(new String[0]);
        archive.zip(fileNames, path+name+".zip");
        startActivity(getIntent());
        finish();
    }

    private void selectCheckBoxes(List<String> checkedFileNames) {
        for(FileItem fileItem : fileItems){
            if (checkedFileNames.contains(fileItem.getName())) {
                fileItem.setChecked(true);
            }
        }
    }

    private FileItem findFileItemInTheSetByName(String name){
        for (FileItem fileItem : fileItems) {
            if (fileItem.getName().equals(name)) {
                return fileItem;
            }
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent data = new Intent();
            ArrayList<String> checkedFileNames = new ArrayList<>();
            for(FileItem fileItem : fileItems){
                if(fileItem.isChecked()){
                    checkedFileNames.add(fileItem.getName());
                }
            }
            if(!checkedFileNames.isEmpty()){
                data.putStringArrayListExtra("checkedFileNames", checkedFileNames);
                setResult(RESULT_OK, data);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
