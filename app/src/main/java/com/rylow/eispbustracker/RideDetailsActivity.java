package com.rylow.eispbustracker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rylow.eispbustracker.network.Connect;
import com.rylow.eispbustracker.network.TransmissionCodes;
import com.rylow.eispbustracker.network.TwoFish;
import com.rylow.eispbustracker.service.BusStop;
import com.rylow.eispbustracker.service.Student;
import com.rylow.eispbustracker.service.StudentAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RideDetailsActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private int lineid;
    private ArrayList<Student> listStudent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ridedetails);
        Intent intent = getIntent();

        final int rideid = intent.getIntExtra("rideid", 0);
        lineid = intent.getIntExtra("lineid", 0);

        ListView rideDetailView = (ListView) findViewById(R.id.rideDetailView);
        final TextView lblSelected = (TextView) findViewById(R.id.lblSelected);
        TextView lblTotal = (TextView) findViewById(R.id.lblTotal);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        View.OnClickListener btnSaveListner = new View.OnClickListener() {
            public void onClick(View v) {

                AsyncTask querySave = new AsyncTask<Integer, Void, Boolean>(){

                    @Override
                    protected Boolean doInBackground(Integer... params) {

                        Connect connect = Connect.getInstance();

                        if (connect.getClientSocket().isClosed()){

                            if (connect.connect()){

                                return saveRideDetailsOnServer(connect, rideid);

                            }

                            return false;
                        }
                        else{
                            return saveRideDetailsOnServer(connect, rideid);
                        }

                    }
                }.execute();

                try {
                    Boolean result = (Boolean) querySave.get();

                    if (result){

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                AlertDialog alertDialog = new AlertDialog.Builder(RideDetailsActivity.this).create();
                                alertDialog.setTitle("Success");
                                alertDialog.setMessage("Data for this ride has been successfully uploaded");
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
                    else{

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                AlertDialog alertDialog = new AlertDialog.Builder(RideDetailsActivity.this).create();
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        };

        btnSave.setOnClickListener(btnSaveListner);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                AsyncTask query = new AsyncTask<Integer, Void, Void>(){

                    @Override
                    protected Void doInBackground(Integer... params) {

                        Connect connect = Connect.getInstance();


                        if (connect.getClientSocket().isClosed()){

                            if(connect.connect()){

                                sendLocationToServer(connect, location, rideid);

                            }
                        }
                        else{

                            sendLocationToServer(connect, location,rideid);
                        }

                        return null;

                    }
                }.execute();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        configureLocation();

        // ArrayList<Student> studentList = new ArrayList<>();


        AsyncTask query = new AsyncTask<Integer, Void, List<Student>>(){

            @Override
            protected List<Student> doInBackground(Integer... params) {

                Connect connect = Connect.getInstance();


                if (connect.getClientSocket().isClosed()){

                    if (connect.connect()){

                        return getStudentList(connect, rideid);

                    }
                    else{

                        return new ArrayList<>();

                    }
                }
                else{
                    return getStudentList(connect, rideid);
                }

            }
        }.execute();

        try {

            listStudent = (ArrayList<Student>) query.get();



            final StudentAdapter adapter = new StudentAdapter(this, listStudent);

            lblTotal.setText(String.valueOf(listStudent.size()));

            adapter.notifyDataSetChanged();

            rideDetailView.setAdapter(adapter);

            final ArrayList<Student> studentList = listStudent;

            rideDetailView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Student value = (Student) parent.getItemAtPosition(position);
                    value.setSelected(!value.getSelected());
                    int currentlySelected = Integer.valueOf(String.valueOf(lblSelected.getText()));

                    if (value.getSelected())
                    {
                        currentlySelected++;
                        lblSelected.setText(String.valueOf(currentlySelected));
                    }
                    else{

                        currentlySelected--;
                        lblSelected.setText(String.valueOf(currentlySelected));
                    }

                    studentList.set(position, value);
                    adapter.notifyDataSetChanged();
                }
            });

            rideDetailView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {

                    Student value = (Student) parent.getItemAtPosition(position);

                    final Dialog dialog = new Dialog(RideDetailsActivity.this);
                    dialog.setContentView(R.layout.dialog_student_profile);
                    dialog.setTitle(value.getName() + " Profile");

                    // set the custom dialog components - text, image and button
                    TextView lblName = (TextView) dialog.findViewById(R.id.lblStudentName);
                    lblName.setText(value.getName());

                    TextView lblFirstContactName = (TextView) dialog.findViewById(R.id.lblFirstContactName);
                    lblFirstContactName.setText(value.getContactName());

                    TextView lblFirstContactPhone = (TextView) dialog.findViewById(R.id.lblFirstContactPhone);
                    String phone = value.getContactPhone().replaceAll(" ", "");
                    if (phone.length() == 9)
                        phone = "+420"+ phone;
                    lblFirstContactPhone.setText(phone);

                    TextView lblSecondaryContactName = (TextView) dialog.findViewById(R.id.lblSecondContactName);
                    lblSecondaryContactName.setText(value.getSecondaryContactName());

                    TextView lblSecondaryContactPhone = (TextView) dialog.findViewById(R.id.lblSecondContactPhone);
                    phone = value.getSecondaryContactPhone().replaceAll(" ", "");
                    if (phone.length() == 9)
                        phone = "+420"+ phone;
                    lblSecondaryContactPhone.setText(phone);

                    ImageView image = (ImageView) dialog.findViewById(R.id.imgStudentProfile);
                    image.setImageBitmap(value.getPhotoBitmap());;

                    Button btnOK = (Button) dialog.findViewById(R.id.bntOK);
                    // if button is clicked, close the custom dialog
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    return true;
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }

    private List<Student> getStudentList(Connect connect, int rideid){

        List<Student> studentsList = new ArrayList<>();

        try {
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

            JSONObject json = new JSONObject();

            json.put("code", TransmissionCodes.REQUEST_RIDE_DETAILS);
            json.put("rideid", rideid);

            String send = TwoFish.encrypt(json.toString(), connect.getSessionKey());
            send = send.replaceAll("(\\r|\\n)", "");

            outToServer.write(send);
            outToServer.newLine();
            outToServer.flush();

            String incString = inFromServer.readLine();

            incString = TwoFish.decrypt(incString, connect.getSessionKey()).trim();

            JSONObject recievedJSON = new JSONObject(incString);

            if(recievedJSON.getInt("code") == TransmissionCodes.RESPONCE_RIDE_DETAILS){

                for (int i = 0; i < recievedJSON.getJSONArray("array").length(); i++){

                    JSONObject studentJson = recievedJSON.getJSONArray("array").getJSONObject(i);
                    JSONObject stopJson = studentJson.getJSONObject("stop");

                    BusStop tempStop = new BusStop(stopJson.getInt("busstopid"), stopJson.getInt("ridestopid"), stopJson.getString("gpsx"), stopJson.getString("gpsy"),
                            stopJson.getString("name"), stopJson.getString("note"));

                    studentsList.add(new Student(studentJson.getString("name"), studentJson.getString("contactname"), studentJson.getString("photo"),
                            studentJson.getString("contactphone"), studentJson.getString("secondarycontactname"), studentJson.getString("secondarycontactphone"), tempStop, studentJson.getInt("ridesstudentid")));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return studentsList;

    }

    private void sendLocationToServer(Connect connect, Location location, int rideid){

        try {

            JSONObject sendJson = new JSONObject();
            JSONObject localtionJSON = new JSONObject();

            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));

            sendJson.put("code", TransmissionCodes.GPS_UPDATE);
            localtionJSON.put("gpsx", String.valueOf(location.getLatitude()));
            localtionJSON.put("gpsy", String.valueOf(location.getLongitude()));
            localtionJSON.put("rideid", rideid);
            sendJson.put("location", localtionJSON);

            String send = TwoFish.encrypt(sendJson.toString(), connect.getSessionKey());
            send = send.replaceAll("(\\r|\\n)", "");

            outToServer.write(send);
            outToServer.newLine();
            outToServer.flush();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            try {
                connect.getClientSocket().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }


    }

    private Boolean saveRideDetailsOnServer(Connect connect, int rideid){

        try {
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

            JSONObject json = new JSONObject();

            json.put("code", TransmissionCodes.RIDE_DETAILS_UPDATE);
            json.put("rideid", rideid);

            List<JSONObject> studentsToSend = new ArrayList<>();

            for (Student student : listStudent){

                JSONObject tempJSON = new JSONObject();
                tempJSON.put("ridesstudentid", student.getRidesStudentid());
                if(student.getSelected())
                    tempJSON.put("ridestatus", 0);
                else
                    tempJSON.put("ridestatus", 2);

                studentsToSend.add(tempJSON);

            }

            JSONArray array = new JSONArray(studentsToSend);

            json.put("array", array);

            String send = TwoFish.encrypt(json.toString(), connect.getSessionKey());
            send = send.replaceAll("(\\r|\\n)", "");

            outToServer.write(send);
            outToServer.newLine();
            outToServer.flush();

            String incString = inFromServer.readLine();

            incString = TwoFish.decrypt(incString, connect.getSessionKey()).trim();

            JSONObject recievedJSON = new JSONObject(incString);

            if(recievedJSON.getInt("code") == TransmissionCodes.RIDE_DETAILS_CONFIRMATION){

                return true;
            }

        } catch (IOException e) {
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

        return false;


    }

    private void configureLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 5);
            }
            return;
        }

        locationManager.requestLocationUpdates("gps", 10000, 20, locationListener);

    }

    private void stopLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 5);
            }
            return;
        }

        locationManager.removeUpdates(locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){

            case (10):
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureLocation();
                return;
        }

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(RideDetailsActivity.this, RideSelectionActivity.class);

        intent.putExtra("lineid", lineid);

        startActivity(intent);
        stopLocation();
        finish();

    }
}
