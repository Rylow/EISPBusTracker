package com.rylow.eispbustracker.service;

/**
 * Created by s.bakhti on 6.4.2016.
 */
public class BusStop {

    int id;
    int ridestopid;
    String gpsx;
    String gpsy;
    String name;
    String note;

    public BusStop(int id, int ridestopid, String gpsx, String gpsy, String name, String note) {

        this.id = id;
        this.ridestopid = ridestopid;
        this.gpsx = gpsx;
        this.gpsy = gpsy;
        this.name = name;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRidestopid() {
        return ridestopid;
    }

    public void setRidestopid(int ridestopid) {
        this.ridestopid = ridestopid;
    }

    public String getGpsx() {
        return gpsx;
    }

    public void setGpsx(String gpsx) {
        this.gpsx = gpsx;
    }

    public String getGpsy() {
        return gpsy;
    }

    public void setGpsy(String gpsy) {
        this.gpsy = gpsy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
