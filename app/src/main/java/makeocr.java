package com.example.toonz.testopencv;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class makeocr extends Activity {
    private List <MatOfPoint> lContours = new ArrayList<MatOfPoint>();
    Mat rgb_img,gray_img,thres_img,thres_img2,cont_img,rgb_img2;

    String ty = "";
    String str;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeocr);
        ImageButton btn_text= (ImageButton) findViewById(R.id.text);
        ImageButton btn_pdf = (ImageButton) findViewById(R.id.pdf);
        bundle = getIntent().getExtras();
        final String pth = bundle.getString("te");
        if(pth.equals("1")){ //ถ้ามาจากโหมดเลือกภาพ
            Intent intent = getIntent();
            Bundle args = intent.getBundleExtra("BUNDLE");
            ArrayList<Object> object = (ArrayList<Object>) args.getSerializable("ARRAYLIST");
            str = mul(object);
            String pt = Environment.getExternalStorageDirectory().getAbsolutePath();
            try { ////ทำให้เป็น file text เพื่อจะนำไปทำเป็นpdf
                File f = new File(pt,"test.txt");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f));
                outputStreamWriter.write(str);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("123", "File write failed: " + e.toString());
            }
        }
        btn_text.setOnClickListener(new View.OnClickListener() {
            ////show text ที่ผ่านการocr
            @Override
            public void onClick(View view) {
                ///ไปเรียกtext เพื่อแสดงactivity
                if(pth.equals("1")){ //ถ้ามาจากโหมดเลือกภาพ
                    textt(str);
                }else {
                    textt(pth);
                }
            }
        });
        btn_pdf.setOnClickListener(new View.OnClickListener() {
            ////show pdf ไฟล์
            @Override
            public void onClick(View view) {
                pdf g = new pdf();
                g.pdft();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                File pdfFile = new File(path,"/sample.pdf");
                Uri path2 = Uri.fromFile(pdfFile);
                Toast.makeText(getApplicationContext(), "Pdf file created.", Toast.LENGTH_LONG).show();
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path2, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(pdfIntent);
            }
        });
    }
    public void textt(String str){
        Intent i = new Intent(getApplicationContext(), text.class);
        i.putExtra("te",str);
        startActivity(i);
    }
    public String mul(ArrayList name){ ///ลิสชื่อจากการเลือกหลายภาพ
        String recognizedText="";
        for(int i=0;i<name.size();i++){
            Toast.makeText(getApplicationContext(), "pathpic"+name.get(i).toString(), Toast.LENGTH_LONG).show();
            File photoPath = new File(name.get(i).toString());

            gray_img = new Mat();
            gray_img = Highgui.imread(photoPath.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
            Mat re = new Mat();
            Imgproc.adaptiveThreshold(gray_img, re, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
            Bitmap bmp = Bitmap.createBitmap(gray_img.width(), gray_img.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(re, bmp);


            //Bitmap bmp = BitmapFactory.decodeFile(photoPath.getAbsolutePath());
            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            //bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            /////tesseract ocr
            File DATA_PATH = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"tesseract");
            TessBaseAPI baseApi = new TessBaseAPI();
            baseApi.init(DATA_PATH.getAbsolutePath(),"eng");
            baseApi.setImage(bmp);
            recognizedText += baseApi.getUTF8Text();
            baseApi.end();
        }
        return recognizedText;
    }


}

