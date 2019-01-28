package com.memedomain.cachecleaner;

import java.util.Set;

public class Pacjent {

    private Long id;
    private String imie;
    private String nazwisko;
    private String pesel;
    private String telefon;
    private Adres adres;
    private Set<Lekarz> lekarzs;

    public Long getId() {
        return id;
    }

    public String getImie() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public String getPesel() {
        return pesel;
    }

    public String getTelefon() {
        return telefon;
    }

    public Adres getAdres() {
        return adres;
    }

    public Set<Lekarz> getLekarzs() {
        return lekarzs;
    }
}
