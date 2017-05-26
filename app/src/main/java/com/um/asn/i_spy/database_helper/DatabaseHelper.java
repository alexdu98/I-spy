package com.um.asn.i_spy.database_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.um.asn.i_spy.models.Contact;
import com.um.asn.i_spy.models.Phone;
import com.um.asn.i_spy.models.PositionGPS;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String POSITION_GPS_TABLE_NAME = "position_gps";
    public static final String CONTACT_TABLE_NAME = "contact";
    public static final String POSITION_GPS_COLUMN_ID = "id";
    public static final String POSITION_GPS_COLUMN_LATITUDE = "latitude";
    public static final String POSITION_GPS_COLUMN_LONGITUDE = "longitude";
    public static final String POSITION_GPS_COLUMN_DATE_POSITION = "datePosition";
    public static final String POSITION_GPS_COLUMN_PHONE = "phone";
    public static final String CONTACT_COLUMN_ID = "id";
    public static final String CONTACT_COLUMN_NOM = "nom";
    public static final String CONTACT_COLUMN_NUMERO = "numero";
    public static final String CONTACT_COLUMN_ID_REF = "idRef";
    public static final String CONTACT_COLUMN_PHONE = "phone";
    private static final String DATABASE_NAME = "ISpy";
    private static final String CREATE_TABLE_POSITION_GPS = "create table " + POSITION_GPS_TABLE_NAME + " (" +
            POSITION_GPS_COLUMN_ID + " integer primary key autoincrement, " +
            POSITION_GPS_COLUMN_LATITUDE + " double, " +
            POSITION_GPS_COLUMN_LONGITUDE + " double, " +
            POSITION_GPS_COLUMN_DATE_POSITION + " datetime, " +
            POSITION_GPS_COLUMN_PHONE + " text" +
            ");";
    private static final String CREATE_TABLE_CONTACT = "create table " + CONTACT_TABLE_NAME + " (" +
            CONTACT_COLUMN_ID + " integer primary key autoincrement, " +
            CONTACT_COLUMN_NOM + " text, " +
            CONTACT_COLUMN_NUMERO + " text, " +
            CONTACT_COLUMN_ID_REF + " integer, " +
            CONTACT_COLUMN_PHONE + " text" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_POSITION_GPS);
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + POSITION_GPS_TABLE_NAME + ";");
        db.execSQL("drop table if exists " + CONTACT_TABLE_NAME + ";");
        onCreate(db);
    }

    public boolean deleteAllContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CONTACT_TABLE_NAME + ";");
        return true;
    }

    public boolean deleteAllPositionGPS() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + POSITION_GPS_TABLE_NAME + ";");
        return true;
    }

    public boolean deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CONTACT_TABLE_NAME + " where " + CONTACT_COLUMN_ID + " = " + id + ";");
        return true;
    }

    public boolean deletePositionGPS(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + POSITION_GPS_TABLE_NAME + " where " + POSITION_GPS_COLUMN_ID + " = " + id + ";");
        return true;
    }

    public Contact getByIdRefContact(int idRef) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + CONTACT_TABLE_NAME + " where " + CONTACT_COLUMN_ID_REF + " = " + idRef + ";", null);
        res.moveToFirst();

        Contact contact = null;
        if (!res.isAfterLast()) {
            Phone phone = new Phone();
            phone.setId(res.getInt(res.getColumnIndex(CONTACT_COLUMN_PHONE)));

            contact = new Contact();
            contact.setId(res.getInt(res.getColumnIndex(CONTACT_COLUMN_ID)));
            contact.setNom(res.getString(res.getColumnIndex(CONTACT_COLUMN_NOM)));
            contact.setNumero(res.getString(res.getColumnIndex(CONTACT_COLUMN_NUMERO)));
            contact.setIdRef(res.getInt(res.getColumnIndex(CONTACT_COLUMN_ID_REF)));
            contact.setPhone(phone);
        }
        return contact;
    }

    public void updateByIdRefContact(int idRef, Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_COLUMN_NOM, contact.getNom());
        contentValues.put(CONTACT_COLUMN_NUMERO, contact.getNumero());
        db.update(CONTACT_TABLE_NAME, contentValues, CONTACT_COLUMN_ID_REF + " = " + idRef, null);
    }

    public ArrayList<Contact> getAllContact() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + CONTACT_TABLE_NAME, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            Phone phone = new Phone();
            phone.setId(res.getInt(res.getColumnIndex(CONTACT_COLUMN_PHONE)));

            Contact contact = new Contact();
            contact.setNom(res.getString(res.getColumnIndex(CONTACT_COLUMN_NOM)));
            contact.setNumero(res.getString(res.getColumnIndex(CONTACT_COLUMN_NUMERO)));
            contact.setIdRef(res.getInt(res.getColumnIndex(CONTACT_COLUMN_ID_REF)));
            contact.setPhone(phone);

            contacts.add(contact);

            res.moveToNext();
        }

        return contacts;
    }

    public ArrayList<PositionGPS> getAllPositionGPS() {
        ArrayList<PositionGPS> positions = new ArrayList<PositionGPS>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + POSITION_GPS_TABLE_NAME + ";", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
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

    public boolean insertContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_COLUMN_NOM, contact.getNom());
        contentValues.put(CONTACT_COLUMN_NUMERO, contact.getNumero());
        contentValues.put(CONTACT_COLUMN_ID_REF, contact.getIdRef());
        contentValues.put(CONTACT_COLUMN_PHONE, contact.getPhone().getId());
        db.insert(CONTACT_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertPositionGPS(PositionGPS position) {
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
