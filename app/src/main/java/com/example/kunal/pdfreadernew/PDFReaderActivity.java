package com.example.kunal.pdfreadernew;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static android.widget.Toast.*;

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

    Bitmap bitmap;

    private TessOCR mTessOCR;
    private ProgressDialog mProgressDialog;

    public static final String lang = "eng";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/DemoOCR/";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        imageView=(ImageView)findViewById(R.id.image);

        detector =  new GestureDetector(this,this);


        //checking for language and drecting language path.
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v("Main", "ERROR: Creation of directory " + path + " on sdcard failed");
                    break;
                } else {
                    Log.v("Main", "Created directory " + path + " on sdcard");
                }
            }

        }
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();

                InputStream in = assetManager.open(lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                // Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                // Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }


        }

        mTessOCR = new TessOCR();

    }


    //Point at which rendering takes place
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
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(PDFReaderActivity.this);
//        builder.setMessage("Show in Text");
//        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//
//                Intent i = new Intent(PDFReaderActivity.this,TextActivity.class);
//                i.putExtra("image",byteArray);
//                startActivity(i);
//            }
//        });
//
//        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //do nothing
//            }
//        });
//
//        AlertDialog alert = builder.create();
//        alert.show();

        doOCR(convertColorIntoBlackAndWhiteImage(bitmap));
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
    void render() {
        try {
//            final Animation zoomAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom);
            String path = null;

            //UNPACK OUR DATA FROM INTENT
            Intent i = this.getIntent();

            if (i.getAction() != null) {
                path = i.getData().getPath();
            } else {
                path = i.getExtras().getString("PATH");

            }
            int REQ_WIDTH = imageView.getWidth();
            int REQ_HEIGHT = imageView.getHeight();

            bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_8888);

            //GET THE PDF FILE
            File file = new File(path);
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));


            if (CurrentPage < 0) {
                CurrentPage = 0;
            }else if (CurrentPage > renderer.getPageCount()) {
                CurrentPage = renderer.getPageCount() - 1;
            }



            Matrix m = imageView.getImageMatrix();
            Rect rect = new Rect(0, 0, REQ_WIDTH, REQ_HEIGHT);
            renderer.openPage(CurrentPage).render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageView.setImageMatrix(m);
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();

            //imageView.startAnimation(zoomAnimation);

            imageView.setOnTouchListener(this);


        }catch (Exception e) {
            e.printStackTrace();
            Log.e("kunal", " " + e);
        }
    }

    public void doOCR(final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "Processing",
                    "Please wait...", true);
            // mResult.setVisibility(V.ViewISIBLE);


        }
        else {
            mProgressDialog.show();
        }

        new Thread(new Runnable() {
            public void run() {

                final String result = mTessOCR.getOCRResult(bitmap).toLowerCase();


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (result != null && !result.equals("")) {
                            String s = result.trim();
                            //save as text
                            writeToFile(result);


                        }

                        mProgressDialog.dismiss();
                    }

                });

            };
        }).start();


    }


    private Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);

        Bitmap blackAndWhiteBitmap = orginalBitmap.copy(
                Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setColorFilter(colorMatrixFilter);

        Canvas canvas = new Canvas(blackAndWhiteBitmap);
        canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

        return blackAndWhiteBitmap;
    }

    public void writeToFile(String data) {
        // Get the directory for the user's public pictures directory.
        verifyStoragePermissions(PDFReaderActivity.this);
        File path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(path, "ToText.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {

            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();

            NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Text File Saved");
            builder.setContentText(path.toString());

            Notification notification = builder.build();
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;

            nm.notify(1, notification);
        }
        catch (Exception e)
        {
            Log.e("kunal", "File write failed: " + e.toString());
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}