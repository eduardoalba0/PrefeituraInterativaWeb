package br.edu.ifpr.bsi.prefeiturainterativaweb.model;

import java.io.Serializable;

import com.google.type.LatLng;

@SuppressWarnings("serial")
public class Localizacao implements Serializable {

    private double latitude;
    private double longitude;

    public Localizacao() {
    }

    public Localizacao(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Localizacao(LatLng latLng) {
        this.latitude = latLng.getLatitude();
        this.longitude = latLng.getLongitude();
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
}
