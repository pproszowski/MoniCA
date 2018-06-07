package com.example.powder.monica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.callback.Callback;


public class MakePhotoActivity extends Activity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private Activity activity;
    private String mCurrentPhotoPath;
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
            File photoFile;

            try {
                photoFile = createImageFile();

                String time = "hh:mm:ss";
                String newName = choosenPriority + "Img "
                        + DateFormat.format(time, Calendar.getInstance().getTime()) + ".jpg";


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

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    compressImage(newNameFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
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

    protected File getPictureFile() {
        return newNameFile;
    }

    protected void compressImage(File newNameFile) throws IOException {

        try {
            File f = newNameFile;
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
           Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
                   null, options);

           final int destWidth = 1000;
           final int destHeight = 1333;

            Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
           Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight , false);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmapScaled.compress(Bitmap.CompressFormat.JPEG, 80,
                    outputStream);


        f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + recorderName + "/" + meetingName, newNameFile.getName());
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(outputStream.toByteArray());
        fo.close();


        } catch (IOException e) {
            Log.w("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w("TAG", "-- OOM Error in setting image");
        }
    }



}



