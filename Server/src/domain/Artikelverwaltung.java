package domain;

import java.io.IOException;
import java.util.*;

import common.Artikel;
import common.Ereignis;
import persistence.DateiPersistenceVerwaltung;

public class Artikelverwaltung {
    private List<Artikel> artikelListe;
    private final DateiPersistenceVerwaltung dpv;
    private List<Ereignis> ereignisListe;

    public Artikelverwaltung(DateiPersistenceVerwaltung dpv) {
        this.dpv = dpv;
        this.artikelListe = new ArrayList<>();
        this.ereignisListe = new ArrayList<>();
    }

    public List<Ereignis> getEreignisListe() {
        return ereignisListe;
    }

    public void setEreignisListe() throws IOException, ClassNotFoundException {
        ereignisListe = dpv.leseEreignisListe();
    }

    public void speichereEreignisListe() throws IOException {
        dpv.schreibeEreignisListe(ereignisListe);
    }

    public List<Ereignis> filterEreignisliste(int artikelnummer) {
        List<Ereignis> el = getEreignisListe();
        List<Ereignis> elNeu= new ArrayList<>();

        for(Ereignis e : el ) {
            if (e.getArtikelnummer() == artikelnummer) {
                elNeu.add(e);
            }
        }
        return elNeu;
    }

    public List<Artikel> getArtikelListe() {
        return artikelListe;
    }

    public void setArtikelListe() throws IOException, ClassNotFoundException {
        artikelListe = dpv.leseArtikelListe();
    }

    public void speichereArtikelListe() throws IOException {
        dpv.schreibeArtikelListe(artikelListe);
    }

    public List<Artikel> pruefenArtikelVorhanden(String bezeichnung, int artikelnummer) {
        List<Artikel> suchErgebnis = new ArrayList<>();
        for (Artikel artikel : artikelListe) {
            if (artikel.getBezeichnung().equals(bezeichnung) || artikel.getArtikelnummer() == artikelnummer) {
                    suchErgebnis.add(artikel);
            }
        }
        return suchErgebnis;
    }

    public void erhoehenBestand(int artikelnummer, int bestand) {
        for(Artikel artikel : artikelListe) {
            if(artikel.getArtikelnummer() == artikelnummer) {
                artikel.erhoehenBestand(bestand);
            }
        }
    }

    public Artikel findeArtikelInArtikelListe(int artikelnummer) {
        for(Artikel artikel : artikelListe) {
            if(artikel.getArtikelnummer() == artikelnummer) {
                return artikel;
            }
        }
        return null;
    }

    public void aktualisiereBestand(Artikel artikel, int verkaufteStueckzahl) {
        if (artikel != null && artikel.getBestand() >= verkaufteStueckzahl) {
            int neuerBestand = artikel.getBestand() - verkaufteStueckzahl;
            artikel.setBestand((neuerBestand));
        }
    }
}