package com.example.kunal.pdfreadernew;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kunal on 30-12-2016.
 */
public class LangConverter extends AsyncTask<LangRequest,Void,LangResponse> {

    public LangConverterListner listner;
    public String API_URL="http://www.transltr.org/api/translate";

    public LangConverter(Context context) {
        if(context instanceof LangConverterListner){
            listner= (LangConverterListner) context;
        }
        else throw new RuntimeException();
    }

    @Override
    protected LangResponse doInBackground(LangRequest... params) {

        LangRequest request = params[0];
        JSONObject jsonObject = null;//todo

        try {
            String urlString = API_URL + "?text=" + request.finalString + "&from=" + "en" + "&to=" + request.langCode;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); //take info
            InputStream stream = connection.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            String totalResponse = "";
            String line;

            while ((line = r.readLine()) != null) {
                totalResponse += line;
            }

            jsonObject = new JSONObject(totalResponse);

            LangResponse response = new LangResponse(jsonObject);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("kunal", " " + e);
        }


        return new LangResponse(jsonObject);
    }


    @Override
    protected void onPostExecute(LangResponse langResponse) {

        if ( langResponse==null) {

        } else {
            listner.getinfo(langResponse);
        }
    }


    public interface LangConverterListner{

        void getinfo(LangResponse langResponse);

    }
}
