package common;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Rechnung implements Serializable {

    private final Date datum;
    private final Map<Artikel, Integer> gekaufteArtikel;
    private final double gesamtpreis;

    public Rechnung(Date datum, Map<Artikel, Integer> gekaufteArtikel, double gesamtpreis) {
        this.datum = datum;
        this.gekaufteArtikel = gekaufteArtikel;
        this.gesamtpreis = gesamtpreis;
    }

    public Date getKaufDatum() {
        return datum;
    }

    public Map<Artikel, Integer> getGekaufteArtikel() {
        return gekaufteArtikel;
    }

    public double getGesamtpreis() {
        return gesamtpreis;
    }
}