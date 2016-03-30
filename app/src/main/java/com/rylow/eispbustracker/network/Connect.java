package com.rylow.eispbustracker.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.rylow.eispbustracker.LoginActivity;
import com.rylow.eispbustracker.MainActivity;
import com.rylow.eispbustracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;


public class Connect implements Runnable, Serializable {

    String sessionKey, username, password;
    LoginActivity loginActivity;

    public Connect (String username, String password, LoginActivity loginActivity){

        this.username = username;
        this.password = password;
        this.loginActivity = loginActivity;

    }

    @Override
    public void run(){

        try {
            Socket clientSocket = new Socket();

            clientSocket.connect(new InetSocketAddress("193.85.228.2", 6788), 1000);

            BufferedReader inFromServer;
            BufferedWriter outToServer;

            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));


            try {
                this.sessionKey = TwoFish.decrypt(inFromServer.readLine(), "VeryC0oLVeryC0oL").trim();

                JSONObject json = new JSONObject();
                json.put("code", TransmissionCodes.USER_LOGIN);
                json.put("username", username);
                json.put("password", password);

                outToServer.write(TwoFish.encrypt(json.toString(), sessionKey));
                outToServer.newLine();
                outToServer.flush();

                String authreply = inFromServer.readLine();

                authreply = TwoFish.decrypt(inFromServer.readLine(), sessionKey).trim();

                json = new JSONObject(authreply);

                auth(json, clientSocket, inFromServer);



            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }



            //os.close();
        }
        catch(IOException|JSONException e){
            e.printStackTrace();
        }


    }

    public void auth(JSONObject replyJson, final Socket clientSocket, BufferedReader inFromServer) throws JSONException, IOException {

        if (replyJson.getInt("code") == TransmissionCodes.USER_LOGIN_REPLY_SUCCESS) {

            Intent intent = new Intent(loginActivity, MainActivity.class);

            intent.putExtra("connectReference", this);

            Thread reciever = new Thread(new Reciever(inFromServer, sessionKey));
            reciever.start();

            loginActivity.startActivity(intent);
            loginActivity.finish();
        }
        else{
            if (replyJson.getInt("code") == TransmissionCodes.USER_LOGIN_REPLY_FAIL) {

                final Context context = loginActivity.getApplicationContext();
                final CharSequence message = "Login Failed";
                final int duration = Toast.LENGTH_SHORT;

                loginActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();
                    }
                });



                clientSocket.close();

            }
            else{

                Context context = loginActivity.getApplicationContext();
                CharSequence message = "Server Error";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
                clientSocket.close();

            }
        }



    }

   /* public void sendLocationData(String locationX, String locationY){

        JSONObject outLocation = new JSONObject();

        try {
            outLocation.put("packetType", 101);
            outLocation.put("locationX", locationX);
            outLocation.put("locationY", locationY);

            String s = TwoFish.encrypt(outLocation.toString(), sessionKey);

            s = s.replaceAll("(\\r|\\n)", "");

            outToServer.write(s);
            outToServer.newLine();
            outToServer.flush();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/


}
