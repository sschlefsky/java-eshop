package common;

public class Kunde extends Benutzer {

    private final Adresse adresse;
    private final Warenkorb warenkorb;

    public Kunde(String passwort, String name, Adresse adresse, int nummer) {
        super(passwort, name, nummer);
        this.adresse = adresse;
        this.warenkorb = new Warenkorb();
    }

    public Warenkorb getWarenkorb() {
        return this.warenkorb;
    }

    public Adresse getAdresse() { return this.adresse; }
}