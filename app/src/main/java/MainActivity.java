package com.example.toonz.testopencv;


import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by toonz on 5/26/2016.
 * import OpenCv and how to use
 * reference http://stackoverflow.com/questions/27406303/opencv-in-android-studio
 * how to use video/camera
 * reference: http://developer.android.com/reference/android/provider/MediaStore.html
 * http://www.tutorialspoint.com/android/android_camera.html
 */


public class MainActivity extends Activity {
    private File photoFile;
    Mat gray_img;
    ArrayList s;
    String r;
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton btn_camera = (ImageButton) findViewById(R.id.camera1);
        ImageButton btn_many = (ImageButton) findViewById(R.id.manypicture);
        ImageButton btn_gall = (ImageButton) findViewById(R.id.all);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, 0);
                    }
                }
            }
        });
        btn_many.setOnClickListener(new View.OnClickListener() { ///ถ่ายหลายภาพ
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        btn_gall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent in = new Intent(getApplicationContext(), makeocr.class);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {///โหมดถ่ายรูปเดียว
                r = "";
                performCrop();

            } else if (requestCode == 1) {///เลือกภาพ
                s = new ArrayList();
                ClipData clipData = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    clipData = data.getClipData();
                }
                if (clipData == null) {
                    s.add(data.getData().toString());
                    Toast.makeText(getApplicationContext(), "pathpic" + data.getData().toString(), Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri uri = clipData.getItemAt(i).getUri();
                        String imagePath = addImageToList(uri);
                        s.add(imagePath);
                    }
                }
                in.putExtra("te", "1");
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) s);
                in.putExtra("BUNDLE", args);
                startActivity(in);

            } else{ ///จากโหมดถ่ายภาพเดี่ยว แบบcrop ได้
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                gray_img = new Mat();
                gray_img = Highgui.imread(path + "/ig.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
                Mat re = new Mat();
                Imgproc.adaptiveThreshold(gray_img, re, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
                Bitmap bm = Bitmap.createBitmap(gray_img.width(), gray_img.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(re, bm);
                    File file2 = new File(path, "gray.jpg");

                    FileOutputStream fos2 = null;
                    try {
                        fos2 = new FileOutputStream(file2);
                        if (fos2 != null) {
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
                            fos2.flush();
                            fos2.close();
                            Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tesseract/";
                    File dir = new File(DATA_PATH + "tessdata/");
                    TessBaseAPI baseApi = new TessBaseAPI();
                    baseApi.init(DATA_PATH, "eng");
                    baseApi.setImage(bm);
                    String recognizedText = baseApi.getUTF8Text();
                    baseApi.end();
                    try { ////ทำให้เป็น file text เพื่อจะนำไปทำเป็นpdf
                        File f = new File(path, "test.txt");
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f));
                        outputStreamWriter.write(recognizedText);
                        outputStreamWriter.close();
                    } catch (IOException e) {
                        Log.e("123", "File write failed: " + e.toString());
                    }
                    in.putExtra("te", recognizedText);
                    startActivity(in);

            }
        } else {
            Toast.makeText(getApplicationContext(), "Choose you images.", Toast.LENGTH_LONG).show();
        }
    }


    public void pick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent, 1);
    }

    private String addImageToList(Uri uri) {
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
        cursor.close();
        return imagePath;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".png";
        String storageDir = Environment.getExternalStorageDirectory()
                + "/ExtractReceipt";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();
        File image = new File(storageDir + "/" + imageFileName);
        return image;
    }
    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(Uri.fromFile(photoFile), "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath())+"/ig.jpg")) );
            startActivityForResult(cropIntent, 2);

        }

        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
