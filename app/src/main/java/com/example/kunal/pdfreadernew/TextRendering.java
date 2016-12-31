package com.example.kunal.pdfreadernew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kunal on 29-12-2016.
 */
public class TextRendering extends Activity implements LangConverter.LangConverterListner {

    TextView textView;
    public int done=0;
    String finalString = "";
    public String[] arraySpinner = new String[] {"<Select language>","English","Hindi","Spanish","Russian"};
    public String langCode;

    public ProgressDialog dialog;

    public String translatedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_text_render);

        textView = (TextView) findViewById(R.id.text_file_text_view);

        Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

//        dialog = new ProgressDialog(getApplicationContext());
//        dialog.setMessage("Translating..");
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setIndeterminate(true);
//        dialog.setCancelable(true);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    langCode = "en";
                    LangConverter converter =  new LangConverter(TextRendering.this);
                    converter.execute(new LangRequest(finalString,langCode));
                } else if (position == 2) {
                    langCode = "hi";
                    LangConverter converter =  new LangConverter(TextRendering.this);
                    converter.execute(new LangRequest(finalString,langCode));
                } else if (position == 3) {
                    langCode = "es";
                    LangConverter converter =  new LangConverter(TextRendering.this);
                    converter.execute(new LangRequest(finalString,langCode));
                } else if (position == 4) {
                    langCode = "ru";
                    LangConverter converter =  new LangConverter(TextRendering.this);
                    converter.execute(new LangRequest(finalString,langCode));
                }


//        dialog.show();

//        dialog.cancel();


        }

        @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(done==0) {
            display();
            done++;
        }
    }

    public void display() {

        try {

            String path = null;

            //UNPACK OUR DATA FROM INTENT
            Intent intent = this.getIntent();

            if (intent.getAction() != null) {
                path = intent.getData().getPath();
            } else {
                path = intent.getExtras().getString("PATH");

            }

            File file = new File(path);
            String[] loadText = Load(file);

            for (int i = 0; i < loadText.length; i++) {
                finalString += loadText[i] + System.getProperty("line.separator");
            }

            textView.setText(finalString);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("kunal", " " + e);
        }
    }

    public static String[] Load(File file) {
        FileInputStream fis = null;

        try {

            fis=new FileInputStream(file);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        InputStreamReader isr= new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int a=0;

        try {
            while ((test=br.readLine())!= null){
                a++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            fis.getChannel().position(0);
        }catch (Exception e){
            e.printStackTrace();
        }

        String[] array = new String[a];

        String line;
        int i=0;
        try {
            while ((line=br.readLine())!=null){
                array[i]=line;
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return array;
    }


    @Override
    public void getinfo(LangResponse langResponse) {
        translatedString=langResponse.translatedString;

        textView.setText(translatedString);
    }
}
