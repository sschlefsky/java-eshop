package common;

import java.io.Serializable;

public class Artikel implements Serializable {

    private final String bezeichnung;
    private final int artikelnummer;
    private int bestand;
    private final double preis;

    public Artikel(int artikelnummer, String bezeichnung, int bestand, double preis) {
        this.bezeichnung = bezeichnung;
        this.artikelnummer = artikelnummer;
        this.bestand = bestand;
        this.preis = preis;
    }

    public String getBezeichnung() { return bezeichnung; }

    public int getArtikelnummer() { return artikelnummer; }

    public int getBestand() { return bestand; }

    public void setBestand(int bestand) { this.bestand = bestand; }

    public void erhoehenBestand(int bestand) { this.bestand += bestand; }

    public double getPreis() { return preis; }

    public String toString() {
        return "Nr.: " + artikelnummer + " / Bezeichnung: " + bezeichnung + " / Bestand: " + bestand + " / Preis: " + preis + " Euro";
    }
}