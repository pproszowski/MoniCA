package com.example.powder.monica.storage;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.powder.monica.FTP;
import com.example.powder.monica.OnSwipeTouchListener;
import com.example.powder.monica.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class StorageActivity extends ListActivity {
    private Button sendButton;
    private File[] files;
    private Set<FileItem> fileItems = new LinkedHashSet<>();
    private String name;
    private Double sizeSelectedItems;
    private String recorderName;
    private String meetingName;
    private ArrayList<String> checkedFileNames = new ArrayList<String>();
    protected ProgressBar progressBar;
    protected TextView percentageProgress;
    private String path;
    private String mailSubject ;
    private String emailContent = "\nLegenda do notatek:\n" +
            "Notatki zaczynają się prefixami, które świadczą o ważności informacji\n" +
            "1) Must - oznacza krytyczne wymaganie, które musi zostać spełnione na początku, aby projekt mógł się powieść\n" +
            "2) Should -  wymaganie istotne dla powodzenia projektu, jednak nie są konieczne w aktualnej fazie cyklu projektu\n" +
            "3) Could - wymaganie mniej krytyczne i często są postrzegane jako takie, które dobrze żeby były. " +
            "Kilka takich spełnionych wymagań w projekcie może zwiększyć zadowolenie klienta przy równoczesnym niskim koszcie ich dostarczenia.\n" +
            "4) Will not - informacje, które w chwilii obecnej nie są wymagane, ale mogą się stać np. w kolejnym cyklu projektu";



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        sendButton = (Button) findViewById(R.id.send_popup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        percentageProgress = (TextView) findViewById(R.id.percProgress);
        overridePendingTransition(0, 0);
        name = getIntent().getExtras().getString("Name");
        //sizeSelectedItems = getIntent().getExtras().getDouble("sizeSelectedItems");
        recorderName = getIntent().getExtras().getString("recorderName");
        meetingName = getIntent().getExtras().getString("meetingName");


        path = Environment.getExternalStorageDirectory().getPath()
                + "/AudioRecorder/" + name + "/";
        File directory = new File(path);
        files = directory.listFiles();


        for (File file : files) {
            if (!file.getName().equals("email.txt")) {
                String name = file.getName();
                fileItems.add(new FileItem(name, file.length(), false));
            }
        }

        ProgressUpdater pu = new ProgressUpdater(checkedFileNames, fileItems,sizeSelectedItems, progressBar, percentageProgress, path);
        setListAdapter(new StorageArrayAdapter(this, new ArrayList<>(fileItems), pu));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);



        listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(), listView) {

            @Override
            public void onClick() {
                super.onClick();
                if (getPosition() != -1) {
                    int position = getPosition() + 1;
                    File file = files[position];
                    if (file.getName().contains("jpg")) {
                        openImage(file, listView.getContext());
                    } else {
                        openAudio(file);
                    }
                }
            }



            @Override
            public void onLongClick() {
                super.onLongClick();
            }


            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (files.length < 2) {
                    return;
                }
                if (getPosition() != -1) {
                    int position = getPosition() + 1;
                    File file = files[position];
                    deleteItem(file);
                    Toast.makeText(getApplicationContext(), "Usunięto " + file.getName(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (files.length < 2) {
                    Toast.makeText(getApplicationContext(), "Brak plików w folderze.", Toast.LENGTH_LONG).show();
                    return;
                }
                addArchive(files, path);
                Toast.makeText(getApplicationContext(), "Dodano archiwum zip", Toast.LENGTH_LONG).show();
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(StorageActivity.this, sendButton);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().toString().compareToIgnoreCase("Email") == 0)
                            sendEmail();
                        else if(item.getTitle().toString().compareToIgnoreCase("Ftp") == 0)
                            sendFtp();
                        else if(item.getTitle().toString().compareToIgnoreCase("Email & FTP") == 0) {
                            sendFtp();
                            sendEmail();
                        }

                        return true;
                    }
                });

                popup.show();
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
            Toast.makeText(getApplicationContext(), "Odtwarzam... " + file.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(File file) {
        FileItem fileItem = findFileItemInTheSetByName(file.getName());
        if (fileItem != null) {
            fileItems.remove(fileItem);
        }
        file.delete();
        startActivity(getIntent());
        finish();
    }

    private void addArchive(File[] files, String path) {
        ZipManager archive = new ZipManager();
        List<String> fileNamesList = new ArrayList<>();
        for (File f : files) {
            fileNamesList.add(path + f.getName());
        }
        String[] fileNames = fileNamesList.toArray(new String[0]);
        archive.zip(fileNames, path + name + ".zip");
        startActivity(getIntent());
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void send(View view) {
        Toast.makeText(getApplicationContext(), "Popup here", Toast.LENGTH_SHORT).show();
        PopupWindow popupWindow = new PopupWindow(getApplicationContext());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setElevation(10);
        popupWindow.setContentView(view);

    }

    private void selectCheckBoxes(List<String> checkedFileNames) {
        for (FileItem fileItem : fileItems) {
            if (checkedFileNames.contains(fileItem.getName())) {
                fileItem.setChecked(true);
            }
        }
    }

    private FileItem findFileItemInTheSetByName(String name) {
        for (FileItem fileItem : fileItems) {
            if (fileItem.getName().equals(name)) {
                return fileItem;
            }
        }
        return null;
    }


    private void sendEmail() {

        sizeSelectedItems = 0.0;
        Log.i("^^^^^^^^^^^^^^", path.toString());
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (checkedFileNames.contains(file.getName())) {
                sizeSelectedItems += file.length();
            }
        }

        if(sizeSelectedItems <= 10000000) {
            checkedFileNames.clear();
            for (FileItem fileItem : fileItems) {
                if (fileItem.isChecked()) {
                    checkedFileNames.add(fileItem.getName());
                }
            }


            ArrayList<Uri> filesUri = new ArrayList<>();
            directory = new File(path);

            files = directory.listFiles();

            for (File file : files) {
                if (checkedFileNames.contains(file.getName())) {
                    filesUri.add(Uri.fromFile(file));
                }
            }

            File file = new File(path, "email.txt");

            Scanner in = null;
            try {
                in = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            List<String> addresses = new ArrayList<>();
            if (file.exists()) {
                mailSubject = in.nextLine();
                while (in.hasNext()) {
                    addresses.add(in.nextLine());
                }


                if (addresses.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Brak podanych maili!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String a[] = new String[0];
                Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
                email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, filesUri);
                email.putExtra(Intent.EXTRA_EMAIL, addresses.toArray(a));
                email.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
                email.putExtra(Intent.EXTRA_TEXT, emailContent);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Rozmiar zaznaczonych plików większy niż 10 MB.", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendFtp(){
        SharedPreferences sharedPref= getSharedPreferences("defaultFTP.xml", 0);
        FTP ftp = new FTP(sharedPref.getString("Custom_hostname", getString(R.string.default_hostname)),
                sharedPref.getString("Custom_login", getString(R.string.default_login)),
                sharedPref.getString("Custom_password", getString(R.string.default_password)),
                sharedPref.getString("Custom_directory", getString(R.string.default_directory)),
                recorderName,
                meetingName);
        ftp.execute();
        Toast.makeText(StorageActivity.this,"Wysłano na serwer FTP.",Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent data = new Intent();
            checkedFileNames = new ArrayList<>();
            for (FileItem fileItem : fileItems) {
                if (fileItem.isChecked()) {
                    checkedFileNames.add(fileItem.getName());
                }
            }
            data.putStringArrayListExtra("checkedFileNames", checkedFileNames);
            setResult(RESULT_OK, data);
        }
        return super.onKeyDown(keyCode, event);
    }
}
