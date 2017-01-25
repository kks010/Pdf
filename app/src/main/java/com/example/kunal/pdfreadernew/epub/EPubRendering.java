package com.example.kunal.pdfreadernew.epub;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.example.kunal.pdfreadernew.R;
import com.example.kunal.pdfreadernew.text.TextRendering;
import com.example.kunal.pdfreadernew.translator.LangConverter;
import com.example.kunal.pdfreadernew.translator.LangRequest;
import com.example.kunal.pdfreadernew.translator.LangResponse;
import com.github.mertakdut.BookSection;
import com.github.mertakdut.CssStatus;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 02-01-2017.
 */
public class EPubRendering extends AppCompatActivity implements PageFragment.OnFragmentReadyListener, View.OnTouchListener,LangConverter.LangConverterListner{

    String path = null;
    Reader reader;
    ViewPager mViewPager;

    int getPosition;
    public int textSize;

    public List<Integer> bookmarkList = new ArrayList<Integer>();


    int startSelection;
    int endSelection;
    String selectedText;

    BookSection bookSection;

    Drawable imageAsDrawable;

    TextView textView;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private int pageCount = Integer.MAX_VALUE;
    private int pxScreenWidth;

    ProgressBar progressBar;
    int progress;

    private boolean isPickedWebView = false;

    private boolean isSkippedToPage = false;

    private Menu menu;

    public String bookName;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.d("kunal pos ", String.valueOf(getPosition) + " " + String.valueOf(pageCount));
                progress= (getPosition) * 7;

                progressBar.setProgress(progress);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        View existedView = findViewById(R.id.exist_view);

        existedView.setOnClickListener(new View.OnClickListener() {
            int exist=0;
            @Override
            public void onClick(View v) {
                if(exist%2==0) {
//                    getSupportActionBar().setTitle(path);
                    getSupportActionBar().setTitle(Html.fromHtml
                            ("<font color=\"black\">" + " " + bookName + "</font>"));
                    getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.white));
                    menu.getItem(0).setIcon(R.drawable.whitebookmark);
                    exist++;
                }else {
                    getSupportActionBar().setTitle(Html.fromHtml
                            ("<font color=\"white\">" + "EPub Reader" + "</font>"));
                    menu.getItem(0).setIcon(R.drawable.bookmark);
                   getSupportActionBar().setBackgroundDrawable(getDrawable(R.color.colorPrimary));
                    exist++;
                }
            }
        });

        //UNPACK OUR DATA FROM INTENT
        Intent intent = this.getIntent();

        if (intent.getAction() != null) {
            path = intent.getData().getPath();
        } else {
            path = intent.getExtras().getString("PATH");

        }
        bookName=intent.getExtras().getString("BOOK_NAME");
        bookName = bookName.substring(0, bookName.length() - 5);
        bookName=bookName.toUpperCase();


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


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("EPubRendering Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
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

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public View onFragmentReady(int position) {

        getPosition = position;

        bookSection = null;

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
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    public View setFragmentView(boolean isContentStyled, String data, String mimeType, String encoding) {

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

                    return imageAsDrawable;
                }
            }, null));

            int pxPadding = dpToPx(12);
            textView.setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
            textView.setTextColor(getColor(R.color.black));

            textView.setLinksClickable(true);

            if (textSize == 0) {
                textSize = 12;
            }

            textView.setTextSize(textSize);
            textView.setTextIsSelectable(true);

//            int min = 0;
//            int max = textView.getText().length();
//            if (textView.isFocused()) {
//                final int selStart = textView.getSelectionStart();
//                final int selEnd = textView.getSelectionEnd();
//
//                min = Math.max(0, Math.min(selStart, selEnd));
//                max = Math.max(0, Math.max(selStart, selEnd));
//            }
//            // Perform your definition lookup with the selected text
//            final CharSequence selectedText = textView.getText().subSequence(min, max);
//            // Finish and close the ActionMode
//
//            String langCode = "hi";
//            LangConverter converter =  new LangConverter(EPubRendering.this);
//            converter.execute(new LangRequest(selectedText.toString(),langCode));

            scrollView.addView(textView);
            return scrollView;

        }
    }

    @Override
    public void getinfo(LangResponse langResponse) {
        String translatedString=langResponse.translatedString;

        Toast.makeText(getApplicationContext(),translatedString,Toast.LENGTH_LONG);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
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
                textSize = 6;

                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextSize(textSize);

                return true;

            case R.id.eight:
                textSize = 8;

                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextSize(textSize);

                return true;

            case R.id.ten:
                textSize = 10;

                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextSize(textSize);

                return true;
            case R.id.twelve:
                textSize = 12;

                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextSize(textSize);

                return true;
            case R.id.fourteen:
                textSize = 14;

                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextSize(textSize);

                return true;
            case R.id.sixteen:
                textSize = 16;

                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextSize(textSize);
                return true;
            case R.id.eighteen:
                textSize = 18;

                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextSize(textSize);

                return true;
            case R.id.twenty:
                textSize = 20;

                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextSize(textSize);

                return true;


            case R.id.Simple:
                mViewPager.getAdapter().notifyDataSetChanged();

                return true;

            case R.id.transformationUp:
                mViewPager.setPageTransformer(true, new RotateUpTransformer());

                return true;

            case R.id.transformationDown:
                mViewPager.setPageTransformer(true, new RotateDownTransformer());

                return true;

            case R.id.curl:

                return true;

            case R.id.black:
                mViewPager.getAdapter().notifyDataSetChanged();
                textView.setTextColor(getColor(R.color.black));

                return true;
            case R.id.red:

                textView.setTextColor(getColor(R.color.red));
                mViewPager.getAdapter().notifyDataSetChanged();

                return true;
            case R.id.blue:
                textView.setTextColor(getColor(R.color.blue));
                mViewPager.getAdapter().notifyDataSetChanged();

                return true;
            case R.id.green:
                textView.setTextColor(getColor(R.color.green));
                mViewPager.getAdapter().notifyDataSetChanged();

                return true;

            case R.id.bookmark:

                bookmarkList.add(getPosition);

                Toast.makeText(getApplicationContext(),"Page "+getPosition+" added to bookmarks",Toast.LENGTH_LONG).show();
                mViewPager.getAdapter().notifyDataSetChanged();
                return true;

            case R.id.bookmarks:
                return true;


            case R.id.normalStyle:
                textView.setTypeface(null, Typeface.NORMAL);
                mViewPager.getAdapter().notifyDataSetChanged();
                return true;
            case R.id.bold:
                textView.setTypeface(null, Typeface.BOLD);
                mViewPager.getAdapter().notifyDataSetChanged();
                return true;
            case R.id.italic:
                textView.setTypeface(null, Typeface.ITALIC);
                mViewPager.getAdapter().notifyDataSetChanged();
                return true;
            case R.id.boldItalic:
                textView.setTypeface(null, Typeface.BOLD_ITALIC);
                mViewPager.getAdapter().notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}


