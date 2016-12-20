package com.example.kunal.pdfreadernew;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Kunal on 17-12-2016.
 */
public class PDFReaderActivity extends Activity implements View.OnTouchListener,GestureDetector.OnGestureListener {

    private ImageView imageView;
    public int CurrentPage=0;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        detector =  new GestureDetector(this,this);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        render();               //for pdf rendering

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        detector.onTouchEvent(event);

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {//left swipe
                CurrentPage++;
                render();
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {//right swipe
                CurrentPage--;
                render();
            }
        } catch (Exception e) {
            // nothing
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void render(){
        try{

            imageView=(ImageView)findViewById(R.id.image);
            String path=null;

            //UNPACK OUR DATA FROM INTENT
            Intent i=this.getIntent();

            if(i.getAction()!= null){
                path=i.getData().getPath();
            }
            else {
                path = i.getExtras().getString("PATH");

            }
            int REQ_WIDTH = imageView.getWidth();
            int REQ_HEIGHT = imageView.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_8888);

            //GET THE PDF FILE
            File file=new File(path);
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY));


            if(CurrentPage<0){
                CurrentPage=0;
            }else if(CurrentPage >renderer.getPageCount()){
                CurrentPage=renderer.getPageCount() - 1;
            }

            Matrix m = imageView.getImageMatrix();
            Rect rect= new Rect(0,0,REQ_WIDTH,REQ_HEIGHT);
            renderer.openPage(CurrentPage).render(bitmap, rect, m, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageView.setImageMatrix(m);
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
            imageView.setOnTouchListener(this);



        }catch(Exception e){
            e.printStackTrace();
            Log.e("kunal", " " + e);
        }
    }

}
