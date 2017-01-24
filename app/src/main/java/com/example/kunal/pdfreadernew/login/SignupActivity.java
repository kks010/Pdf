package com.example.kunal.pdfreadernew.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.kunal.pdfreadernew.MainActivity;
import com.example.kunal.pdfreadernew.R;


/**
 * Created by Kunal on 24-01-2017.
 */

public class SignupActivity extends AppCompatActivity {

    Button signupButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_user);

        signupButton=(Button)findViewById(R.id.signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }
}
