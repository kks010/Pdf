package com.example.kunal.pdfreadernew;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    ListView listDrawerElements;
    public String[] drawerItems = {"Pdf", "Text","EPub","Html"};

    int Pdf=0;
    int Text=1;
    int EPub=2;
    int Html=3;

    String TAG="kunal";

    TextView textAfterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity_main);

        getSupportActionBar().setTitle("Book Shelf");

        isStoragePermissionGranted();

        final GridView gridView= (GridView)findViewById(R.id.gridView);
        textAfterLogin=(TextView)findViewById(R.id.text_after_login);

        listDrawerElements=(ListView)findViewById(R.id.left_drawer);
        listDrawerElements.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, drawerItems));
        listDrawerElements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                  textAfterLogin.setVisibility(View.INVISIBLE);

//                ProgressDialog pds = new ProgressDialog(MainActivity.this);
//                pds.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                pds.setMessage("Please wait!! Loading PDF");
//                pds.setIndeterminate(true);
//                pds.setCancelable(true);
//                pds.show();

                if(position==Pdf){
                    gridView.setAdapter(new CustomAdapter(getApplicationContext(), getPdfFiles()));
                }else if(position==Text){
                    gridView.setAdapter(new CustomAdapter(getApplicationContext(), getTextFiles()));
                }else if(position==EPub){
                    gridView.setAdapter(new CustomAdapter(getApplicationContext(), getEPubFiles()));
                }else if(position==Html){
                    gridView.setAdapter(new CustomAdapter(getApplicationContext(), getHtmlFiles()));
                }
            }
        });


    }


    public  boolean isStoragePermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }



    public ArrayList<Doc> getPdfFiles() {
        ArrayList<Doc> pdfDocs=new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        Doc pdfDoc;

        if(downloadsFolder.exists())
        {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files=downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i=0;i<files.length;i++)
            {
                File file=files[i];

                if(file.getPath().endsWith("pdf"))
                {
                    pdfDoc=new Doc();
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());

                    pdfDocs.add(pdfDoc);
                }

            }
        }

        return pdfDocs;
    }
    public ArrayList<Doc> getTextFiles() {
        ArrayList<Doc> textDocs=new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        Doc textDoc;

        if(downloadsFolder.exists())
        {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files=downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i=0;i<files.length;i++)
            {
                File file=files[i];

                if(file.getPath().endsWith("txt"))
                {
                    textDoc=new Doc();
                    textDoc.setName(file.getName());
                    textDoc.setPath(file.getAbsolutePath());

                    textDocs.add(textDoc);
                }

            }
        }

        return textDocs;
    }
    public ArrayList<Doc> getEPubFiles() {
        ArrayList<Doc> epubDocs=new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        Doc epubDoc;

        if(downloadsFolder.exists())
        {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files=downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i=0;i<files.length;i++)
            {
                File file=files[i];

                if(file.getPath().endsWith("epub"))
                {
                    epubDoc=new Doc();
                    epubDoc.setName(file.getName());
                    epubDoc.setPath(file.getAbsolutePath());

                    epubDocs.add(epubDoc);
                }

            }
        }

        return epubDocs;
    }
    public ArrayList<Doc> getHtmlFiles() {
        ArrayList<Doc> htmlDocs=new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        Doc htmlDoc;

        if(downloadsFolder.exists())
        {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files=downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i=0;i<files.length;i++)
            {
                File file=files[i];

                if(file.getPath().endsWith("html"))
                {
                    htmlDoc=new Doc();
                    htmlDoc.setName(file.getName());
                    htmlDoc.setPath(file.getAbsolutePath());

                    htmlDocs.add(htmlDoc);
                }

            }
        }

        return htmlDocs;
    }


}