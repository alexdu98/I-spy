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

public class Phone {

    private int id;
    private String login;
    private String password;

    public Phone() {
    }

    public Phone(int pId, String n) {
        id = pId;
        login = n;
    }

    public Phone(int pId, String n, String pssw) {
        id = pId;
        login = n;
        password = pssw;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + " " + " " + login + " " + password;
    }

    public void loadWithFile(Context context) {
        try {
            FileInputStream file = context.openFileInput(Config.PHONE_INFO);
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
            this.login = (String) jo.get("login");
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
        JSONObject phone = new JSONObject();
        try {
            phone.put("id", String.valueOf(this.getId()));
            phone.put("login", this.getLogin());
            phone.put("password", this.getPassword());
            return phone;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
