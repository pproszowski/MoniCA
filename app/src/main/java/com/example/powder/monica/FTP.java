package com.example.powder.monica;

import android.os.AsyncTask;
import android.os.Environment;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FTP extends AsyncTask <Void,Void,Void>{

    private Exception exception;
    private String recorderName;
    private String recorderName2;
    private FileInputStream data;
    private BufferedInputStream buffIn;

    FTP(String recorderName,String recorderName2)
    {
    super();
    this.recorderName=recorderName;
    this.recorderName2=recorderName2;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        FTPClient client = new FTPClient();
        String path = Environment.getExternalStorageDirectory().getPath() +"/"+recorderName+"/"+recorderName2;
        File directory = new File(path);
        File[] files = directory.listFiles();


        try {
            client.connect("ftp.mkwk018.cba.pl");
            client.login("aras112", "zaq1@WSX");
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            client.makeDirectory("aras.cba.pl/"+recorderName2+"/");
            client.changeWorkingDirectory("aras.cba.pl/"+recorderName2+"/");




            for (File file : files) {
                buffIn = new BufferedInputStream(new FileInputStream(file));
                client.enterLocalPassiveMode();
                client.storeFile(file.getName(), buffIn);
                System.out.println(file.getName());
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    return  null;
    }



    }


