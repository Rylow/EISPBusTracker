package com.rylow.eispbustracker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.rylow.eispbustracker.network.Connect;
import com.rylow.eispbustracker.network.ConnecterAsyncTask;
import com.rylow.eispbustracker.network.TransmissionCodes;
import com.rylow.eispbustracker.network.TwoFish;

import org.json.JSONArray;
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
import android.view.View;

/**
 * Created by bakht on 30.03.2016.
 */
public class LineSelectionActivity extends AppCompatActivity {

    public class Line{

        private String name;
        private int id;

        public Line(int id, String name) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString(){
            return name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineselection);

        ListView lineListView = (ListView) findViewById(R.id.lineListView);

        List<Line> lineList = new ArrayList<>();


        AsyncTask query = new AsyncTask<Integer, Void, List<Line>>(){

            @Override
            protected List<Line> doInBackground(Integer... params) {

                Connect connect = Connect.getInstance();
                List<Line> lineList = new ArrayList<>();

                if (connect.getClientSocket().isClosed()){

                    connect.connect();
                    connect.auth();

                }

                try {
                    BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

                    JSONObject json = new JSONObject();

                    json.put("code", TransmissionCodes.REQUEST_LINE_LIST);

                    outToServer.write(TwoFish.encrypt(json.toString(), connect.getSessionKey()));
                    outToServer.newLine();
                    outToServer.flush();

                    String incString = inFromServer.readLine();

                    incString = TwoFish.decrypt(incString, connect.getSessionKey()).trim();

                    JSONObject recievedJSON = new JSONObject(incString);

                    if(recievedJSON.getInt("code") == TransmissionCodes.RESPONSE_LINE_LIST){

                        for (int i = 0; i < recievedJSON.getJSONArray("array").length(); i++){

                            JSONObject tempJson = recievedJSON.getJSONArray("array").getJSONObject(i);

                            lineList.add(new Line(tempJson.getInt("id"), tempJson.getString("name")));

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

                return lineList;
            }
        }.execute();

        try {
            lineList = (List<Line>) query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ArrayAdapter<Line> adapter = new ArrayAdapter<Line>(this,
                R.layout.line_list_view_text,lineList);

        lineListView.setAdapter(adapter);
        lineListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Line value = (Line) parent.getItemAtPosition(position);

                Intent intent = new Intent(LineSelectionActivity.this, RideSelectionActivity.class);

                intent.putExtra("lineid", value.getId());

                startActivity(intent);
                finish();
            }
        });



    }
}
