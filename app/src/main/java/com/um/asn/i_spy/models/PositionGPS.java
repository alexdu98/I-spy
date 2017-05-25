package com.um.asn.i_spy.models;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PositionGPS {

    private int id;
    private double latitude;
    private double longitude;
    private String pays;
    private String ville;
    private String codePostal;
    private String adresse;
    private String datePosition;
    private Phone phone;

    public PositionGPS() {
    }

    public PositionGPS(Location location, String datePosition, Phone phone) {
        this.setCoord(location);
        this.datePosition = datePosition;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getDatePosition() {
        return datePosition;
    }

    public void setDatePosition(String datePosition) {
        this.datePosition = datePosition;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public void setCoord(Location location) {
        this.setLatitude(location.getLatitude());
        this.setLongitude(location.getLongitude());
    }

    public void setAdresseFields(Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(this.getLatitude(), this.getLongitude(), 1);
            Address obj = addresses.get(0);
            this.setAdresse(obj.getAddressLine(0));
            this.setPays(obj.getCountryName());
            this.setCodePostal(obj.getPostalCode());
            this.setVille(obj.getLocality());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
