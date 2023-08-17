package com.ali.minimalweather.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

public class City {

    private int city_id;
    private String cityName = "";
    private double latitude;
    private double longitude;
    private String countryCode = "";
    private boolean selected = false;
    private boolean shown = false;

    public int getCity_id() {
        return city_id;
    }
    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isShown() {
        return shown;
    }
    public void setShown(boolean shown) {
        this.shown = shown;
    }


    @Override
    public String toString() {
        return "City{" +
                "city_id=" + city_id +
                ", cityName='" + cityName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", countryCode='" + countryCode + '\'' +
                ", selected=" + selected +
                ", shown=" + shown +
                '}';
    }

    public ContentValues getContentValuesForDB(){
        ContentValues cv = new ContentValues();
        cv.put("city_id", city_id);
        cv.put("city_name",cityName);
        cv.put("latitude",latitude);
        cv.put("longitude",longitude);
        cv.put("country_code",countryCode);
        cv.put("selected", selected ? 1:0);
        cv.put("shown", shown ? 1:0);
        return cv;
    }



    public static ContentValues createContentValues(int id, String cityName,
                                                    double latitude, double longitude,
                                                    String countryCode,
                                                    boolean selected, boolean shown){
        ContentValues cv = new ContentValues();
        cv.put("city_id",id);
        cv.put("city_name",cityName);
        cv.put("latitude",latitude);
        cv.put("longitude",longitude);
        cv.put("country_code",countryCode);
        cv.put("selected",selected ? 1:0);
        cv.put("shown", shown ? 1:0);
        return cv;
    }

    @SuppressLint("Range")
    public static City fromCursor(Cursor cursor){
        City city = new City();

        city.setCity_id(cursor.getInt(cursor.getColumnIndex("city_id")));
        city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
        city.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
        city.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
        city.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
        return city;
    }
}
