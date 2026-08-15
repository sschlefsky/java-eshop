package net;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import common.Commands;
import common.*;
import common.exceptions.*;

public class ClientRequestProcessor implements Runnable {

    private final ObjectOutputStream outputZumClient;
    private final ObjectInputStream inputVomClient;
    private final IEShop eShop;

    public ClientRequestProcessor(Socket s, IEShop eShop) throws IOException {
        this.eShop = eShop;
        outputZumClient = new ObjectOutputStream(s.getOutputStream());
        inputVomClient = new ObjectInputStream(s.getInputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Commands erhalteneDaten = (Commands) inputVomClient.readObject();
                handleCommandRequest(erhalteneDaten);
            } catch (SocketException e) {
                System.err.println("Client hat Verbindung geschlossen");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.err.println("Fehler bei der Client-Server-Kommunikation");
            }
        }
    }

    private void handleCommandRequest(Commands erhalteneDaten) throws IOException, ClassNotFoundException {
        System.err.println("Vom Client empfangende Daten: " + erhalteneDaten);

        switch (erhalteneDaten) {
            case CMD_GET_ANGEMELDET -> handleGetAngemeldet();
            case CMD_BESTAETIGEN -> handleBestaetigen();
            case CMD_BEENDEN -> handleBeenden();
            case CMD_IST_KUNDE -> handleIstKunde();
            case CMD_KUNDE_REGISTRIEREN -> handleKundeRegistrieren();
            case CMD_ERSTELLEN_ADRESSE -> handleErstellenAdresse();
            case CMD_MITARBEITER_REGISTRIEREN -> handleMitarbeiterRegistrieren();
            case CMD_GET_ARTIKELLISTE -> handleGetArtikelliste();
            case CMD_SORTIERT_NACH_BEZEICHNUNG -> handleSortiertNachBezeichnung();
            case CMD_SORTIERT_NACH_ARTIKELNUMMER -> handleSortiertNachArtikelnummer();
            case CMD_GET_EREIGNISLISTE -> handleGetEreignisliste();
            case CMD_ERZEUGEN_EREIGNIS -> handleErzeugenEreignis();
            case CMD_ANLEGEN_ARTIKEL -> handleAnlegenArtikel();
            case CMD_ANLEGEN_MASSEN_ARTIKEL -> handleAnlegenMassenArtikel();
            case CMD_ERHOEHEN_ARTIKEL_BESTAND -> handleErhoehenArtikelBestand();
            case CMD_FILTER_EREIGNISLISTE -> handleFilterEreignisliste();
            case CMD_ARTIKEL_HINZUFUEGEN_WARENKORB -> handleArtikelHinzufuegenWarenkorb();
            case CMD_ARTIKEL_ENTFERNEN -> handleArtikelEntfernenWarenkorb();
            case CMD_ARTIKEL_STUECKZAHL_AENDERN -> handleArtikelStueckzahlAendernWarenkorb();
            case CMD_WARENKORB_LEEREN -> handleWarenkorbLeeren();
            case CMD_GET_GESAMTPREIS -> handleGetGesamtpreis();
            case CMD_GET_WARENKORB_INHALT -> handleGetWarenkorbInhalt();
            case CMD_KAUF_ABSCHLIESSEN -> handleKaufAbschliessen();
            case CMD_LADEN_DATEN -> handleLadenDaten();
            case CMD_SPEICHERN_DATEN -> handleSpeichernDaten();

            default -> System.err.println("Ungueltige Anfrage empfangen!");
        }
    }

    private Commands getFehler(Exception e) {
        return switch (e) {

            case ArtikelExistiertNichtException i -> Commands.CMD_ARTIKEL_EXISITIERT_NICHT_EXCEPTION;
            case ArtikelExistiertBereitsException i -> Commands.CMD_ARTIKEL_EXISITIERT_BEREITS_EXCEPTION;
            case ArtikelNichtImWbException i -> Commands.CMD_ARTIKEL_NICHT_IM_WB_EXCEPTION;
            case ArtikelBereitsImWbException i -> Commands.CMD_ARTIKEL_BEREITS_IM_WB_EXCEPTION;
            case ClassNotFoundException i -> Commands.CMD_CLASS_NOT_FOUND_PERSISTENCE_EXCEPTION;
            case FalschePackungsgroesseException i -> Commands.CMD_FALSCHE_PACKUNGGROESSE_EXCEPTION;
            case GroessereStueckzahlException i -> Commands.CMD_GROESSERE_STUECKZAHL_EXCEPTION;
            case IOException i -> Commands.CMD_IO_PERSISTENCE_EXCEPTION;
            case LeereEingabeException i -> Commands.CMD_LEERE_EINGABE_EXCECPTION;
            case NegativeEingabeException i -> Commands.CMD_NEGATIVE_EINGABE_EXCEPTION;
            case NutzerExisitiertBereitsException i -> Commands.CMD_NUTZER_EXISTIERT_BEREITS_EXCEPTION;
            case NutzerExisistiertNichtException i -> Commands.CMD_NUTZER_EXISTIERT_NICHT_EXCEPTION;
            case UngueltigeStueckzahlException i -> Commands.CMD_UNGUELTIGE_STUECKZAHL_EXCEPTION;
            case UngueltigeBestandsException i -> Commands.CMD_UNGUELTIGE_BESTANDS_EXCEPTION;

            case null, default -> throw new RuntimeException("Nicht unterstützter Fehler");
        };
    }

    private void handleFehler(Exception e) throws IOException {
        outputZumClient.writeObject(getFehler(e));
        if (!(e instanceof IOException|| e instanceof ClassNotFoundException)) {
            outputZumClient.writeObject(e.getMessage());
            outputZumClient.reset();
        }
        outputZumClient. reset();
    }

    private void handleLadenDaten() throws IOException {
        try {
            eShop.ladenDaten();

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleSpeichernDaten() throws IOException {
        try {
            eShop.speichernDaten();

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleGetAngemeldet() throws IOException, ClassNotFoundException {
        Benutzer benutzer = eShop.getAngemeldet();

        outputZumClient.writeObject(Commands.CMD_GET_ANGEMELDET_RESP);
        outputZumClient.writeObject(benutzer);
        outputZumClient.reset();
    }

    private void handleBestaetigen() throws IOException {
        try {
            String passwort = (String) inputVomClient.readObject();
            int nummer = inputVomClient.readInt();
            eShop.bestaetigen(passwort, nummer);

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleBeenden() throws IOException {
        eShop.beenden();
    }

    private void handleIstKunde() throws IOException, ClassNotFoundException {
        boolean bool = eShop.istKunde();

        outputZumClient.writeObject(Commands.CMD_IST_KUNDE_RESP);
        outputZumClient.writeBoolean(bool);
        outputZumClient.reset();
    }

    private void handleKundeRegistrieren() throws IOException {
        try {
            String passwort = (String) inputVomClient.readObject();
            String name = (String) inputVomClient.readObject();
            Adresse adresse = (Adresse) inputVomClient.readObject();
            Kunde kunde = eShop.kundeRegistrieren(passwort, name, adresse);

            outputZumClient.writeObject(Commands.CMD_KUNDE_REGISTRIEREN_RESP);
            outputZumClient.writeObject(kunde);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleErstellenAdresse() throws IOException {
        try {
            String strasse = (String) inputVomClient.readObject();
            int hausnummer = inputVomClient.readInt();
            int plz = inputVomClient.readInt();
            String ort = (String) inputVomClient.readObject();
            Adresse adresse = eShop.erstellenAdresse(strasse, hausnummer, plz, ort);

            outputZumClient.writeObject(Commands.CMD_ERSTELLEN_ADRESSE_RESP);
            outputZumClient.writeObject(adresse);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleMitarbeiterRegistrieren() throws IOException {
        try {
            String name = (String) inputVomClient.readObject();
            String passwort = (String) inputVomClient.readObject();
            Mitarbeiter mitarbeiter = eShop.mitarbeiterRegistrieren(name, passwort);

            outputZumClient.writeObject(Commands.CMD_MITARBEITER_REGISTRIEREN_RESP);
            outputZumClient.writeObject(mitarbeiter);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleGetArtikelliste() throws IOException, ClassNotFoundException {
        List<Artikel> artikelListe = eShop.getArtikelListe();

        outputZumClient.writeObject(Commands.CMD_GET_ARTIKELLISTE_RESP);
        outputZumClient.writeObject(artikelListe);
        outputZumClient.reset();
    }

    private void handleSortiertNachBezeichnung() throws IOException, ClassNotFoundException {
        List<Artikel> artikelListe = eShop.sortiertNachBezeichnung();

        outputZumClient.writeObject(Commands.CMD_SORTIERT_NACH_BEZEICHNUNG_RESP);
        outputZumClient.writeObject(artikelListe);
        outputZumClient.reset();
    }

    private void handleSortiertNachArtikelnummer() throws IOException, ClassNotFoundException {
        List<Artikel> artikelListe = eShop.sortiertNachArtikelnummer();

        outputZumClient.writeObject(Commands.CMD_SORTIERT_NACH_ARTIKELNUMMER_RESP);
        outputZumClient.writeObject(artikelListe);
        outputZumClient.reset();
    }

    private void handleAnlegenArtikel() throws IOException {
        try {
            Benutzer benutzer = (Benutzer) inputVomClient.readObject();
            int artikelnummer = inputVomClient.readInt();
            String bezeichnung = (String) inputVomClient.readObject();
            int bestand = inputVomClient.readInt();
            double preis = inputVomClient.readDouble();
            eShop.anlegenArtikel(benutzer, artikelnummer, bezeichnung, bestand, preis);

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleAnlegenMassenArtikel() throws IOException {
        try {
            Benutzer benutzer= (Benutzer) inputVomClient.readObject();
            int artikelnummer =  inputVomClient.readInt();
            String bezeichnung = (String) inputVomClient.readObject();
            int bestand =  inputVomClient.readInt();
            double preis =  inputVomClient.readDouble();
            int packungsGroesse =  inputVomClient.readInt();
            eShop.anlegenMassenArtikel(benutzer,artikelnummer,bezeichnung,bestand, preis,packungsGroesse);

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleErhoehenArtikelBestand() throws IOException {
        try {
            Benutzer benutzer= (Benutzer) inputVomClient.readObject();
            int artikelnummer = inputVomClient.readInt();
            int bestand = inputVomClient.readInt();
            eShop.erhoehenArtikelBestand(benutzer, artikelnummer, bestand);

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleErzeugenEreignis() throws IOException, ClassNotFoundException {
        Benutzer benutzer = (Benutzer) inputVomClient.readObject();
        String aktion = (String) inputVomClient.readObject();
        int artikelnummer = inputVomClient.readInt();
        eShop.erzeugenEreignis(benutzer, aktion, artikelnummer);
    }

    private void handleGetEreignisliste() throws IOException, ClassNotFoundException {
        List<Ereignis> ereignisListe = eShop.getEreignisListe();

        outputZumClient.writeObject(Commands.CMD_GET_EREIGNISLISTE_RESP);
        outputZumClient.writeObject(ereignisListe);
        outputZumClient.reset();
    }

    private void handleFilterEreignisliste() throws IOException {
        try {
            int artikelnummer = inputVomClient.readInt();
            List<Ereignis> ereignisListe = eShop.filterEreignisliste(artikelnummer);

            outputZumClient.writeObject(Commands.CMD_FILTER_EREIGNISLISTE_RESP);
            outputZumClient.writeObject(ereignisListe);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleGetWarenkorbInhalt() throws IOException, ClassNotFoundException {
        Map<Artikel, Integer> warenkorbInhalt = eShop.getWarenkorbInhalt();

        outputZumClient.writeObject(Commands.CMD_GET_WARENKORB_INHALT_RESP);
        outputZumClient.writeObject(warenkorbInhalt);
        outputZumClient.reset();
    }

    private void handleArtikelHinzufuegenWarenkorb() throws IOException {
        try {
            int artikelnummer = inputVomClient.readInt();
            int stueckzahl = inputVomClient.readInt();
            eShop.artikelHinzufuegenWarenkorb(artikelnummer,stueckzahl);

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }
    private void handleArtikelEntfernenWarenkorb() throws IOException {
        try {
            int artikelnummer = inputVomClient.readInt();
            eShop.artikelEntfernenWarenkorb(artikelnummer);

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();
        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleArtikelStueckzahlAendernWarenkorb() throws IOException {
        try {
            int artikelnummer =  inputVomClient.readInt();
            int neueStueckzahl = inputVomClient.readInt();
            eShop.artikelStueckzahlAendernWarenkorb(artikelnummer,neueStueckzahl);

            outputZumClient.writeObject(Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG);
            outputZumClient.reset();

        } catch (Exception e) {
            handleFehler(e);
        }
    }

    private void handleWarenkorbLeeren() throws IOException {
        eShop.warenkorbLeeren();
    }

    private void handleGetGesamtpreis() throws IOException, ClassNotFoundException {
        double gesamtpreis = eShop.getGesamtpreis();

        outputZumClient.writeObject(Commands.CMD_GET_GESAMTPREIS_RESP);
        outputZumClient.writeDouble(gesamtpreis);
        outputZumClient.reset();
    }

    private void handleKaufAbschliessen() throws IOException, ClassNotFoundException {
        Rechnung rechnung = eShop.kaufAbschliessen();

        outputZumClient.writeObject(Commands.CMD_KAUF_ABSCHLIESSEN_RESP);
        outputZumClient.writeObject(rechnung);
        outputZumClient.reset();
    }
}