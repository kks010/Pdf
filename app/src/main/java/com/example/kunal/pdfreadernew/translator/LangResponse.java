package com.example.kunal.pdfreadernew.translator;

import org.json.JSONObject;

/**
 * Created by Kunal on 30-12-2016.
 */
public class LangResponse {

    public String translatedString;

    public LangResponse(JSONObject jsonObject){

        try {
            translatedString = jsonObject.getString("translationText");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
