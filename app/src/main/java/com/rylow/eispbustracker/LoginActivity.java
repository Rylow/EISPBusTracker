package com.rylow.eispbustracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rylow.eispbustracker.network.Connect;

/**
 * Created by s.bakhti on 30.3.2016.
 */
public class LoginActivity extends AppCompatActivity {


    private Connect connect;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText textUsername = (EditText) findViewById(R.id.inputUsername);
        final EditText textPassword = (EditText) findViewById(R.id.inputPassword);

        final ImageView imageLogin = (ImageView) findViewById(R.id.imageViewLogin);
        imageLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        username = textUsername.getText().toString();
                        password = textPassword.getText().toString();


                        connect = new Connect(username, password, LoginActivity.this);

                        Thread connectThread = new Thread(connect);
                        connectThread.start();

                        break;
                    }
                }
                return true;
            }
        });


    }




    }
