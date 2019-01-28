package com.memedomain.cachecleaner;

import java.util.Set;

public class Lekarz {

    private Long id;
    private String imie;
    private String nazwisko;
    private String pesel;
    private String telefon;
    private Adres adres;
    private Set<Pacjent> pacjents;

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

    public Set<Pacjent> getPacjents() {
        return pacjents;
    }
}
