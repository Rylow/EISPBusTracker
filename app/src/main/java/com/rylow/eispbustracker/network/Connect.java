package com.rylow.eispbustracker.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.rylow.eispbustracker.LineSelectionActivity;
import com.rylow.eispbustracker.LoginActivity;

import org.json.JSONArray;
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
import java.security.InvalidKeyException;


public class Connect {

    private String sessionKey, username, password;
    private static Connect connect;
    private Socket clientSocket = new Socket();

    private BufferedReader inFromServer;
    private BufferedWriter outToServer;


    private Connect (){

    }

    public static Connect getInstance(){

        if(connect == null)
            connect = new Connect();


        return connect;
    }

    public void connect(){

        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress("193.85.228.2", 6788), 1000);

            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            this.sessionKey = TwoFish.decrypt(inFromServer.readLine(), "VeryC0oLVeryC0oL").trim();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


    }

    public Boolean auth() {

        JSONObject json = new JSONObject();
        try {

            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            json.put("code", TransmissionCodes.USER_LOGIN);
            json.put("username", username);
            json.put("password", password);

            outToServer.write(TwoFish.encrypt(json.toString(), sessionKey));
            outToServer.newLine();
            outToServer.flush();

            String authreply = inFromServer.readLine();

            authreply = TwoFish.decrypt(authreply, sessionKey).trim();

            json = new JSONObject(authreply);

            if (json.getInt("code") == TransmissionCodes.USER_LOGIN_REPLY_SUCCESS) {


                return true;

            }
            else{
                if (json.getInt("code") == TransmissionCodes.USER_LOGIN_REPLY_FAIL) {

                    clientSocket.close();

                    return false;

                }
                else{


                    clientSocket.close();
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Socket getClientSocket() {
        return clientSocket;
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
