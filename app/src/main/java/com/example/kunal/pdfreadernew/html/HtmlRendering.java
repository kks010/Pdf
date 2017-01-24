package com.example.kunal.pdfreadernew.html;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.webkit.WebView;

import com.example.kunal.pdfreadernew.R;

/**
 * Created by Kunal on 21-01-2017.
 */
public class HtmlRendering extends AppCompatActivity {

    String path=null;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_html);

        getSupportActionBar().setTitle("Html Reader");

        Intent intent = this.getIntent();

        if (intent.getAction() != null) {
            path = intent.getData().getPath();
        } else {
            path = intent.getExtras().getString("PATH");

        }

        webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl(path);
    }
}
