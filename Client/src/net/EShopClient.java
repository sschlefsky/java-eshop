package net;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import common.*;
import common.IEShop;
import common.exceptions.*;

public class EShopClient implements IEShop {

    private final Socket socket;
    private final ObjectOutputStream outputZumServer;
    private final ObjectInputStream inputVomServer;

    public EShopClient() throws IOException {
        socket = new Socket("127.0.0.1", 33333);
        System.out.println("Verbunden!");
        socket.setSoTimeout(100000);
        outputZumServer = new ObjectOutputStream(socket.getOutputStream());
        inputVomServer = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void ladenDaten() throws IOException, ClassNotFoundException, IOPersistenceException, ClassNotFoundPersistenceException {
        outputZumServer.writeObject(Commands.CMD_LADEN_DATEN);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            switch (erhalteneDaten) {
                case Commands.CMD_IO_PERSISTENCE_EXCEPTION -> throw new IOPersistenceException("Fehler beim Laden der Daten");
                case Commands.CMD_CLASS_NOT_FOUND_PERSISTENCE_EXCEPTION -> throw new ClassNotFoundPersistenceException("Fehler beim Laden der Daten");
            }
        }
    }

    @Override
    public void speichernDaten() throws IOException, ClassNotFoundException, IOPersistenceException {
        outputZumServer.writeObject(Commands.CMD_SPEICHERN_DATEN);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            throw new IOPersistenceException("Fehler beim Speichern der Daten");
        }
    }

    @Override
    public Benutzer getAngemeldet() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_GET_ANGEMELDET);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        if (!Commands.CMD_GET_ANGEMELDET_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else {
            return (Benutzer) inputVomServer.readObject();
        }
    }

    @Override
    public void bestaetigen(String passwort, int nummer) throws NutzerExisistiertNichtException, IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_BESTAETIGEN);
        outputZumServer.writeObject(passwort);
        outputZumServer.writeInt(nummer);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            String fehlerMeldung = (String) inputVomServer.readObject();
            throw new NutzerExisistiertNichtException(fehlerMeldung);
        }
    }

    @Override
    public void beenden() throws IOException {
        outputZumServer.writeObject(Commands.CMD_BEENDEN);
        outputZumServer.reset();
    }

    @Override
    public boolean istKunde() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_IST_KUNDE);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);
        if (!Commands.CMD_IST_KUNDE_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else {
            return inputVomServer.readBoolean();
        }
    }

    @Override
    public Kunde kundeRegistrieren(String passwort, String name, Adresse adresse) throws NutzerExisitiertBereitsException, IOException, ClassNotFoundException, LeereEingabeException {
        outputZumServer.writeObject(Commands.CMD_KUNDE_REGISTRIEREN);
        outputZumServer.writeObject(passwort);
        outputZumServer.writeObject(name);
        outputZumServer.writeObject(adresse);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_KUNDE_REGISTRIEREN_RESP.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_LEERE_EINGABE_EXCECPTION -> throw new LeereEingabeException(fehlerMeldung);
                case Commands.CMD_NUTZER_EXISTIERT_BEREITS_EXCEPTION -> throw new NutzerExisitiertBereitsException(fehlerMeldung);
                case null, default -> throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
            }
        } else {
            return (Kunde) inputVomServer.readObject();
        }
    }

    @Override
    public Adresse erstellenAdresse(String strasse, int hausnummer, int plz, String ort) throws IOException, ClassNotFoundException, LeereEingabeException {
        outputZumServer.writeObject(Commands.CMD_ERSTELLEN_ADRESSE);
        outputZumServer.writeObject(strasse);
        outputZumServer.writeInt(hausnummer);
        outputZumServer.writeInt(plz);
        outputZumServer.writeObject(ort);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_ERSTELLEN_ADRESSE_RESP.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_LEERE_EINGABE_EXCECPTION -> throw new LeereEingabeException(fehlerMeldung);
                case null, default -> throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
            }
        } else {
            return (Adresse) inputVomServer.readObject();
        }
    }

    @Override
    public Mitarbeiter mitarbeiterRegistrieren(String name, String passwort) throws NutzerExisitiertBereitsException, IOException, ClassNotFoundException, LeereEingabeException {
        outputZumServer.writeObject(Commands.CMD_MITARBEITER_REGISTRIEREN);
        outputZumServer.writeObject(name);
        outputZumServer.writeObject(passwort);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_MITARBEITER_REGISTRIEREN_RESP.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_LEERE_EINGABE_EXCECPTION -> throw new LeereEingabeException(fehlerMeldung);
                case Commands.CMD_NUTZER_EXISTIERT_BEREITS_EXCEPTION -> throw new NutzerExisitiertBereitsException(fehlerMeldung);
                case null, default -> throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
            }
        } else {
            return (Mitarbeiter) inputVomServer.readObject();
        }
    }
    @Override
    public List<Artikel> getArtikelListe() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_GET_ARTIKELLISTE);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);
        if (!Commands.CMD_GET_ARTIKELLISTE_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else {
            return (List<Artikel>) inputVomServer.readObject();
        }
    }

    @Override
    public List<Artikel> sortiertNachBezeichnung() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_SORTIERT_NACH_BEZEICHNUNG);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);
        if (!Commands.CMD_SORTIERT_NACH_BEZEICHNUNG_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else {
            return (List<Artikel>) inputVomServer.readObject();
        }
    }

    @Override
    public List<Artikel> sortiertNachArtikelnummer() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_SORTIERT_NACH_ARTIKELNUMMER);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);
        if (!Commands.CMD_SORTIERT_NACH_ARTIKELNUMMER_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else {
            return (List<Artikel>) inputVomServer.readObject();
        }
    }

    @Override
    public void anlegenArtikel(Benutzer benutzer, int artikelnummer, String bezeichnung, int bestand, double preis) throws ArtikelExistiertBereitsException, NegativeEingabeException, IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_ANLEGEN_ARTIKEL);
        outputZumServer.writeObject(benutzer);
        outputZumServer.writeInt(artikelnummer);
        outputZumServer.writeObject(bezeichnung);
        outputZumServer.writeInt(bestand);
        outputZumServer.writeDouble(preis);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_ARTIKEL_EXISITIERT_BEREITS_EXCEPTION -> throw new ArtikelExistiertBereitsException(fehlerMeldung);
                case Commands.CMD_NEGATIVE_EINGABE_EXCEPTION -> throw new NegativeEingabeException(fehlerMeldung);
            }
        }
    }

    @Override
    public void anlegenMassenArtikel(Benutzer benutzer, int artikelnummer, String bezeichnung, int bestand, double preis, int packungsGroesse) throws ArtikelExistiertBereitsException, NegativeEingabeException, FalschePackungsgroesseException, IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_ANLEGEN_MASSEN_ARTIKEL);
        outputZumServer.writeObject(benutzer);
        outputZumServer.writeInt(artikelnummer);
        outputZumServer.writeObject(bezeichnung);
        outputZumServer.writeInt(bestand);
        outputZumServer.writeDouble(preis);
        outputZumServer.writeInt(packungsGroesse);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_ARTIKEL_EXISITIERT_BEREITS_EXCEPTION -> throw new ArtikelExistiertBereitsException(fehlerMeldung);
                case Commands.CMD_NEGATIVE_EINGABE_EXCEPTION -> throw new NegativeEingabeException(fehlerMeldung);
                case Commands.CMD_FALSCHE_PACKUNGGROESSE_EXCEPTION -> throw new FalschePackungsgroesseException(fehlerMeldung);
            }
        }
    }
    @Override
    public void erhoehenArtikelBestand(Benutzer benutzer, int artikelnummer, int bestand) throws NegativeEingabeException, ArtikelExistiertNichtException, UngueltigeBestandsException, IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_ERHOEHEN_ARTIKEL_BESTAND);
        outputZumServer.writeObject(benutzer);
        outputZumServer.writeInt(artikelnummer);
        outputZumServer.writeInt(bestand);
        outputZumServer.reset();
        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_ARTIKEL_EXISITIERT_NICHT_EXCEPTION -> throw new ArtikelExistiertNichtException(fehlerMeldung);
                case Commands.CMD_NEGATIVE_EINGABE_EXCEPTION -> throw new NegativeEingabeException(fehlerMeldung);
                case Commands.CMD_UNGUELTIGE_BESTANDS_EXCEPTION -> throw new UngueltigeBestandsException(fehlerMeldung);
            }
        }
    }
    @Override
    public void erzeugenEreignis(Benutzer benutzer, String aktion, int artikelnummer) throws IOException {
        outputZumServer.writeObject(Commands.CMD_ERZEUGEN_EREIGNIS);
        outputZumServer.writeObject(benutzer);
        outputZumServer.writeObject(aktion);
        outputZumServer.writeInt(artikelnummer);
        outputZumServer.reset();
    }
    @Override
    public List<Ereignis> getEreignisListe() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_GET_EREIGNISLISTE);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);
        if (!Commands.CMD_GET_EREIGNISLISTE_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else  {
        return (List <Ereignis>) inputVomServer.readObject();
        }
    }


    @Override
    public List<Ereignis> filterEreignisliste(int artikelnummer) throws ArtikelExistiertNichtException, IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_FILTER_EREIGNISLISTE);
        outputZumServer.writeInt(artikelnummer);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_FILTER_EREIGNISLISTE_RESP.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_ARTIKEL_EXISITIERT_NICHT_EXCEPTION -> throw new ArtikelExistiertNichtException(fehlerMeldung);
                case null, default -> throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
            }
        } else {
            return (List <Ereignis>) inputVomServer.readObject();
        }
    }
    @Override
    public Map<Artikel, Integer> getWarenkorbInhalt() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_GET_WARENKORB_INHALT);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);
        if (!Commands.CMD_GET_WARENKORB_INHALT_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else {
            return (Map<Artikel, Integer>) inputVomServer.readObject();
        }
    }

    @Override
    public void artikelHinzufuegenWarenkorb(int artikelnummer, int stueckzahl) throws ArtikelExistiertNichtException, GroessereStueckzahlException, NegativeEingabeException, ArtikelBereitsImWbException, ArtikelNichtImWbException, UngueltigeStueckzahlException, IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_ARTIKEL_HINZUFUEGEN_WARENKORB);
        outputZumServer.writeInt(artikelnummer);
        outputZumServer.writeInt(stueckzahl);
        outputZumServer.reset();
        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_ARTIKEL_EXISITIERT_NICHT_EXCEPTION -> throw new ArtikelExistiertNichtException(fehlerMeldung);
                case Commands.CMD_NEGATIVE_EINGABE_EXCEPTION -> throw new NegativeEingabeException(fehlerMeldung);
                case Commands.CMD_ARTIKEL_NICHT_IM_WB_EXCEPTION -> throw new ArtikelNichtImWbException(fehlerMeldung);
                case Commands.CMD_ARTIKEL_BEREITS_IM_WB_EXCEPTION -> throw new ArtikelBereitsImWbException(fehlerMeldung);
                case Commands.CMD_GROESSERE_STUECKZAHL_EXCEPTION -> throw new GroessereStueckzahlException(fehlerMeldung);
                case Commands.CMD_UNGUELTIGE_STUECKZAHL_EXCEPTION -> throw new UngueltigeStueckzahlException(fehlerMeldung);
            }
        }
    }

    @Override
    public void artikelEntfernenWarenkorb(int artikelnummer) throws ArtikelExistiertNichtException, ArtikelNichtImWbException, IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_ARTIKEL_ENTFERNEN);
        outputZumServer.writeInt(artikelnummer);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_ARTIKEL_EXISITIERT_NICHT_EXCEPTION -> throw new ArtikelExistiertNichtException(fehlerMeldung);
                case Commands.CMD_ARTIKEL_NICHT_IM_WB_EXCEPTION -> throw new ArtikelNichtImWbException(fehlerMeldung);
            }
        }
    }

    @Override
    public void artikelStueckzahlAendernWarenkorb(int artikelnummer, int neueStueckzahl) throws ArtikelNichtImWbException, GroessereStueckzahlException, UngueltigeStueckzahlException, ArtikelExistiertNichtException, IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_ARTIKEL_STUECKZAHL_AENDERN);
        outputZumServer.writeInt(artikelnummer);
        outputZumServer.writeInt(neueStueckzahl);
        outputZumServer.reset();
        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);

        String fehlerMeldung;
        if (!Commands.CMD_ERFOLGREICHE_AUSFUEHRUNG.equals(erhalteneDaten)) {
            fehlerMeldung = (String) inputVomServer.readObject();
            switch (erhalteneDaten) {
                case Commands.CMD_ARTIKEL_EXISITIERT_NICHT_EXCEPTION -> throw new ArtikelExistiertNichtException(fehlerMeldung);
                case Commands.CMD_ARTIKEL_NICHT_IM_WB_EXCEPTION -> throw new ArtikelNichtImWbException(fehlerMeldung);
                case Commands.CMD_GROESSERE_STUECKZAHL_EXCEPTION -> throw new GroessereStueckzahlException(fehlerMeldung);
                case Commands.CMD_UNGUELTIGE_STUECKZAHL_EXCEPTION -> throw new UngueltigeStueckzahlException(fehlerMeldung);
            }
        }
    }

    @Override
    public void warenkorbLeeren() throws IOException {
        outputZumServer.writeObject(Commands.CMD_WARENKORB_LEEREN);
        outputZumServer.reset();
    }

    @Override
    public double getGesamtpreis() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_GET_GESAMTPREIS);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);
        if (!Commands.CMD_GET_GESAMTPREIS_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else {
            return inputVomServer.readDouble();
        }
    }

    @Override
    public Rechnung kaufAbschliessen() throws IOException, ClassNotFoundException {
        outputZumServer.writeObject(Commands.CMD_KAUF_ABSCHLIESSEN);
        outputZumServer.reset();

        Commands erhalteneDaten = (Commands) inputVomServer.readObject();
        System.err.println("Empfangene Daten vom Server: " + erhalteneDaten);
        if (!Commands.CMD_KAUF_ABSCHLIESSEN_RESP.equals(erhalteneDaten)) {
            throw new RuntimeException("Ungueltige Antwort auf Anfrage erhalten!");
        } else {
            return (Rechnung) inputVomServer.readObject();
        }
    }
}