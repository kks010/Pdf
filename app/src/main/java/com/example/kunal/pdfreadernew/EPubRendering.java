package com.example.kunal.pdfreadernew;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButton;

import com.github.mertakdut.BookSection;
import com.github.mertakdut.CssStatus;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

/**
 * Created by Kunal on 02-01-2017.
 */
public class EPubRendering extends AppCompatActivity implements PageFragment.OnFragmentReadyListener,View.OnTouchListener {

    String path = null;
    Reader reader;
    ViewPager mViewPager;

    int getPosition;
    float textSize = 12;
    int startSelection;
    int endSelection;
    String selectedText;

    Drawable imageAsDrawable;

    TextView textView;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private int pageCount = Integer.MAX_VALUE;
    private int pxScreenWidth;

    private boolean isPickedWebView = false;

    private boolean isSkippedToPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_epub_render);

        getSupportActionBar().setTitle("EPub Reader");

        pxScreenWidth = getResources().getDisplayMetrics().widthPixels;

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //UNPACK OUR DATA FROM INTENT
        Intent intent = this.getIntent();

        if (intent.getAction() != null) {
            path = intent.getData().getPath();
        } else {
            path = intent.getExtras().getString("PATH");

        }

        try {
            reader = new Reader();

            // Setting optionals once per file is enough.
            reader.setMaxContentPerSection(1250);
            reader.setCssStatus(isPickedWebView ? CssStatus.INCLUDE : CssStatus.OMIT);
            reader.setIsIncludingTextContent(true);
            reader.setIsOmittingTitleTag(true);

            // This method must be called before readSection.
            reader.setFullContent(path);

//                int lastSavedPage = reader.setFullContentWithProgress(filePath);
            if (reader.isSavedProgressFound()) {
                int lastSavedPage = reader.loadProgress();
                mViewPager.setCurrentItem(lastSavedPage);
            }

        } catch (ReadingException e) {
            Toast.makeText(EPubRendering.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }


    public View onFragmentReady(int position) {

        getPosition=position;

        BookSection bookSection = null;

        try {
            bookSection = reader.readSection(position);
        } catch (ReadingException e) {
            e.printStackTrace();
            Toast.makeText(EPubRendering.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (OutOfPagesException e) {
            e.printStackTrace();
            this.pageCount = e.getPageCount();

            if (isSkippedToPage) {
                Toast.makeText(EPubRendering.this, "Max page number is: " + this.pageCount, Toast.LENGTH_LONG).show();
            }

            mSectionsPagerAdapter.notifyDataSetChanged();
        }

        isSkippedToPage = false;

        if (bookSection != null) {
            return setFragmentView(isPickedWebView, bookSection.getSectionContent(), "text/html", "UTF-8"); // reader.isContentStyled
        }

        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            reader.saveProgress(mViewPager.getCurrentItem());
            Toast.makeText(EPubRendering.this, "Saved page: " + mViewPager.getCurrentItem() + "...", Toast.LENGTH_LONG).show();
        } catch (ReadingException e) {
            e.printStackTrace();
            Toast.makeText(EPubRendering.this, "Progress is not saved: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (OutOfPagesException e) {
            e.printStackTrace();
            Toast.makeText(EPubRendering.this, "Progress is not saved. Out of Bounds. Page Count: " + e.getPageCount(), Toast.LENGTH_LONG).show();
        }
    }

    private View setFragmentView(boolean isContentStyled, String data, String mimeType, String encoding) {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (isContentStyled) {
            WebView webView = new WebView(EPubRendering.this);
            webView.loadDataWithBaseURL(null, data, mimeType, encoding, null);


            webView.setLayoutParams(layoutParams);
            webView.getSettings().setBuiltInZoomControls(true);

            return webView;
        } else {
            ScrollView scrollView = new ScrollView(EPubRendering.this);
            scrollView.setLayoutParams(layoutParams);

            textView = new TextView(EPubRendering.this);
            textView.setLayoutParams(layoutParams);

            textView.setText(Html.fromHtml(data, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    String imageAsStr = source.substring(source.indexOf(";base64,") + 8);
                    byte[] imageAsBytes = Base64.decode(imageAsStr, Base64.DEFAULT);
                    Bitmap imageAsBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                    int imageWidthStartPx = (pxScreenWidth - imageAsBitmap.getWidth()) / 2;
                    int imageWidthEndPx = pxScreenWidth - imageWidthStartPx;

                    imageAsDrawable = new BitmapDrawable(getResources(), imageAsBitmap);
                    imageAsDrawable.setBounds(imageWidthStartPx, 0, imageWidthEndPx, imageAsBitmap.getHeight());



//                    ImageView imageView= (ImageView)findViewById(R.id.expanded_image);
//                    imageView.setBackground(imageAsDrawable);

                    return imageAsDrawable;
                }
            }, null));

            int pxPadding = dpToPx(12);

            textView.setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
            textView.setTextColor(getColor(R.color.black));
            textView.setTextSize(textSize);
            textView.setTextIsSelectable(true);

//            startSelection = textView.getSelectionStart();
//            endSelection=textView.getSelectionEnd();
//
//            selectedText = String.valueOf(textView.getText().subSequence(startSelection,endSelection));
//            if(startSelection!=endSelection){
//                Toast.makeText(getApplicationContext(),selectedText,Toast.LENGTH_SHORT).show();
//            }

            scrollView.addView(textView);
            return scrollView;
        }
    }


    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            textSize = textSize - 2;
            textView.setTextSize(textSize);

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            textSize = textSize + 2;
            textView.setTextSize(textSize);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return PageFragment.newInstance(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.epub_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.white:
                mViewPager.setBackgroundColor(getColor(R.color.white));
                return true;

            case R.id.sepia:
                mViewPager.setBackgroundColor(getColor(R.color.sepia));
                return true;

            case R.id.six:
                textSize=6;

//                PageFragment p = new PageFragment();
//                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.container,p);
//                transaction.commit();

                return true;
            case R.id.eight:
                textSize=8;
                onFragmentReady(getPosition);
                return true;
            case R.id.ten:
                textSize=10;
                onFragmentReady(getPosition);
                return true;
            case R.id.twelve:
                textSize=12;
                onFragmentReady(getPosition);
                return true;
            case R.id.fourteen:
                textSize=14;
                onFragmentReady(getPosition);
                return true;
            case R.id.sixteen:
                textSize=16;
                onFragmentReady(getPosition);
                return true;
            case R.id.eighteen:
                textSize=18;
                onFragmentReady(getPosition);
                return true;
            case R.id.twenty:
                textSize=20;
                onFragmentReady(getPosition);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


