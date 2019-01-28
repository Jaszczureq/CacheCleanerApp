package com.memedomain.cachecleaner;

import com.google.gson.annotations.SerializedName;

public class Adres {

    private Long id;
    private String kraj;
    private String wojewodztwo;
    private String miasto;
    private String kodPocztowy;
    private String ulica;
    private int nrDomu;
    private int nrMieszkania;

    @SerializedName("body")
    private String text;

    public Adres(Long id, String kraj) {
        this.id = id;
        this.kraj = kraj;
    }

    public Long getId() {
        return id;
    }

    public String getKraj() {
        return kraj;
    }

    public String getWojewodztwo() {
        return wojewodztwo;
    }

    public String getMiasto() {
        return miasto;
    }

    public String getKodPocztowy() {
        return kodPocztowy;
    }

    public String getUlica() {
        return ulica;
    }

    public int getNrDomu() {
        return nrDomu;
    }

    public int getNrMieszkania() {
        return nrMieszkania;
    }

    public String getText() {
        return text;
    }
}