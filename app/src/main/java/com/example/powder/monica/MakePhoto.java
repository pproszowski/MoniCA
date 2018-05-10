package com.example.powder.monica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.callback.Callback;


public class MakePhoto extends Activity {

    private Activity activity;
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String recorderName;
    private String meetingName;
    private String choosenPriority;
    private File newNameFile = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choosenPriority = getIntent().getExtras().getString("choosenPriority");
        recorderName = getIntent().getExtras().getString("recorderName");
        meetingName = getIntent().getExtras().getString("Name");
        dispatchTakePictureIntent();

    }



    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            Callback ic;


            try {
                photoFile = createImageFile();

                String newName = choosenPriority + "Img "
                        + Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"꞉"
                        + Calendar.getInstance().get(Calendar.MINUTE) +"꞉"
                        + Calendar.getInstance().get(Calendar.SECOND) + ".jpg";


                newNameFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName, newName);
                photoFile.renameTo(newNameFile);

                if (newNameFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(activity,
                            "com.example.powder.monica.fileprovider",
                            newNameFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == RESULT_OK) {
                try {
                    compressImage(newNameFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == RESULT_CANCELED){
                newNameFile.delete();
            }
        }
        finish();
    }


    private File createImageFile() throws IOException {
        String filepath = Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = new File(filepath);
        if (!storageDir.exists()) {
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

    protected File getPictureFile(){
        return newNameFile;
    }

    protected void compressImage(File newNameFile) throws IOException {
        Log.i("^^^^^^^^^^^^^^^^^^^^", "Compress..");
        // we'll start with the original picture already open to a file
        File imgFileOrig = newNameFile; //change "getPic()" for whatever you need to open the image file.

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath(), options);




// original measurements
        int origWidth = b.getWidth();
        int origHeight = b.getHeight();

        final int destWidth = 1080;//or the width you need

        if (origWidth > destWidth) {
            // picture is wider than we want it, we calculate its target height
            int destHeight = origHeight / (origWidth / destWidth);
            // we create an scaled bitmap so it reduces the image, not just trim it
            Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            // compress to the format you want, JPEG, PNG...
            // 70 is the 0-100 quality percentage
            b2.compress(Bitmap.CompressFormat.JPEG, 85, outStream);
            // we save the file, at least until we have made use of it
            File f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName, newNameFile.getName());
            f.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            // remember close de FileOutput
            fo.close();
        }
    }


}



