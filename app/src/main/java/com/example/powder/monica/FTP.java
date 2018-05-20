package com.example.powder.monica;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FTP extends AsyncTask<Void, Void, Void> {

    private Exception exception;
    private String hostname;
    private String login;
    private String password;
    private String directoryPath;
    private String recorderName;
    private String meetingName;
    private FileInputStream data;
    private BufferedInputStream buffIn;


    public FTP(String hostname, String login, String password, String directory, String recorderName, String meetingName) {
        super();
        this.hostname = hostname;
        this.login = login;
        this.password = password;
        this.directoryPath = directory;
        this.recorderName = recorderName;
        this.meetingName = meetingName;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        FTPClient client = new FTPClient();
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName;
        File directory = new File(path);
        File[] files = directory.listFiles();


        try {
            client.connect(hostname);
            client.login(login, password);
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            client.makeDirectory(directoryPath + "/" + meetingName + "/");
            client.changeWorkingDirectory("aras.cba.pl/" + meetingName + "/");


            for (File file : files) {
                buffIn = new BufferedInputStream(new FileInputStream(file));
                client.enterLocalPassiveMode();
                client.storeFile(file.getName(), buffIn);
                System.out.println(file.getName());
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }


}


