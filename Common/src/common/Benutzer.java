package common;

import java.io.Serializable;

public class Benutzer implements Serializable {

    private final String passwort;
    private final String name;
    private final int nummer ;

    public Benutzer(String passwort, String name, int nummer) {
        this.passwort = passwort;
        this.name = name;
        this.nummer = nummer;
    }

    public String getPasswort() { return passwort; }

    public String getName() {
        return name;
    }

    public int getNummer() {return nummer;}
}
