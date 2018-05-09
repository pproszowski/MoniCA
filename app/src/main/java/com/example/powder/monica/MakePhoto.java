package com.example.powder.monica;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MakePhoto {

    private Activity activity;
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String recorderName;
    private String meetingName;

    public MakePhoto(Activity activity){
        this.activity = activity;
        recorderName = activity.getIntent().getExtras().getString("recorderName");
        meetingName = activity.getIntent().getExtras().getString("Name");
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            File newNameFile = null;
            try {
                photoFile = createImageFile();
                String newName = "Img "
                        + Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"꞉"
                        + Calendar.getInstance().get(Calendar.MINUTE) +"꞉"
                        + Calendar.getInstance().get(Calendar.SECOND) + ".jpg";


                newNameFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName, newName);
                photoFile.renameTo(newNameFile);

            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (newNameFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.powder.monica.fileprovider",
                        newNameFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        String filepath = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = new File(filepath);
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
