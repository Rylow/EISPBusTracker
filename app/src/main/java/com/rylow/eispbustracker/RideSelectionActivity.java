package com.rylow.eispbustracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    private int lineid;
    private ListView rideListView;

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
        rideListView = (ListView) findViewById(R.id.rideListView);

        Intent intent = getIntent();
        lineid = intent.getIntExtra("lineid", 0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTheJob();
            }
        });


        doTheJob();


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(RideSelectionActivity.this, LineSelectionActivity.class);

        startActivity(intent);
        finish();

    }

    private void doTheJob(){

        List<Ride> rideList;

        AsyncTask query = new AsyncTask<Integer, Void, List<Ride>>(){

            @Override
            protected List<Ride> doInBackground(Integer... params) {

                Connect connect = Connect.getInstance();

                if (connect.getClientSocket().isClosed()){

                    if (connect.connect()){

                        return getRideList(connect);

                    }
                    else{

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                AlertDialog alertDialog = new AlertDialog.Builder(RideSelectionActivity.this).create();
                                alertDialog.setTitle("Failure");
                                alertDialog.setMessage("Connection to the server is not available. Probably mobile connection is not available at this moment. Please again later.");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        });

                        return new ArrayList<>();

                    }
                }
                else{

                    return getRideList(connect);

                }


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

                    Intent intent = new Intent(RideSelectionActivity.this, RideDetailsActivity.class);

                    intent.putExtra("rideid", value.getId());
                    intent.putExtra("lineid", lineid);

                    setContentView(R.layout.loading_ride_details);

                    startActivity(intent);
                    finish();
                }
            });


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private List<Ride> getRideList(Connect connect){

        List<Ride> rideList = new ArrayList<>();

        try {
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

            JSONObject json = new JSONObject();

            json.put("code", TransmissionCodes.REQUEST_RIDE_LIST);
            json.put("lineid", lineid);

            outToServer.write(TwoFish.encrypt(json.toString(), connect.getSessionKey()));
            outToServer.newLine();
            outToServer.flush();

            String incString = inFromServer.readLine();

            if (incString != null) {
                incString = TwoFish.decrypt(incString, connect.getSessionKey()).trim();
            }
            else {
                incString = "";
                showErrorDialog();
                connect.getClientSocket().close();

            }

            JSONObject recievedJSON = new JSONObject(incString);

            if(recievedJSON.getInt("code") == TransmissionCodes.RESPONCE_RIDE_LIST){

                for (int i = 0; i < recievedJSON.getJSONArray("array").length(); i++){

                    JSONObject tempJson = recievedJSON.getJSONArray("array").getJSONObject(i);

                    rideList.add(new Ride(tempJson.getInt("id"), tempJson.getString("direction"), tempJson.getString("date")));

                }
            }


        } catch (IOException e) {
            showErrorDialog();
            try {
                connect.getClientSocket().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return rideList;


    }

    private void showErrorDialog(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(RideSelectionActivity.this).create();
                alertDialog.setTitle("Failure");
                alertDialog.setMessage("Connection to the server is not available. Probably mobile connection is not available at this moment. Please again later.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

    }

}
