package com.rylow.eispbustracker.service;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rylow.eispbustracker.R;

import java.util.ArrayList;

/**
 * Created by s.bakhti on 6.4.2016.
 */
public class StudentAdapter extends ArrayAdapter<Student> {


    public StudentAdapter(Context context, ArrayList<Student> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Student student = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_student, parent, false);
        }
        // Lookup view for data population
        ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);
        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        TextView txtStop = (TextView) convertView.findViewById(R.id.txtStop);
        // Populate the data into the template view using the data object
        imgPhoto.setImageBitmap(student.getPhotoBitmap());
        txtName.setText(student.getName());
        txtStop.setText(student.getStop().getName() + " (" + student.getStop().getNote() + ")");

        if (student.getSelected()){

            txtName.setTextColor(Color.GREEN);

        }
        else{
            txtName.setTextColor(Color.GRAY);
        }

        // Return the completed view to render on screen
        return convertView;
    }


}
