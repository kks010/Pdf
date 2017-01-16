package com.example.kunal.pdfreadernew;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * Created by Kunal on 11-01-2017.
 */
public class EPubParser extends AppCompatActivity {

    String path = null;
    Book book = null;

    String linez = null;

    List<String> spineElements;
    List<SpineReference> spineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_epub_render);

        getSupportActionBar().setTitle("EPub Reader");

        WebView webView = (WebView) findViewById(R.id.container);
        webView.getSettings().setJavaScriptEnabled(true);

        CustomWebClient customWebClient = new CustomWebClient(getApplicationContext());
        webView.setWebViewClient(customWebClient);


        //to get Path
        Intent intent = this.getIntent();

        if (intent.getAction() != null) {
            path = intent.getData().getPath();
        } else {
            path = intent.getExtras().getString("PATH");

        }

        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            EpubReader epubReader = new EpubReader();
            book = epubReader.readEpub(fileInputStream);


        } catch (Exception e) {
            Log.e("While loading book ", e.toString());
        }


        Spine spine = book.getSpine();
        spineList = spine.getSpineReferences();

        int count = spineList.size();

//        book.getResources().getAllHrefs();

        StringBuilder string = new StringBuilder();
        for (int i = 0; i < count; i++) {
            Resource res = spine.getResource(i);

            try {
                InputStream is = res.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                String line = null;

                try {
                    while ((line = reader.readLine()) != null) {
                        linez = string.append(line + "\n").toString();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                //do with stream

            } catch (IOException e) {
                e.printStackTrace();
            }

//            linez = linez.replace("../", "");

//            webView.loadDataWithBaseURL(null, linez, "text/html", "UTF-8", null);
            webView.loadData(linez,"text/html","UTF-8");
            webView.getSettings().setBuiltInZoomControls(true);

        }
    }

}
