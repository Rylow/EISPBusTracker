package com.rylow.eispbustracker.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;




public class Connect implements Runnable {

    Socket clientSocket = new Socket();

    @Override
    public void run(){

        try {


            Log.v("aa", "Thread Start");

            clientSocket.connect(new InetSocketAddress("192.168.1.37", 6789), 1000);

            JSONObject json = new JSONObject();
            json.put("type", "CONNECT");

            //OutputStreamWriter os = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedWriter os = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            try {
                Log.v("aa",json.toString());
                os.write(TwoFish.encrypt(json.toString(), "VeryC0oLVeryC0oL"));
                os.newLine();

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }



            os.close();
        }
        catch(IOException|JSONException e){
            e.printStackTrace();
        }


    }

}
