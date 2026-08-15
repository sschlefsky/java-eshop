package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Warenkorb implements Serializable {

    private final Map<Artikel, Integer> warenkorbArtikel;
    private double gesamtpreis;

    public Warenkorb() {
        this.warenkorbArtikel = new HashMap<>();
        this.gesamtpreis = 0.0;
    }

    public Map<Artikel, Integer> getWarenkorbArtikel() {
        return warenkorbArtikel;
    }

    public void hinzufuegenWarenkorbArtikel(Artikel artikel, int stueckzahl) {
        this.warenkorbArtikel.put(artikel, stueckzahl);
    }
    public void entfernenWarenkorbArtikel(Artikel artikel) {
        this.warenkorbArtikel.remove(artikel);
    }

    public void leerenWarenkorb() {
        warenkorbArtikel.clear();
        gesamtpreis = 0.0;
    }

    public double getGesamtpreis() {
        return gesamtpreis;
    }

    public void setGesamtpreis(double neuerGesamtpreis) {
        this.gesamtpreis += neuerGesamtpreis;
    }
}