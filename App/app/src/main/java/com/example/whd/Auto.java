package com.example.whd;

public class Auto {

    // Attribute
    private String kennzeichen;
    private int km_stand;

    // Constructor
    public Auto (String kennzeichen, int kms) {
        this.kennzeichen = kennzeichen;
        this.km_stand = kms;
    }

    // Getter & Setter
    public String getKennzeichen() {
        return this.kennzeichen;
    }

    public void setKennzeichen(String kennzeichen) {
        this.kennzeichen = kennzeichen;
    }

    public int getKm_stand() {
        return this.km_stand;
    }

    public void setKm_stand(int km_stand) {
        this.km_stand = km_stand;
    }
}
