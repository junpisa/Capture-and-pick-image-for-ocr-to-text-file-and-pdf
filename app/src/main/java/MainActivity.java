package com.example.toonz.video;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by toonz on 5/26/2016.
 * import OpenCv and how to use
 * reference http://stackoverflow.com/questions/27406303/opencv-in-android-studio
 * how to use video/camera
 * reference: http://developer.android.com/reference/android/provider/MediaStore.html
 * http://www.tutorialspoint.com/android/android_camera.html
 */


public class MainActivity extends Activity {
    private Uri uriVideo;
    private String videoPath;
    private ArrayList result;
    private ArrayList<String> imagesPathList;
    private Bitmap yourbitmap;
    private LinearLayout lnrImages;
    ArrayList s;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton btn_camera = (ImageButton) findViewById(R.id.camera1);
        ImageButton btn_many = (ImageButton) findViewById(R.id.manypicture);
        ImageButton btn_gall = (ImageButton) findViewById(R.id.all);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
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
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent in = new Intent(getApplicationContext(), makeocr.class);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {///โหมดถ่ายรูปเดียว
                Bundle extras = data.getExtras();
                // Get the returned image from extra
                Bitmap bmp = (Bitmap) extras.get("data");
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);

////                /////tesseract ocr
                String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tesseract/";
                File dir = new File(DATA_PATH + "tessdata/");
                TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.init(DATA_PATH,"eng");
                baseApi.setImage(bmp);
                String recognizedText = baseApi.getUTF8Text();
                baseApi.end();
                try { ////ทำให้เป็น file text เพื่อจะนำไปทำเป็นpdf
                    File f = new File(path,"test.txt");
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f));
                    outputStreamWriter.write(recognizedText);
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                    Log.e("123", "File write failed: " + e.toString());
                }

                in.putExtra("te",recognizedText);
                startActivity(in);
            } else {///เลือกภาพ
                s = new ArrayList();
                ClipData clipData = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    clipData = data.getClipData();
                }
                //Both approach work

                if(clipData == null){
                    s.add(data.getData().toString());
                    Toast.makeText(getApplicationContext(), "pathpic"+data.getData().toString(), Toast.LENGTH_LONG).show();
                }else{
                    for(int i=0; i<clipData.getItemCount(); i++){
                        Uri uri = clipData.getItemAt(i).getUri();
                        String imagePath = addImageToList(uri);
                        s.add(imagePath);
                    }
                }
                in.putExtra("te","1");
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)s);
                in.putExtra("BUNDLE",args);
                startActivity(in);

            }
        } else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
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
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.toonz.video/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.toonz.video/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}
