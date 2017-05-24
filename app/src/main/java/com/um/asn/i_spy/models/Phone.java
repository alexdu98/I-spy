package com.um.asn.i_spy.models;

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

}
