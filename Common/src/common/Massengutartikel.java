package common;
public class Massengutartikel extends Artikel {

    private final int packungsGroesse;

    public Massengutartikel(int artikelnummer, String bezeichnung, int bestand, double preis, int packungsGroesse) {
        super(artikelnummer, bezeichnung, bestand, preis);
        this.packungsGroesse = packungsGroesse;
    }

    public int getPackungsGroesse() { return packungsGroesse; }

    public String toString() {
        return super.toString() + ", Packungsgröße: " + getPackungsGroesse();
    }
}
