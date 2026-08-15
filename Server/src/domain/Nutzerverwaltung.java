package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.*;
import common.exceptions.LeereEingabeException;
import common.exceptions.NutzerExisitiertBereitsException;
import persistence.DateiPersistenceVerwaltung;

public class Nutzerverwaltung {

    private List<Benutzer> benutzerListe;
    private final DateiPersistenceVerwaltung dpv;

    public Nutzerverwaltung(DateiPersistenceVerwaltung dpv) {
        this.dpv = dpv;
        this.benutzerListe = new ArrayList<>();
    }

    public void setBenutzerListe() throws IOException, ClassNotFoundException {
        benutzerListe = dpv.leseBenutzerListe();
    }

    public boolean hatKeineBenutzer() {
        return benutzerListe.isEmpty();
    }

    public void speichereBenutzerListe() throws IOException {
        dpv.schreibeBenutzerListe(benutzerListe);
    }

    public Benutzer einloggen(String passwort, int nummer) {
        for (Benutzer benutzer : benutzerListe) {
            if (benutzer.getPasswort().equals(passwort) && benutzer.getNummer()==(nummer)) {
                return benutzer;
            }
        }
        return null;
    }

    public boolean istKunde(Benutzer benutzer) {
        return benutzer instanceof Kunde;
    }

    public List<Benutzer> findetNutzerInNutzerliste(String passwort, String name) {
        List<Benutzer> suchErgebnis = new ArrayList<>();
        for (Benutzer benutzer : benutzerListe) {
            if (benutzer.getName().equals(name) && benutzer.getPasswort().equals(passwort)) {
                suchErgebnis.add(benutzer);
            }
        }
        return suchErgebnis;
    }

    public Mitarbeiter mitarbeiterRegistieren(String passwort, String name) throws NutzerExisitiertBereitsException, LeereEingabeException {
        if (passwort.isEmpty() || name.isEmpty()) {
            throw new LeereEingabeException("Die Eingabe war leer.");
        }
        if (!findetNutzerInNutzerliste(passwort, name).isEmpty()) {
            throw new NutzerExisitiertBereitsException("Der Nutzer existiert bereits.");
        }
        int nummer = getErsteVerfuegbareNummer();

        Mitarbeiter m = new Mitarbeiter(passwort, name, nummer);
        benutzerListe.add(m);
        return m;
    }

    public Kunde kundeRegistieren(String passwort, String name, Adresse adresse) throws NutzerExisitiertBereitsException, LeereEingabeException {
        if (passwort.isEmpty() || name.isEmpty()) {
            throw new LeereEingabeException("Die Eingabe war leer.");
        }
        if (!findetNutzerInNutzerliste(passwort, name).isEmpty()) {
            throw new NutzerExisitiertBereitsException("Der Nutzer existiert bereits.");
        }
        int nummer = getErsteVerfuegbareNummer();

        Kunde k = new Kunde(passwort, name, adresse, nummer);
        benutzerListe.add(k);
        return k;
    }

    public Adresse erstellenAdresse(String strasse, int hausnummer, int plz, String ort) throws LeereEingabeException {
        if (strasse.isEmpty() || ort.isEmpty()) {
            throw new LeereEingabeException("Die Eingabe war leer.");
        }
        return new Adresse(strasse, hausnummer, plz, ort);
    }

    private int getErsteVerfuegbareNummer() {
        List<Integer> belegteNummern = benutzerListe.stream().map(Benutzer::getNummer).toList();

        for (int i = benutzerListe.size(); i >= 0; i--) {
            if (!belegteNummern.contains(i)) {
                return i;
            }
        }
        return 0;
    }
}