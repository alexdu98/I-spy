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

    private String phoneId;
    private String login;
    private String password;

    public Phone(){}

    public Phone(String pId, String n, String pssw) {
        phoneId = pId;
        login = n;
        password = pssw;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneId() {
        return phoneId;
    }

    @Override
    public String toString() {
        return phoneId + " " + " " + login + " " + password;
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

            this.phoneId = String.valueOf((int) jo.get("id"));
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

}
