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
import com.rylow.eispbustracker.network.ConnecterAsyncTask;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

/**
 * Created by s.bakhti on 30.3.2016.
 */
public class LoginActivity extends AppCompatActivity implements Serializable {



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


                        Connect connect = Connect.getInstance();
                        connect.setPassword(password);
                        connect.setUsername(username);

                        ConnecterAsyncTask connecter = new ConnecterAsyncTask();
                        connecter.execute(0);

                        try {
                            if (connecter.get()){

                                Intent intent = new Intent(LoginActivity.this, LineSelectionActivity.class);

                                startActivity(intent);
                                finish();


                            }

                            else{

                                final Context context = getApplicationContext();
                                final CharSequence message = "Login Failed. Either your password is incorrect or server is not available";
                                final int duration = Toast.LENGTH_SHORT;

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(context, message, duration);
                                        toast.show();
                                    }
                                });

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }
                return true;
            }
        });


    }




    }
