package common;

import java.io.Serializable;
import java.util.Date;

public class Ereignis implements Serializable {

    private final String name;
    private String ereignis;
    private final Date datum;
    private final int artikelnummer;

    public Ereignis(String name, int artikelnummer) {
        this.name = name;
        this.datum = new Date();
        this.artikelnummer = artikelnummer;
    }

    public void setNeuesEreignis(String neuesEreignis) {
        ereignis = neuesEreignis;
    }

    public Date getDatum() {
        return datum;
    }

    public int getArtikelnummer() {
        return artikelnummer;
    }

    public String toString() {
        return name + " " + ereignis;
    }
}