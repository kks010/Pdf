package com.example.kunal.pdfreadernew;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kunal.pdfreadernew.epub.EPubRendering;
import com.example.kunal.pdfreadernew.html.HtmlRendering;
import com.example.kunal.pdfreadernew.pdf.PDFReaderActivity;
import com.example.kunal.pdfreadernew.text.TextRendering;

import java.util.ArrayList;

/**
 * Created by Kunal on 17-12-2016.
 */
public class CustomAdapter extends BaseAdapter {

    Context c;
    ArrayList<Doc> docs;
    String bookName;

    public CustomAdapter(Context c, ArrayList<Doc> docs) {
        this.c = c;
        this.docs = docs;
    }

    @Override
    public int getCount() {
        return docs.size();
    }

    @Override
    public Object getItem(int i) {
        return docs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            //INFLATE CUSTOM LAYOUT
            view= LayoutInflater.from(c).inflate(R.layout.card_view_list,viewGroup,false);
        }

        final Doc doc= (Doc) this.getItem(i);

        TextView nameTxt= (TextView) view.findViewById(R.id.pdf_name);
        ImageView img = (ImageView) view.findViewById(R.id.pdf_photo);


        bookName=doc.getName();
        //BIND DATA
        nameTxt.setText(bookName);
        img.setImageResource(R.drawable.book);

        //VIEW ITEM CLICK
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openView(doc.getPath());
            }
        });
        return view;
    }

    //OPEN VIEW
    private void openView(String path)
    {

        if(path.endsWith("pdf")) {
            Intent i = new Intent(c, PDFReaderActivity.class);
            i.putExtra("PATH", path);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
        }
        if(path.endsWith("txt")){
            Intent i = new Intent(c, TextRendering.class);
            i.putExtra("PATH", path);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);

        }
        if(path.endsWith("epub")){
            Intent i = new Intent(c, EPubRendering.class);
            i.putExtra("PATH", path);
            i.putExtra("BOOK_NAME",bookName);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);

        }
        if(path.endsWith("html")){
            Intent i = new Intent(c, HtmlRendering.class);
            i.putExtra("PATH", path);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);

        }
    }
}