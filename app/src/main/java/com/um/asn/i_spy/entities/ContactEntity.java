package com.um.asn.i_spy.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.um.asn.i_spy.models.Contact;
import com.um.asn.i_spy.models.Phone;

import java.util.ArrayList;

public class ContactEntity extends SQLiteOpenHelper {

    public static final String CONTACT_TABLE_NAME = "contact";

    public static final String CONTACT_COLUMN_ID = "id";
    public static final String CONTACT_COLUMN_NOM = "nom";
    public static final String CONTACT_COLUMN_NUMERO = "numero";
    public static final String CONTACT_COLUMN_ID_REF = "idRef";
    public static final String CONTACT_COLUMN_PHONE = "phone";

    public ContactEntity(Context context, String nom, SQLiteDatabase.CursorFactory cursorfactory, int version) {
        super(context, nom, cursorfactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CONTACT_TABLE_NAME + " (" +
                CONTACT_COLUMN_ID + " integer primary key autoincrement, " +
                CONTACT_COLUMN_NOM + " text, " +
                CONTACT_COLUMN_NUMERO + " text, " +
                CONTACT_COLUMN_ID_REF + " integer, " +
                CONTACT_COLUMN_PHONE + " text" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Dans notre cas, nous supprimons la base et les données pour en
        // créer une nouvelle ensuite.
        db.execSQL("drop table if exists " + CONTACT_TABLE_NAME + ";");
        // Création de la nouvelle structure.
        onCreate(db);
    }

    public boolean deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CONTACT_TABLE_NAME + ";");
        return true;
    }

    public boolean delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CONTACT_TABLE_NAME + " where " + CONTACT_COLUMN_ID + " = " + id + ";");
        return true;
    }

    public ArrayList<Contact> getAll() {
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

    public boolean insert(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_COLUMN_NOM, contact.getNom());
        contentValues.put(CONTACT_COLUMN_NUMERO, contact.getNumero());
        contentValues.put(CONTACT_COLUMN_ID_REF, contact.getIdRef());
        contentValues.put(CONTACT_COLUMN_PHONE, contact.getPhone().getId());
        db.insert(CONTACT_TABLE_NAME, null, contentValues);
        return true;
    }

}
