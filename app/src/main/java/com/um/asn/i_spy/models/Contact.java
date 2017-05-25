package com.um.asn.i_spy.models;


public class Contact {

    private int id;
    private String nom;
    private String numero;
    private int idRef;
    private Phone phone;

    public Contact() {
    }

    public Contact(String nom, String numero, int idRef, Phone phone) {
        this.nom = nom;
        this.numero = numero;
        this.idRef = idRef;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getIdRef() {
        return idRef;
    }

    public void setIdRef(int idRef) {
        this.idRef = idRef;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }
}
