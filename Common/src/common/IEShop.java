package common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import common.exceptions.*;

public interface IEShop {

    void ladenDaten() throws IOException, ClassNotFoundException, IOPersistenceException, ClassNotFoundPersistenceException;

    void speichernDaten() throws IOException, ClassNotFoundException, IOPersistenceException;

    Benutzer getAngemeldet() throws IOException, ClassNotFoundException;

    void bestaetigen(String passwort, int nummer) throws NutzerExisistiertNichtException, IOException, ClassNotFoundException;

    void beenden() throws IOException;

    boolean istKunde() throws IOException, ClassNotFoundException;

    Kunde kundeRegistrieren(String passwort, String name, Adresse adresse) throws NutzerExisitiertBereitsException, IOException, ClassNotFoundException, LeereEingabeException;

    Adresse erstellenAdresse(String strasse, int hausnummer, int plz, String ort) throws IOException, ClassNotFoundException, LeereEingabeException;

    Mitarbeiter mitarbeiterRegistrieren(String name, String passwort) throws NutzerExisitiertBereitsException, IOException, ClassNotFoundException, LeereEingabeException;

    List<Artikel> getArtikelListe() throws IOException, ClassNotFoundException;

    List<Artikel> sortiertNachBezeichnung() throws IOException, ClassNotFoundException;

    List<Artikel> sortiertNachArtikelnummer() throws IOException, ClassNotFoundException;

    void anlegenArtikel(Benutzer benutzer, int artikelnummer, String bezeichnung, int bestand, double preis) throws ArtikelExistiertBereitsException, NegativeEingabeException, IOException, ClassNotFoundException;

    void anlegenMassenArtikel(Benutzer benutzer, int artikelnummer, String bezeichnung, int bestand, double preis, int packungsGroesse) throws ArtikelExistiertBereitsException, NegativeEingabeException, FalschePackungsgroesseException, IOException, ClassNotFoundException;

    void erhoehenArtikelBestand(Benutzer benutzer, int artikelnummer, int bestand) throws NegativeEingabeException, ArtikelExistiertNichtException, UngueltigeBestandsException, IOException, ClassNotFoundException;

    void erzeugenEreignis(Benutzer benutzer, String aktion, int artikelnummer) throws IOException;

    List<Ereignis> getEreignisListe() throws IOException, ClassNotFoundException;

    List<Ereignis> filterEreignisliste(int artikelnummer) throws ArtikelExistiertNichtException, IOException, ClassNotFoundException;

    Map<Artikel, Integer> getWarenkorbInhalt() throws IOException, ClassNotFoundException;

    void artikelHinzufuegenWarenkorb(int artikelnummer, int stueckzahl) throws ArtikelExistiertNichtException,
            GroessereStueckzahlException, NegativeEingabeException, ArtikelBereitsImWbException, ArtikelNichtImWbException,
            UngueltigeStueckzahlException, IOException, ClassNotFoundException;

    void artikelEntfernenWarenkorb(int artikelnummer) throws ArtikelExistiertNichtException,
            ArtikelNichtImWbException, IOException, ClassNotFoundException;

    void artikelStueckzahlAendernWarenkorb(int artikelnummer, int neueStueckzahl) throws ArtikelNichtImWbException,
            GroessereStueckzahlException, UngueltigeStueckzahlException, ArtikelExistiertNichtException, IOException, ClassNotFoundException;

    void warenkorbLeeren() throws IOException;

    double getGesamtpreis() throws IOException, ClassNotFoundException;

    Rechnung kaufAbschliessen() throws IOException, ClassNotFoundException;
}
