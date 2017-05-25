package com.um.asn.i_spy.models;


public class User {

    private int id;
    private String mail;
    private String password;

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


}
