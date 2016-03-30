package com.rylow.eispbustracker.network;

import java.io.BufferedReader;

/**
 * Created by s.bakhti on 30.3.2016.
 */
public class Reciever implements Runnable {

    private BufferedReader inFromServer;
    String sessionKey;

    public Reciever(BufferedReader inFromServer, String sessionKey) {
        this.inFromServer = inFromServer;
        this.sessionKey = sessionKey;
    }


    @Override
    public void run() {



    }
}
