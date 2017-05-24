package com.um.asn.i_spy.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class PositionGPSEntity extends SQLiteOpenHelper {

    public static final String POSITION_GPS_TABLE_NAME = "position_gps";

    public static final String POSITION_GPS_COLUMN_ID = "id_position_gps";
    public static final String POSITION_GPS_COLUMN_LATITUDE = "latitude";
    public static final String POSITION_GPS_COLUMN_LONGITUDE = "longitude";
    public static final String POSITION_GPS_COLUMN_PAYS = "pays";
    public static final String POSITION_GPS_COLUMN_VILLE = "ville";
    public static final String POSITION_GPS_COLUMN_CODE_POSTAL = "code_postal";
    public static final String POSITION_GPS_COLUMN_ADRESSE = "adresse";
    public static final String POSITION_GPS_COLUMN_DATE_POSITION = "date_position";

    public PositionGPSEntity(Context context, String nom, SQLiteDatabase.CursorFactory cursorfactory, int version) {
        super(context, nom, cursorfactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + POSITION_GPS_TABLE_NAME + " (" +
                POSITION_GPS_COLUMN_ID + " integer primary key autoincrement, " +
                POSITION_GPS_COLUMN_LATITUDE + " double, " +
                POSITION_GPS_COLUMN_LONGITUDE + " double, " +
                POSITION_GPS_COLUMN_PAYS + " text, " +
                POSITION_GPS_COLUMN_VILLE + " text, " +
                POSITION_GPS_COLUMN_CODE_POSTAL + " text, " +
                POSITION_GPS_COLUMN_ADRESSE + " text, " +
                POSITION_GPS_COLUMN_DATE_POSITION + " date" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Dans notre cas, nous supprimons la base et les données pour en
        // créer une nouvelle ensuite.
        db.execSQL("drop table if exists " + POSITION_GPS_TABLE_NAME + ";");
        // Création de la nouvelle structure.
        onCreate(db);
    }

    public boolean drop() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + POSITION_GPS_TABLE_NAME + ";");
        return true;
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String,String>> positions = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> position = new HashMap<String,String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + POSITION_GPS_TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            position.put(POSITION_GPS_COLUMN_LATITUDE, res.getString(res.getColumnIndex(POSITION_GPS_COLUMN_LATITUDE)));
            position.put(POSITION_GPS_COLUMN_LONGITUDE, res.getString(res.getColumnIndex(POSITION_GPS_COLUMN_LONGITUDE)));
            position.put(POSITION_GPS_COLUMN_PAYS, res.getString(res.getColumnIndex(POSITION_GPS_COLUMN_PAYS)));
            position.put(POSITION_GPS_COLUMN_VILLE, res.getString(res.getColumnIndex(POSITION_GPS_COLUMN_VILLE)));
            position.put(POSITION_GPS_COLUMN_CODE_POSTAL, res.getString(res.getColumnIndex(POSITION_GPS_COLUMN_CODE_POSTAL)));
            position.put(POSITION_GPS_COLUMN_ADRESSE, res.getString(res.getColumnIndex(POSITION_GPS_COLUMN_ADRESSE)));
            position.put(POSITION_GPS_COLUMN_DATE_POSITION, res.getString(res.getColumnIndex(POSITION_GPS_COLUMN_DATE_POSITION)));

            positions.add(position);

            res.moveToNext();
        }

        return positions;
    }

    public boolean insert(
            double latitude, double longitude,
            String pays, String ville, String adresse, String code_postal,
            String date_position)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POSITION_GPS_COLUMN_LATITUDE, latitude);
        contentValues.put(POSITION_GPS_COLUMN_LONGITUDE, longitude);
        contentValues.put(POSITION_GPS_COLUMN_PAYS, pays);
        contentValues.put(POSITION_GPS_COLUMN_VILLE, ville);
        contentValues.put(POSITION_GPS_COLUMN_CODE_POSTAL, code_postal);
        contentValues.put(POSITION_GPS_COLUMN_ADRESSE, adresse);
        contentValues.put(POSITION_GPS_COLUMN_DATE_POSITION, date_position);
        db.insert(POSITION_GPS_TABLE_NAME, null, contentValues);
        return true;
    }

}
