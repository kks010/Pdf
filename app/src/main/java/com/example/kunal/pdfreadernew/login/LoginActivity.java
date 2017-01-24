package com.example.kunal.pdfreadernew.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kunal.pdfreadernew.MainActivity;
import com.example.kunal.pdfreadernew.R;
import com.example.kunal.pdfreadernew.StartScreen.StartActivity;

/**
 * Created by Kunal on 24-01-2017.
 */

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    Button newuserButton;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        username=(EditText)findViewById(R.id.loginUsername);
        password=(EditText)findViewById(R.id.loginPassword);

        loginButton = (Button)findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                username.setText("");
                password.setText("");
            }
        });

        newuserButton = (Button)findViewById(R.id.new_user);
        newuserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);

                username.setText("");
                password.setText("");
            }
        });

    }
}
