package com.rylow.eispbustracker.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;


public class Connect implements Runnable {

    Socket clientSocket = new Socket();

    BufferedReader inFromServer;
    BufferedWriter os;

    String sessionKey;

    @Override
    public void run(){

        try {


            Log.v("aa", "Thread Start");

            clientSocket.connect(new InetSocketAddress("192.168.1.37", 6789), 1000);

            JSONObject json = new JSONObject();
            json.put("check", "ok");

            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.os = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));


            try {
                this.sessionKey = TwoFish.decrypt(inFromServer.readLine(), "VeryC0oLVeryC0oL").trim();

                Log.v("aa",sessionKey);
                os.write(TwoFish.encrypt(json.toString(), sessionKey));
                os.newLine();
                os.flush();

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }



            //os.close();
        }
        catch(IOException|JSONException e){
            e.printStackTrace();
        }


    }

    public void sendLocationData(String locationX, String locationY){

        JSONObject outLocation = new JSONObject();

        try {
            outLocation.put("packetType", 101);
            outLocation.put("locationX", locationX);
            outLocation.put("locationY", locationY);

            String s = TwoFish.encrypt(outLocation.toString(), sessionKey);

            s = s.replaceAll("(\\r|\\n)", "");

            os.write(s);
            os.newLine();
            os.flush();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
