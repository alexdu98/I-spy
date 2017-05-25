package com.um.asn.i_spy.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.PositionGPS;

import java.util.ArrayList;

public class PositionGPSEntity extends SQLiteOpenHelper {

    public static final String POSITION_GPS_TABLE_NAME = "position_gps";

    public static final String POSITION_GPS_COLUMN_ID = "id";
    public static final String POSITION_GPS_COLUMN_LATITUDE = "latitude";
    public static final String POSITION_GPS_COLUMN_LONGITUDE = "longitude";
    public static final String POSITION_GPS_COLUMN_DATE_POSITION = "datePosition";
    public static final String POSITION_GPS_COLUMN_PHONE = "phone";

    public PositionGPSEntity(Context context, String nom, SQLiteDatabase.CursorFactory cursorfactory, int version) {
        super(context, nom, cursorfactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + POSITION_GPS_TABLE_NAME + " (" +
                POSITION_GPS_COLUMN_ID + " integer primary key autoincrement, " +
                POSITION_GPS_COLUMN_LATITUDE + " double, " +
                POSITION_GPS_COLUMN_LONGITUDE + " double, " +
                POSITION_GPS_COLUMN_DATE_POSITION + " datetime, " +
                POSITION_GPS_COLUMN_PHONE + " text" +
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

    public boolean deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + POSITION_GPS_TABLE_NAME + ";");
        return true;
    }

    public boolean delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + POSITION_GPS_TABLE_NAME + " where " + POSITION_GPS_COLUMN_ID + " = " + id + ";");
        return true;
    }

    public ArrayList<PositionGPS> getAll() {
        ArrayList<PositionGPS> positions = new ArrayList<PositionGPS>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + POSITION_GPS_TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            Phone phone = new Phone();
            phone.setId(res.getInt(res.getColumnIndex(POSITION_GPS_COLUMN_PHONE)));

            Location location = new Location("");
            location.setLatitude(res.getDouble(res.getColumnIndex(POSITION_GPS_COLUMN_LATITUDE)));
            location.setLongitude(res.getDouble(res.getColumnIndex(POSITION_GPS_COLUMN_LONGITUDE)));

            PositionGPS position = new PositionGPS(location, res.getString(res.getColumnIndex(POSITION_GPS_COLUMN_DATE_POSITION)), phone);

            positions.add(position);

            res.moveToNext();
        }

        return positions;
    }

    public boolean insert(PositionGPS position)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POSITION_GPS_COLUMN_LATITUDE, position.getLatitude());
        contentValues.put(POSITION_GPS_COLUMN_LONGITUDE, position.getLongitude());
        contentValues.put(POSITION_GPS_COLUMN_DATE_POSITION, position.getDatePosition());
        contentValues.put(POSITION_GPS_COLUMN_PHONE, position.getPhone().getId());
        db.insert(POSITION_GPS_TABLE_NAME, null, contentValues);
        return true;
    }

}
