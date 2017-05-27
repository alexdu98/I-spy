package com.um.asn.i_spy.models;


import android.content.Context;

import com.um.asn.i_spy.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class User {

    private int id;
    private String mail;
    private String password;

    private ArrayList<Phone> phones;

    public User() {
    }

    public User(int i, String m) {
        id = i;
        mail = m;
    }

    public User(int i, String m, String pssw) {
        id = i;
        mail = m;
        password = pssw;
    }

    public int getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Phone> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<Phone> phones) {
        this.phones = phones;
    }

    public void loadWithFile(Context context) {
        try {
            FileInputStream file = context.openFileInput(Config.USER_INFO);
            InputStreamReader inputStreamReader = new InputStreamReader(file);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jo = new JSONObject(sb.toString());

            System.out.println(jo.toString());

            this.id = (int) jo.get("id");
            this.mail = (String) jo.get("mail");
            this.password = (String) jo.get("password");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSON() {
        JSONObject user = new JSONObject();
        try {
            user.put("id", String.valueOf(this.getId()));
            user.put("mail", this.getMail());
            user.put("password", this.getPassword());
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
