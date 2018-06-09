package com.mini_mo.viewpager.ReadAndWrite;

public class LocateListviewItem {

    private int save_number;
    private String message;
    private double latitude, longitude;

    public void setLocate(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setSaveNumber(int num){
        save_number = num;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public int getSaveNumber(){
        return save_number;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getMessage() {
        return message;
    }
}
