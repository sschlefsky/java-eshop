package domain;

import java.io.IOException;
import java.util.*;

import common.*;
import common.exceptions.*;
import persistence.DateiPersistenceVerwaltung;

public class EShop implements IEShop {

    private final DateiPersistenceVerwaltung dpv;
    private final Nutzerverwaltung nw;
    private final Artikelverwaltung av;
    private Benutzer angemeldet;

    public EShop() {
        this.dpv = new DateiPersistenceVerwaltung();
        this.nw  = new Nutzerverwaltung(dpv);
        this.av = new Artikelverwaltung(dpv);
    }
    @Override
    public void ladenDaten() throws IOException, ClassNotFoundException {
        nw.setBenutzerListe();
        av.setArtikelListe();
        av.setEreignisListe();

        if (nw.hatKeineBenutzer()) {
            initialisiereDemoDaten();
            speichernDaten();
        }
    }

    private void initialisiereDemoDaten() {
        try {
            // Demo-Mitarbeiter
            nw.mitarbeiterRegistieren("demo123", "Demo Mitarbeiter");

            // Demo-Kunde
            Adresse demoAdresse = nw.erstellenAdresse(
                    "Musterstraße",
                    10,
                    12345,
                    "Musterstadt"
            );

            nw.kundeRegistieren(
                    "demo123",
                    "Demo Kunde",
                    demoAdresse
            );

            // Demo-Artikel
            av.getArtikelListe().add(new Artikel(1, "Tee", 20, 2.50));
            av.getArtikelListe().add(new Artikel(2, "Kaffee", 15, 5.00));
            av.getArtikelListe().add(new Artikel(3, "Kakao", 10, 3.30));

            // Massengutartikel: Bestand muss ein Vielfaches der Packungsgröße sein
            av.getArtikelListe().add(
                    new Massengutartikel(4, "Wasser", 48, 2.00, 6)
            );

        } catch (NutzerExisitiertBereitsException | LeereEingabeException e) {
            throw new IllegalStateException(
                    "Die Demo-Daten konnten nicht initialisiert werden.", e
            );
        }
    }

    @Override
    public void speichernDaten() throws IOException {
        nw.speichereBenutzerListe();
        av.speichereArtikelListe();
        av.speichereEreignisListe();
    }

    @Override
    public Benutzer getAngemeldet() {
        return angemeldet;
    }

    @Override
    public void bestaetigen(String passwort, int nummer) throws NutzerExisistiertNichtException {
        angemeldet = nw.einloggen(passwort, nummer);
        if (angemeldet == null) {
            throw new NutzerExisistiertNichtException("Der Nutzer exisitiert nicht.");
        }
    }

    @Override
    public void beenden() {
        angemeldet = null;
    }

    @Override
    public boolean istKunde() {
        return nw.istKunde(angemeldet);
    }

    @Override
    public Kunde kundeRegistrieren(String passwort, String name, Adresse adresse) throws NutzerExisitiertBereitsException, LeereEingabeException {
        return nw.kundeRegistieren(passwort, name, adresse);
    }

    @Override
    public Adresse erstellenAdresse(String strasse, int hausnummer, int plz, String ort) throws LeereEingabeException {
        return nw.erstellenAdresse(strasse, hausnummer, plz, ort);
    }

    @Override
    public Mitarbeiter mitarbeiterRegistrieren(String name, String passwort) throws NutzerExisitiertBereitsException, LeereEingabeException {
        return nw.mitarbeiterRegistieren(passwort, name);
    }

    @Override
    public List<Artikel> getArtikelListe() {
        return av.getArtikelListe();
    }

    @Override
    public List<Artikel> sortiertNachBezeichnung() {
        List<Artikel> artikelListe = av.getArtikelListe();
        artikelListe.sort(Comparator.comparing(Artikel::getBezeichnung));
        return artikelListe;
    }

    @Override
    public List<Artikel> sortiertNachArtikelnummer() {
        List<Artikel> artikelListe = av.getArtikelListe();
        artikelListe.sort(Comparator.comparing(Artikel::getArtikelnummer));
        return artikelListe;
    }

    @Override
    public void anlegenArtikel(Benutzer benutzer, int artikelnummer, String bezeichnung, int bestand, double preis) throws ArtikelExistiertBereitsException, NegativeEingabeException, IOException, ClassNotFoundException {
        if (artikelnummer <= 0 || bestand <= 0 || preis <= 0) {
            throw new NegativeEingabeException("Negativen Wert oder den Wert '0' eingegeben.");
        }
        if (!av.pruefenArtikelVorhanden(bezeichnung, artikelnummer).isEmpty()) {
            throw new ArtikelExistiertBereitsException("Der Artikel existiert bereits.");
        }
        Artikel artikel = new Artikel(artikelnummer, bezeichnung, bestand, preis);
        av.getArtikelListe().add(artikel);

        erzeugenEreignis(benutzer, " hat " + bestand + " " + bezeichnung + " (" + artikelnummer +") " + " hinzugefügt ", artikelnummer);
    }

    @Override
    public void anlegenMassenArtikel(Benutzer benutzer, int artikelnummer, String bezeichnung, int bestand, double preis, int packungsGroesse) throws ArtikelExistiertBereitsException, NegativeEingabeException, FalschePackungsgroesseException {
        if (artikelnummer <= 0 || bestand <= 0 || preis <= 0) {
            throw new NegativeEingabeException("Negativen Wert oder den Wert '0' eingegeben.");
        }
        if (!av.pruefenArtikelVorhanden(bezeichnung, artikelnummer).isEmpty()) {
            throw new ArtikelExistiertBereitsException("Der Artikel existiert bereits.");
        }
        if (bestand % packungsGroesse != 0) {
            throw new FalschePackungsgroesseException ("Der Bestand muss ein vielfaches der Packungsgröße (" + packungsGroesse + ") sein.");
        }
        Massengutartikel artikel = new Massengutartikel(artikelnummer, bezeichnung, bestand, preis, packungsGroesse);
        av.getArtikelListe().add(artikel);

        erzeugenEreignis(benutzer, " hat " + bestand + " " + bezeichnung + " (" + artikelnummer +") " + " hinzugefügt ", artikelnummer);
    }

    @Override
    public void erhoehenArtikelBestand(Benutzer benutzer, int artikelnummer, int bestand) throws NegativeEingabeException, ArtikelExistiertNichtException, UngueltigeBestandsException {
        if (bestand <= 0) {
            throw new NegativeEingabeException("Negativen Wert oder den Wert '0' eingegeben.");
        }
        Artikel artikel = av.findeArtikelInArtikelListe(artikelnummer);
        if (artikel == null) {
            throw new ArtikelExistiertNichtException("Artikel existiert nicht, geben Sie eine andere Artikelnummer ein.");
        }
        if (!(artikel instanceof Massengutartikel massenartikel)) { //Einzelartikel
            av.erhoehenBestand(artikelnummer, bestand);

            Artikel a = av.findeArtikelInArtikelListe(artikelnummer);
            String bezeichnung = a.getBezeichnung();
            erzeugenEreignis(benutzer, " hat " + bestand + " " + bezeichnung + " (" + artikelnummer +") " + " hinzugefügt ", artikelnummer);

        } else { //Massengut
            if (bestand % massenartikel.getPackungsGroesse() != 0) {
                throw new UngueltigeBestandsException("Der neue Bestand muss ein vielfaches der Packungsgröße (" + massenartikel.getPackungsGroesse() + ") sein.");
            }
            av.erhoehenBestand(artikelnummer, bestand);

            Artikel a = av.findeArtikelInArtikelListe(artikelnummer);
            String bezeichnung = a.getBezeichnung();
            erzeugenEreignis(benutzer, " hat " + bestand + " " + bezeichnung + " (" + artikelnummer +") " + " hinzugefügt ", artikelnummer);
        }
    }

    @Override
    public void erzeugenEreignis(Benutzer benutzer, String aktion, int artikelnummer) {

        String prefix = istKunde() ? "K.: " : "M: ";

        Ereignis ereignisNeu = new Ereignis(prefix + benutzer.getName() + " Nr.: " + benutzer.getNummer(), artikelnummer);
        ereignisNeu.setNeuesEreignis(aktion);
        av.getEreignisListe().add(ereignisNeu);
    }

    @Override
    public List<Ereignis> getEreignisListe() {
        List<Ereignis> ereignisListeArtikel = av.getEreignisListe();
        ereignisListeArtikel.sort(Comparator.comparing(Ereignis::getDatum));
        Collections.reverse(ereignisListeArtikel);
        return ereignisListeArtikel;
    }

    @Override
    public List<Ereignis> filterEreignisliste(int artikelnummer) throws ArtikelExistiertNichtException {
        Artikel a = this.av.findeArtikelInArtikelListe(artikelnummer);
        if (a == null) {
            throw new ArtikelExistiertNichtException("Artikel existiert nicht, geben Sie eine andere Artikelnummer ein.");
        }

        List<Ereignis> ereignisListeArtikel = av.filterEreignisliste(artikelnummer);
        ereignisListeArtikel.sort(Comparator.comparing(Ereignis::getDatum));
        Collections.reverse(ereignisListeArtikel);
        return ereignisListeArtikel;
    }

    @Override
    public Map<Artikel, Integer> getWarenkorbInhalt() {
        Kunde kunde = (Kunde) angemeldet;
        return kunde.getWarenkorb().getWarenkorbArtikel();
    }

    @Override
    public void artikelHinzufuegenWarenkorb(int artikelnummer, int stueckzahl) throws ArtikelExistiertNichtException, GroessereStueckzahlException, NegativeEingabeException, ArtikelBereitsImWbException, ArtikelNichtImWbException, UngueltigeStueckzahlException {
        Artikel artikel = av.findeArtikelInArtikelListe(artikelnummer);
        if (artikel == null) {
            throw new ArtikelExistiertNichtException("Artikel existiert nicht, geben Sie eine andere Artikelnummer ein.");
        }
        if (stueckzahl <= 0) {
            throw new NegativeEingabeException("Die Stückzahl muss größer als Null sein.");
        }
        if (stueckzahl > artikel.getBestand()) {
            throw new GroessereStueckzahlException("Es sind nur " + artikel.getBestand() + " Einheiten im Bestand verfügbar.");
        }
        if (artikel instanceof Massengutartikel massengutartikel) {
            int packungsGroesse = massengutartikel.getPackungsGroesse();
            if (stueckzahl % packungsGroesse != 0) {
                throw new UngueltigeStueckzahlException("Die hinzugefügte Stückzahl muss ein Vielfaches der Pckungsgröße (" + packungsGroesse + ") sein.");
            }
        }
        Kunde kunde = (Kunde) angemeldet;
        Map<Artikel, Integer> warenkorbArtikel = kunde.getWarenkorb().getWarenkorbArtikel();
        boolean ergebnis = pruefenWarenkorbEnthaeltArtikel(warenkorbArtikel, artikel);
        if (ergebnis) {
            throw new ArtikelBereitsImWbException("Artikel " + artikel.getBezeichnung() + " ist bereits im Warenkorb.");
        } else {
            kunde.getWarenkorb().hinzufuegenWarenkorbArtikel(artikel, stueckzahl);
            double neuerGesamtpreis = stueckzahl*artikel.getPreis();
            kunde.getWarenkorb().setGesamtpreis(neuerGesamtpreis);
        }
    }

    private boolean pruefenWarenkorbEnthaeltArtikel(Map<Artikel, Integer> warenkorbArtikel, Artikel artikel) {
        for(Artikel a : warenkorbArtikel.keySet()){
            if(a.getArtikelnummer() == artikel.getArtikelnummer()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void artikelEntfernenWarenkorb(int artikelnummer) throws ArtikelExistiertNichtException, ArtikelNichtImWbException {
        Artikel artikel = av.findeArtikelInArtikelListe(artikelnummer);
        if (artikel == null) {
            throw new ArtikelExistiertNichtException("Artikel existiert nicht, geben Sie eine andere Artikelnummer ein.");
        }
        Kunde kunde = (Kunde) angemeldet;
        Map<Artikel, Integer> warenkorbArtikel = kunde.getWarenkorb().getWarenkorbArtikel();
        if (!warenkorbArtikel.containsKey(artikel)){
            throw new ArtikelNichtImWbException("Der Artikel ist nicht im Warenkorb.");
        } else {
            int menge = warenkorbArtikel.get(artikel);
            double neuerGesamtpreis = -menge * artikel.getPreis();
            kunde.getWarenkorb().setGesamtpreis(neuerGesamtpreis);
            kunde.getWarenkorb().entfernenWarenkorbArtikel(artikel);
        }
    }

    @Override
    public void artikelStueckzahlAendernWarenkorb(int artikelnummer, int neueStueckzahl) throws ArtikelNichtImWbException, GroessereStueckzahlException, UngueltigeStueckzahlException, ArtikelExistiertNichtException {
        Artikel artikel = av.findeArtikelInArtikelListe(artikelnummer);
        if (artikel == null) {
            throw new ArtikelExistiertNichtException("Artikel existiert nicht, geben Sie eine andere Artikelnummer ein.");
        }
        if (neueStueckzahl > artikel.getBestand()) {
            throw new GroessereStueckzahlException("Es sind nur " + artikel.getBestand() + " Einheiten im Bestand verfügbar.");
        }
        if (artikel instanceof Massengutartikel massengutartikel) {
            int packungsGroesse = massengutartikel.getPackungsGroesse();
            if (neueStueckzahl % packungsGroesse != 0) {
                throw new UngueltigeStueckzahlException("Die hinzugefügte Stückzahl muss ein Vielfaches der Pckungsgröße (" + packungsGroesse + ") sein.");
            }
        }
        Kunde kunde = (Kunde) angemeldet;
        Map<Artikel, Integer> warenkorbArtikel = kunde.getWarenkorb().getWarenkorbArtikel();

        if (!warenkorbArtikel.containsKey(artikel)) {
            throw new ArtikelNichtImWbException("Der Artikel ist nicht im Warenkorb.");}
        if (neueStueckzahl <= 0) {
            throw new UngueltigeStueckzahlException("Die Stückzahl muss größer als Null sein.");
        } else {
            int mengeAlt = warenkorbArtikel.get(artikel);
            kunde.getWarenkorb().entfernenWarenkorbArtikel(artikel);
            kunde.getWarenkorb().hinzufuegenWarenkorbArtikel(artikel, neueStueckzahl);

            double neuerGesamtpreis = -mengeAlt*artikel.getPreis();
            kunde.getWarenkorb().setGesamtpreis(neuerGesamtpreis);
            neuerGesamtpreis = neueStueckzahl*artikel.getPreis();
            kunde.getWarenkorb().setGesamtpreis(neuerGesamtpreis);
        }
    }

    @Override
    public void warenkorbLeeren() {
        Kunde kunde = (Kunde) angemeldet;
        kunde.getWarenkorb().leerenWarenkorb();
    }

    @Override
    public double getGesamtpreis() {
        Kunde kunde = (Kunde) angemeldet;
        return kunde.getWarenkorb().getGesamtpreis();
    }

    @Override
    public Rechnung kaufAbschliessen() {
        Kunde kunde = (Kunde) angemeldet;
        Warenkorb warenkorb = kunde.getWarenkorb();
        Date datum = new Date();
        Rechnung r = new Rechnung(datum, Map.copyOf(warenkorb.getWarenkorbArtikel()), warenkorb.getGesamtpreis());
        for (Map.Entry<Artikel, Integer> eintrag : warenkorb.getWarenkorbArtikel().entrySet()) {
            Artikel artikel = eintrag.getKey();
            Integer menge = eintrag.getValue();
            av.aktualisiereBestand(artikel, menge);
            erzeugenEreignis(kunde, " hat " + menge + " " + artikel.getBezeichnung() + " (" + artikel.getArtikelnummer() +") " + " gekauft ", artikel.getArtikelnummer());
        }
        warenkorbLeeren();
        return r;
    }
}