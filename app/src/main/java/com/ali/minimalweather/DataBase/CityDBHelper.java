package com.ali.minimalweather.DataBase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ali.minimalweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CityDBHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "db_city";
    private static final String TABLE_CITY = "tb_city";
    private static final String[] columns = {"city_id","city_name","latitude","longitude","country_code","selected","shown"};

    private static final String CMD_CREATE_TABLE_CITY = "CREATE TABLE IF NOT EXISTS " + TABLE_CITY +
            " ( "+
                "'city_id' INTEGER PRIMARY KEY NOT NULL, "+
                "'city_name' TEXT, "+
                "'latitude' DOUBLE, "+
                "'longitude' DOUBLE, "+
                "'country_code' TEXT, "+
                "'selected' INTEGER , " +
                "'shown' INTEGER" +
                                            " );";


    public CityDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD_CREATE_TABLE_CITY);
        Log.i("database","table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        onCreate(db);
    }




/*
    public void initContents(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    StringBuilder builder = new StringBuilder();
                    InputStream is = context.getResources().openRawResource(R.raw.city_list);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    SQLiteDatabase db = getWritableDatabase();


                    String line = "";
                    while ((line=reader.readLine()) != null )
                        builder.append(line);

                    JSONArray jsonArray = new JSONArray(builder.toString());
                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        //if anything was empty; do not error! just continue!

                        if (jsonObject.getString("city_id").equals("")||
                                jsonObject.getString("city_name").equals("")||
                                jsonObject.getString("latitude").equals("")||
                                jsonObject.getString("longitude").equals("")||
                                jsonObject.getString("country_code").equals("")       )
                            continue;




                        City city = new City();
                        city.setCity_id(Integer.parseInt(jsonObject.getString("city_id")));
                        city.setCityName(jsonObject.getString("city_name"));
                        city.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                        city.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                        city.setCountryCode(jsonObject.getString("country_code"));




                        // insert to database
                        //insertCityToDB(city);

                        long insertId = db.insert(TABLE_CITY,null,City.createContentValues
                                            (Integer.parseInt(jsonObject.getString("city_id")),
                                            jsonObject.getString("city_name"),
                                            Double.parseDouble(jsonObject.getString("latitude")),
                                            Double.parseDouble(jsonObject.getString("longitude")),
                                            jsonObject.getString("country_code"),
                                            false,false));

                        Log.i("database","insert Id -> " + insertId);
                    }
                    db.close();
                }catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


 */

    /*
    public void insertCityToDB(City city){
        //SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CITY,null, city.getContentValuesForDB());
        db.close();
    }

     */






    public List<City> getCities(String selection, String[] selectionArgs){
        List<City> cityList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITY,columns,selection,selectionArgs,
                null,null,"country_code , city_name");

        Log.i("database", "Cursor returned " + cursor.getCount() + " records.");

        if (cursor.moveToFirst()){
            do {
                cityList.add(City.fromCursor(cursor));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return cityList;
    }

    public void deletefirst(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CITY,null,null);
    }




    public void updateCitySelected(int id, boolean selected){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("selected",selected ? 1:0);
        db.update(TABLE_CITY,cv,"city_id = " + id,null);
        db.close();
    }

    public void updateCityShown(int id, boolean shown){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("shown", 0);
        db.update(TABLE_CITY,cv,"shown = 1",null);

        cv.put("shown",shown ? 1:0);
        db.update(TABLE_CITY,cv,"city_id = " + id , null);
        db.close();
    }

    public City getShownCity(){
        City city = new City();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITY,columns,"shown = 1",null,null,null,null,null);

        if (cursor.moveToFirst()){
            do {
                city = City.fromCursor(cursor);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return city;
    }


    public List<City> searchCityByName(String cityName, String limit){
        List<City> cityList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true,TABLE_CITY,columns,
                "city_name LIKE '"+cityName+"%'" + "AND selected = 0",
                null,null,null,"country_code, city_name",limit);
        if (cursor.moveToFirst()){
            do {
                cityList.add(City.fromCursor(cursor));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cityList;
    }


}
