package com.ali.minimalweather.DataBase;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetDatabaseHelper {

    private final Context context;
    private String DB_NAME = "db_city";
    
    public AssetDatabaseHelper(Context context) {
        this(context,"db_city");
    }

    public AssetDatabaseHelper(Context context,String databaseName){
        this.context = context;
        this.DB_NAME = databaseName;
    }

    public void checkDB(){
        File dbFile= context.getDatabasePath(DB_NAME);
        if (!dbFile.exists()){
            try{
                copyDatabase(dbFile);
                Log.i("database","database copied.");
            }catch (IOException e){
                throw new RuntimeException("error creating source database",e);
            }
        }
    }

    private void copyDatabase(File dbFile) throws IOException {
        InputStream is = context.getAssets().open(DB_NAME);
        dbFile.getParentFile().mkdirs();
        OutputStream os = new FileOutputStream(dbFile);

        int len = 0;
        byte[] buffer = new byte[1024];

        while ((len = is.read(buffer)) > 0){
            os.write(buffer,0,len);
        }

        os.flush();
        os.close();
        is.close();

    }
}
