package ui.cui;

import java.io.IOException;
import java.util.*;

import common.*;
import common.exceptions.*;
import domain.EShop;

public class EShopCUI {

    private final EShop eShop;
    private final Scanner scan;

    public EShopCUI() {
        eShop = new EShop();
        scan = new Scanner(System.in);
    }

    private void hauptmenue()  {
        String eingabe;

        do {
            System.out.println("\nBitte auswählen!");
            System.out.println("Artikel ausgeben:                         'a'");
            System.out.println("Einloggen:                                'l'");
            System.out.println("Registrieren als Kunde:                   'r'");
            System.out.println("---------------------------------------------");
            System.out.println("Beenden:                                  'f'");
            System.out.print("> ");
            eingabe = scan.nextLine();

            switch (eingabe) {
                case "a":
                    ausgebenArtikel();
                    break;
                case "l":
                    einloggen();
                    break;
                case "r":
                    registierenKunde();
                    break;
                case "f":
                    speichernDaten();
                    System.exit(0);
                    break;
                default:
                    System.err.println("Ungültige Eingabe: " + eingabe);
            }
        } while (!eingabe.equals("f"));
    }

    private void ladenDaten() {
        try {
            eShop.ladenDaten();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Fehler beim laden der Daten");
        }
    }

    private void speichernDaten() {
        try {
            eShop.speichernDaten();
        } catch (IOException e) {
            System.err.println("Fehler beim speichern der Daten");
        }
    }

    private void ausgebenArtikel() {
        String eingabe;

        System.out.println("Nach Bezeichnung sortiert ausgeben        'b'");
        System.out.println("Nach Artikelnummer sortiert ausgeben      'n'");
        System.out.println("---------------------------------------------");
        System.out.print("> ");
        eingabe = scan.nextLine();

        switch (eingabe) {
            case "b":
                List<Artikel> artikelListeBezeichnung = eShop.sortiertNachBezeichnung();
                listeAusprinten(artikelListeBezeichnung);
                break;
            case "n":
                List<Artikel> artikelListeArtikelnummer = eShop.sortiertNachArtikelnummer();
                listeAusprinten(artikelListeArtikelnummer);
                break;
            default:
                System.err.println("Ungültige Eingabe: " + eingabe);
        }
    }

    public void listeAusprinten(List<Artikel> artikelListe) {
        System.out.println("Vorhandene Artikel: ");
        for(Artikel a : artikelListe) {
            System.out.println(a);
        }
    }

    private void einloggen() {
        try {
            int nummer;
            String passwort;

            System.out.println("Benutzernummer eingeben: ");
            nummer = Integer.parseInt(scan.nextLine());
            System.out.println("Passwort eingeben: ");
            passwort = scan.nextLine();
            eShop.bestaetigen(passwort, nummer);

            if (eShop.istKunde()) {
                gibMenueKundeAus();
            } else {
                gibMenuMitarbeiterAus();
            }
        } catch (NumberFormatException e) {
            System.err.println("Falsche Eingabe. Bitte versuchen Sie es erneut.");
        } catch (NutzerExisistiertNichtException e) {
            System.err.println(e.getMessage());
        }
    }

    private void registierenKunde() {
        try {
            String name;
            String passwort;
            int benutzernummer;
            String strasse;
            int hausnummer;
            int plz;
            String ort;

            System.out.println("Benutzername eingeben: ");
            name = scan.nextLine();
            System.out.println("Passwort eingeben: ");
            passwort = scan.nextLine();
            System.out.println("Straße eingeben: ");
            strasse = scan.nextLine();
            System.out.println("Hausnummer eingeben: ");
            hausnummer = Integer.parseInt(scan.nextLine());
            System.out.println("Postleitzahl eingeben: ");
            plz = Integer.parseInt(scan.nextLine());
            System.out.println("Ort eingeben: ");
            ort = scan.nextLine();

            Adresse adresse = eShop.erstellenAdresse(strasse, hausnummer, plz, ort);

            Kunde k = eShop.kundeRegistrieren(passwort, name, adresse);
            benutzernummer = k.getNummer();
            System.out.println("Ihre Benutzernummer ist " + benutzernummer);
            eShop.bestaetigen(passwort, benutzernummer);
        } catch (NumberFormatException e) {
            System.err.println("Falsche Eingabe. Bitte schreiben Sie sorgfältiger.");
        } catch (NutzerExisitiertBereitsException | NutzerExisistiertNichtException | NullPointerException | LeereEingabeException e) {
            System.err.println(e.getMessage());
        }
    }

    private void gibMenuMitarbeiterAus() {
        String eingabe;

        do {
            System.out.println("\nMitarbeitermenü: Bitte auswählen!");
            System.out.println("Artikel ausgeben:                         'a'");
            System.out.println("Neue Artikel anlegen                      'c'");
            System.out.println("Bestand exisitierender Artikel erhöhen    'h'");
            System.out.println("Neue Mitarbeiter registieren              'n'");
            System.out.println("Ereignisliste ausgeben                    'l'");
            System.out.println("---------------------------------------------");
            System.out.println("Ausloggen und zum Hauptmenü               'z'");
            System.out.println("Beenden:                                  'f'");
            System.out.print("> ");
            eingabe = scan.nextLine();
            switch(eingabe) {
                case "a":
                    ausgebenArtikel();
                    break;
                case "c":
                    anlegenAlleArtikel();
                    break;
                case "h":
                    erhoehenBestand();
                    break;
                case "n":
                    registrierenMitarbeiter();
                    break;
                case "l":
                    gibEreignisliste();
                    break;
                case "z":
                    speichernDaten();
                    eShop.beenden();
                    hauptmenue();
                    break;
                case "f":
                    speichernDaten();
                    System.exit(0);
                    break;
                default:
                    System.err.println("Ungültige Eingabe: " + eingabe);
            }
        } while(!eingabe.equals("f"));
    }

    private void anlegenAlleArtikel() {
        String eingabe;

        System.out.println("Einzelartikel anlegen                     'e'");
        System.out.println("Massengutartikel anlegen                  'm'");
        System.out.println("---------------------------------------------");
        System.out.print("> ");
        eingabe = scan.nextLine();

        switch (eingabe) {
            case "e":
               anlegenArtikel();
                break;
            case "m":
                anlegenMassenArtikel();
                break;
            default:
                System.err.println("Ungültige Eingabe: " + eingabe);
        }
    }

    private void anlegenArtikel() {
        try {
            int artikelnummer;
            String bezeichnung;
            int bestand;
            double preis;

            System.out.println("Artikelnummer eingeben: ");
            artikelnummer = Integer.parseInt(scan.nextLine());
            System.out.println("Bezeichnung eingeben: ");
            bezeichnung = scan.nextLine();
            System.out.print("Bestand eingeben: ");
            bestand = Integer.parseInt(scan.nextLine());
            System.out.println("Preis eingeben: ");
            preis = Double.parseDouble(scan.nextLine());

            eShop.anlegenArtikel(eShop.getAngemeldet(), artikelnummer, bezeichnung, bestand, preis);
            System.out.println("Artikel wurde angelegt.");
        } catch (NumberFormatException e) {
            System.err.println("Bitte geben sie den richtigen Wert ein.");
        } catch (NegativeEingabeException e) {
            System.err.println(e.getMessage());
        } catch (ArtikelExistiertBereitsException e) {
            System.err.println("Fehler beim anlegen des Artikels:\n" + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Fehler beim speichern der Daten");
        }
    }

    private void anlegenMassenArtikel() {
        try {
            int artikelnummer;
            String bezeichnung;
            int bestand;
            double preis;
            int packungsGroesse;

            System.out.println("Artikelnummer eingeben: ");
            artikelnummer = Integer.parseInt(scan.nextLine());
            System.out.println("Bezeichnung eingeben: ");
            bezeichnung = scan.nextLine();
            System.out.print("Bestand eingeben: ");
            bestand = Integer.parseInt(scan.nextLine());
            System.out.println("Preis eingeben: ");
            preis = Double.parseDouble(scan.nextLine());
            System.out.println("Packungsgröße eingeben: ");
            packungsGroesse = Integer.parseInt(scan.nextLine());

            eShop.anlegenMassenArtikel(eShop.getAngemeldet(), artikelnummer, bezeichnung, bestand, preis, packungsGroesse);
            System.out.println("Artikel wurde angelegt.");
        } catch (NumberFormatException e) {
            System.out.println("Fehler bei der Eingabe der Zahlen. Bitte korrekte Werte eingeben.");
        } catch (ArtikelExistiertBereitsException | NegativeEingabeException | FalschePackungsgroesseException e) {
            System.err.println(e.getMessage());
        }
    }

    private void erhoehenBestand() {
        try {
            int artikelnummer;
            int bestand;

            System.out.println("Artikelnummer eingeben: ");
            artikelnummer = Integer.parseInt(scan.nextLine());
            System.out.println("Anzahl der Artikel eingeben: ");
            bestand = Integer.parseInt(scan.nextLine());

            eShop.erhoehenArtikelBestand(eShop.getAngemeldet(), artikelnummer, bestand);
            System.out.println("Die Anzahl des Artikels wurde erhöht.");
        } catch (NumberFormatException e) {
            System.err.println("Bitte geben Sie den richtigen Wert ein.");
        } catch (NegativeEingabeException | ArtikelExistiertNichtException | UngueltigeBestandsException e) {
            System.err.println(e.getMessage());
        }
    }

    private void registrierenMitarbeiter() {
        try {
            String name;
            String passwort;
            int mitarbeiternummer;

            System.out.println("Benutzername eingeben: ");
            name = scan.nextLine();
            System.out.println("Passwort eingeben: ");
            passwort = scan.nextLine();
            Mitarbeiter m = eShop.mitarbeiterRegistrieren(name, passwort);
            mitarbeiternummer = m.getNummer();
            System.out.println("Ihre Benutzernummer ist " + mitarbeiternummer);
            eShop.bestaetigen(passwort, mitarbeiternummer);

        } catch (NumberFormatException e) {
            System.err.println("Falsche Eingabe. Bitte schreiben Sie sorgfältiger.");
        } catch (NutzerExisitiertBereitsException | NutzerExisistiertNichtException | LeereEingabeException e) {
            System.err.println(e.getMessage());
        }
    }

    private void gibEreignisliste() {
        String eingabe;

        System.out.println("Alle Ereignisse anzeigen                  'a'");
        System.out.println("Nach Eignis filtern                       'f'");
        System.out.println("---------------------------------------------");
        System.out.print("> ");
        eingabe = scan.nextLine();

        switch (eingabe) {
            case "a":
                ganzeEreignisliste();
                break;
            case "f":
                filterEreignisliste();
                break;
            default:
                System.err.println("Ungültige Eingabe: " + eingabe);
        }
    }

    private void ganzeEreignisliste() {
        List<Ereignis> ereignisliste = eShop.getEreignisListe();
        System.out.println("Ereignisse: ");
        for(Ereignis e : ereignisliste) {
            System.out.println(e);
        }
    }

    private void filterEreignisliste(){
        try {
            int eingabe;
            System.out.println("Bitte Artikelnummer eingeben");
            eingabe = Integer.parseInt(scan.nextLine());

            List<Ereignis> ereignisliste = eShop.filterEreignisliste(eingabe);
            System.out.println("Ereignisse von Artikel " + eingabe + ":");
            for(Ereignis e : ereignisliste) {
                System.out.println(e);
            }
        } catch (NumberFormatException e) {
            System.err.println("Bitte geben sie den richtigen Wert ein.");
        } catch (ArtikelExistiertNichtException e) {
            System.err.println(e.getMessage());

        }
    }

    private void gibMenueKundeAus() {
        String eingabe;

        do {
            System.out.println("\nKundenmenü: Bitte auswählen!");
            System.out.println("Artikel ausgeben:                         'a'");
            System.out.println("Warenkorb anzeigen:                       'b'");
            System.out.println("Artikel zum Warenkorb hinzufügen:         'h'");
            System.out.println("Artikel aus Warenkorb entfernen:          'e'");
            System.out.println("Stückzahl des Artikels ändern:            's'");
            System.out.println("Warenkorb leeren:                         'c'");
            System.out.println("Kauf abschließen und Rechnung erstellen:  'k'");
            System.out.println("---------------------------------------------");
            System.out.println("Ausloggen und zum Hauptmenü               'z'");
            System.out.println("Beenden:                                  'f'");
            System.out.print("> ");
            eingabe = scan.nextLine();

            switch (eingabe) {
                case "a":
                    ausgebenArtikel();
                    break;
                case "b":
                    anzeigenWarenkorb();
                    break;
                case "h":
                    hinzufuegenArtikelWarenkorb();
                    break;
                case "e":
                    entfernenArtikelWarenkorb();
                    break;
                case "s":
                    aendernArtikelWarenkorb();
                    break;
                case "c":
                    leerenWarenkorb();
                    break;
                case "k":
                    abschliessenKauf();
                    break;
                case "z":
                    speichernDaten();
                    eShop.beenden();
                    hauptmenue();
                    break;
                case "f":
                    speichernDaten();
                    System.exit(0);
                    break;
                default:
                    System.err.println("Ungültige Eingabe: " + eingabe);
            }
        } while(!eingabe.equals("f"));
    }

    private void anzeigenWarenkorb() {
        System.out.println("Warenkorb:");
        Map<Artikel, Integer> warenkorbinhalt = eShop.getWarenkorbInhalt();
        warenkorbinhalt.forEach((artikel, anzahl) -> System.out.println("Artikel: " + artikel + ", Anzahl: " + anzahl));
        System.out.println("Gesamtpreis: " + eShop.getGesamtpreis() + " Euro");
    }

    private void hinzufuegenArtikelWarenkorb() {
        try {
            int numH;
            int qtyH;

            System.out.println("Artikelnummer für das hinzufügen eingeben:");
            numH = Integer.parseInt(scan.nextLine());
            System.out.println("Stückzahl eingeben:");
            qtyH = Integer.parseInt(scan.nextLine());

            eShop.artikelHinzufuegenWarenkorb(numH, qtyH);
            System.out.println("Artikel wurde Hinzugefügt.");
        } catch (NumberFormatException e) {
            System.err.println("Ungültige Eingabe. Bitte geben sie eine gültige Zahl ein.");
        } catch (ArtikelExistiertNichtException | GroessereStueckzahlException |
                 UngueltigeStueckzahlException |
                 NegativeEingabeException | ArtikelNichtImWbException |
                 ArtikelBereitsImWbException e) {
            System.err.println(e.getMessage());
        }
    }

    private void entfernenArtikelWarenkorb() {
        try {
            int artikelnummer;

            System.out.println("Artikelnummer des zu entfernenden Artikels eingeben:");
            artikelnummer = Integer.parseInt(scan.nextLine());

            eShop.artikelEntfernenWarenkorb(artikelnummer);
            System.out.println("Artikel entfernt.");
        } catch (NumberFormatException e) {
            System.err.println("Bitte geben Sie eine gültige Zahl ein.");
        } catch (ArtikelExistiertNichtException | ArtikelNichtImWbException e) {
            System.err.println(e.getMessage());
        }
    }

    private void aendernArtikelWarenkorb() {
        try {
            int numS;
            int neueStueckzahl;

            System.out.println("Artikelnummer für Stückzahländerung eingeben:");
            numS = Integer.parseInt(scan.nextLine());
            System.out.println("Neue Stückzahl eingeben:");
            neueStueckzahl = Integer.parseInt(scan.nextLine());

            eShop.artikelStueckzahlAendernWarenkorb(numS, neueStueckzahl);
            System.out.println("Stückzahl geändert.");
        } catch (NumberFormatException e) {
            System.err.println("Bitte geben Sie eine gültige Zahl ein.");
        } catch (ArtikelNichtImWbException | GroessereStueckzahlException | UngueltigeStueckzahlException | ArtikelExistiertNichtException e) {
            System.err.println(e.getMessage());
        }
    }

    private void leerenWarenkorb() {
        eShop.warenkorbLeeren();
        System.out.println("Warenkorb wurde geleert.");
    }

    private void abschliessenKauf() {
        if (eShop.getWarenkorbInhalt().isEmpty()) {
            System.out.println("Der Warenkorb ist leer. Bitte fügen Sie Artikel hinzu, " +
                    "bevor Sie den Kauf abschließsen.");
        } else {
            Kunde kunde = (Kunde) eShop.getAngemeldet();
            Rechnung rechnung = eShop.kaufAbschliessen();
            System.out.println("Kauf abgeschlossen und Rechnung erstellt.");
            System.out.println("Rechnung: ");
            System.out.println("Kunde: " + kunde.getName());
            System.out.println("Benutzernummer: " + kunde.getNummer());
            System.out.println("Adresse: " + kunde.getAdresse());
            System.out.println("Rechnungsdatum: " + rechnung.getKaufDatum());
            System.out.println("Gesamtpreis: " + rechnung.getGesamtpreis() + " Euro");
            System.out.println("Gekaufte Artikel:");
            for (Map.Entry<Artikel, Integer> eintrag : rechnung.getGekaufteArtikel().entrySet()) {
                Artikel artikel = eintrag.getKey();
                Integer menge = eintrag.getValue();
                System.out.println("- " + artikel.getBezeichnung() + " x " + menge + " zu je " + artikel.getPreis() + "Euro");
            }
        }
    }

    public static void main(String[] args) {
        EShopCUI cui = new EShopCUI();
        cui.ladenDaten();
        cui.hauptmenue();
    }
}