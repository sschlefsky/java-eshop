package common;

import java.io.Serializable;

public class Adresse implements Serializable {

    private final String strasse;
    private final int hausnummer;
    private final int plz;
    private final String ort;

    public Adresse(String strasse, int hausnummer, int plz, String ort) {
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.plz = plz;
        this.ort = ort;
    }

    public String toString() {
        return strasse + ", " + hausnummer + ", " + plz + ", " + ort;
    }
}
