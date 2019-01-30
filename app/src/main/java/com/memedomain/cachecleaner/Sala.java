package com.memedomain.cachecleaner;

public class Sala {

    private Integer id;
    private String rodzaj;
    private Integer wielkosc;

    public Sala(String rodzaj, Integer wielkosc) {
        this.rodzaj = rodzaj;
        this.wielkosc = wielkosc;
    }

    public Integer getId() {
        return id;
    }

    public String getRodzaj() {
        return rodzaj;
    }

    public Integer getWielkosc() {
        return wielkosc;
    }
}
