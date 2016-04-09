package com.rylow.eispbustracker.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by s.bakhti on 6.4.2016.
 */
public class Student implements Comparable<Student> {

    private String name;
    private String contactName;
    private String contactPhone;
    private String secondaryContactName;
    private String secondaryContactPhone;
    private String photo;
    private BusStop stop;
    private Boolean selected = false;
    private int ridesStudentid;

    public Student(String name, String contactName, String photo, String contactPhone, String secondaryContactName, String secondaryContactPhone, BusStop stop, int ridesStudentid) {
        this.name = name;
        this.contactName = contactName;
        this.photo = photo;
        this.contactPhone = contactPhone;
        this.secondaryContactName = secondaryContactName;
        this.secondaryContactPhone = secondaryContactPhone;
        this.stop = stop;
        this.ridesStudentid = ridesStudentid;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int compareTo(Student o) {
        return Integer.valueOf(this.stop.ridestopid).compareTo(o.getStop().getRidestopid());
    }

    public Bitmap getPhotoBitmap(){

        byte[] decodedString = Base64.decode(photo, Base64.URL_SAFE);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, o);

        int scale = 1;
        if (o.outHeight > 300 || o.outWidth > 300) {
            scale = (int)Math.pow(2, (int) Math.ceil(Math.log(200 /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        o2.inPurgeable=true;

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, o2);
    }

    public int getRidesStudentid() {
        return ridesStudentid;
    }

    public void setRidesStudentid(int ridesStudentid) {
        this.ridesStudentid = ridesStudentid;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getSecondaryContactName() {
        return secondaryContactName;
    }

    public void setSecondaryContactName(String secondaryContactName) {
        this.secondaryContactName = secondaryContactName;
    }

    public String getSecondaryContactPhone() {
        return secondaryContactPhone;
    }

    public void setSecondaryContactPhone(String secondaryContactPhone) {
        this.secondaryContactPhone = secondaryContactPhone;
    }

    public BusStop getStop() {
        return stop;
    }

    public void setStop(BusStop stop) {
        this.stop = stop;
    }
}
