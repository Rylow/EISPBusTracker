package com.rylow.eispbustracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.rylow.eispbustracker.network.Connect;
import com.rylow.eispbustracker.network.TransmissionCodes;
import com.rylow.eispbustracker.network.TwoFish;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bakht on 31.03.2016.
 */
public class RideSelectionActivity extends AppCompatActivity {

    private class Ride{

        private int id;
        private String direction;
        private String date;

        public Ride(int id, String direction, String date) {
            this.id = id;
            this.direction = direction;
            this.date = date;
        }

        @Override
        public String toString(){
            return direction + " at " + date;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rideselection);
        ListView rideListView = (ListView) findViewById(R.id.rideListView);

        Intent intent = getIntent();
        final int rideid = intent.getIntExtra("rideid", 0);

        List<Ride> rideList = new ArrayList<>();

        AsyncTask query = new AsyncTask<Integer, Void, List<Ride>>(){

            @Override
            protected List<Ride> doInBackground(Integer... params) {

                Connect connect = Connect.getInstance();
                List<Ride> rideList = new ArrayList<>();

                if (connect.getClientSocket().isClosed()){

                    connect.connect();
                    connect.auth();

                }

                try {
                    BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

                    JSONObject json = new JSONObject();

                    json.put("code", TransmissionCodes.REQUEST_RIDE_LIST);
                    json.put("rideid", rideid);

                    outToServer.write(TwoFish.encrypt(json.toString(), connect.getSessionKey()));
                    outToServer.newLine();
                    outToServer.flush();

                    String incString = inFromServer.readLine();

                    incString = TwoFish.decrypt(incString, connect.getSessionKey()).trim();

                    JSONObject recievedJSON = new JSONObject(incString);

                    if(recievedJSON.getInt("code") == TransmissionCodes.RESPONCE_RIDE_LIST){

                        for (int i = 0; i < recievedJSON.getJSONArray("array").length(); i++){

                            JSONObject tempJson = recievedJSON.getJSONArray("array").getJSONObject(i);

                            rideList.add(new Ride(tempJson.getInt("id"), tempJson.getString("direction"), tempJson.getString("date")));

                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

                return rideList;
            }
        }.execute();

        try {
            rideList = (List<Ride>) query.get();

            ArrayAdapter<Ride> adapter = new ArrayAdapter<Ride>(this,
                    R.layout.line_list_view_text,rideList);

            rideListView.setAdapter(adapter);
            rideListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Ride value = (Ride) parent.getItemAtPosition(position);

                    Toast.makeText(getApplicationContext(),
                            value.getDate(), Toast.LENGTH_SHORT).show();
                }
            });


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }

}
