package com.um.asn.i_spy.models;

public class Message {

    public final static int TYPE_RECEIVED = 1;
    public final static int TYPE_SENT = 2;

    private int id;
    private String numero;
    private int type;
    private int date;
    private String contenu;
    private Phone phone;
    private Contact contact;
    private int idRef;

    public Message() {
    }

    public Message(String numero, int type, int date, String contenu, Phone phone, int idRef) {
        this.numero = numero;
        this.type = type;
        this.date = date;
        this.contenu = contenu;
        this.phone = phone;
        this.idRef = idRef;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public int getIdRef() {
        return idRef;
    }

    public void setIdRef(int idRef) {
        this.idRef = idRef;
    }
}
