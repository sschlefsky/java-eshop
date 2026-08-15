package ui.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

import common.Artikel;
import common.Kunde;
import common.Rechnung;
import common.IEShop;
import common.exceptions.*;

public class WarenkorbMenue extends JMenu implements ActionListener {

    private final EshopGUI gui;
    private IEShop e;

    public WarenkorbMenue(IEShop e, EshopGUI gui) {
        super("Warenkorb");
        this.e = e;
        this.gui = gui;

        JMenuItem item = new JMenuItem("Artikel hinzufügen");
        item.addActionListener(this);
        add(item);

        item = new JMenuItem("Artikel entfernen");
        item.addActionListener(this);
        add(item);

        item = new JMenuItem("Stückzahl Artikel ändern");
        item.addActionListener(this);
        add(item);

        item = new JMenuItem("Warenkorb leeren");
        item.addActionListener(this);
        add(item);

        item = new JMenuItem("Kauf abschließen und Rechnung erstellen");
        item.addActionListener(this);
        add(item);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Artikel hinzufügen":
                try {
                    int numH = EingabeExtras.intEingeben("Artikelnummer für das hinzufügen eingeben:");
                    int qtyH = EingabeExtras.intEingeben("Stückzahl eingeben:");

                    this.e.artikelHinzufuegenWarenkorb(numH, qtyH);
                    JOptionPane.showMessageDialog(null, "Artikel wurde hinzugefügt.");
                    gui.panalEast.removeAll();
                    gui.layoutFuegeWarenkorbTabelleHinzu();
                    gui.revalidate();
                    gui.repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ungültige Eingabe. Bitte geben sie eine gültige Zahl ein.");
                } catch (ArtikelExistiertNichtException | GroessereStueckzahlException |
                         UngueltigeStueckzahlException |
                         NegativeEingabeException | ArtikelNichtImWbException | ArtikelBereitsImWbException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                } catch (EingabeUnterbrechungException ignored) {
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
                }
                break;
            case "Artikel entfernen":
                try {
                    int artikelnummer = EingabeExtras.intEingeben("Artikelnummer des zu entfernenden Artikels eingeben:");

                    this.e.artikelEntfernenWarenkorb(artikelnummer);
                    JOptionPane.showMessageDialog(null, "Artikel entfernt.");
                    gui.panalEast.removeAll();
                    gui.layoutFuegeWarenkorbTabelleHinzu();
                    gui.revalidate();
                    gui.repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige Zahl ein.");
                } catch (ArtikelExistiertNichtException | ArtikelNichtImWbException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                } catch (EingabeUnterbrechungException ignored) {
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
                }
                break;
            case "Stückzahl Artikel ändern":
                try {
                    int numS = EingabeExtras.intEingeben("Artikelnummer für Stückzahländerung eingeben:");
                    int neueStueckzahl = EingabeExtras.intEingeben("Neue Stückzahl eingeben:");

                    this.e.artikelStueckzahlAendernWarenkorb(numS, neueStueckzahl);
                    JOptionPane.showMessageDialog(null, "Stückzahl geändert.");
                    gui.panalEast.removeAll();
                    gui.layoutFuegeWarenkorbTabelleHinzu();
                    gui.revalidate();
                    gui.repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige Zahl ein.");
                } catch (ArtikelNichtImWbException | GroessereStueckzahlException | UngueltigeStueckzahlException |
                         ArtikelExistiertNichtException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                } catch (EingabeUnterbrechungException ignored) {
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
                }
                break;
            case "Warenkorb leeren":
                try {
                    this.e.warenkorbLeeren();
                    JOptionPane.showMessageDialog(null, "Warenkorb wurde geleert.");
                    gui.panalEast.removeAll();
                    gui.layoutFuegeWarenkorbTabelleHinzu();
                    gui.revalidate();
                    gui.repaint();

                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
                }
                break;
            case "Kauf abschließen und Rechnung erstellen":
                try {
                    if (this.e.getWarenkorbInhalt().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Der Warenkorb ist leer. Bitte fügen Sie Artikel hinzu, " + "bevor Sie den Kauf abschließsen.");
                    } else {
                        Rechnung rechnung = this.e.kaufAbschliessen();
                        Kunde kunde = (Kunde) this.e.getAngemeldet();
                        StringBuilder rechnungMessage = new StringBuilder("Rechnung für: " + kunde.getName() + " \nBenutzernummer: " + kunde.getNummer() + "\nAdresse: " + kunde.getAdresse() + " \nRechnungsdatum: " + rechnung.getKaufDatum() + " \nGesamtpreis: " + rechnung.getGesamtpreis() + " Euro " + "" +
                                "\nGekaufte Artikel: ");

                        for (Map.Entry<Artikel, Integer> eintrag : rechnung.getGekaufteArtikel().entrySet()) {
                            Artikel artikel = eintrag.getKey();
                            Integer menge = eintrag.getValue();
                            rechnungMessage.append("\n"+ artikel.getBezeichnung()).append(" x ").append(menge).append(" zu je ").append(artikel.getPreis()).append(" Euro");
                        }

                        JOptionPane.showMessageDialog(null, rechnungMessage.toString());

                        gui.panalCenter.removeAll();
                        gui.panalEast.removeAll();
                        gui.layoutFuegeArtikelListeHinzu(this.e.getArtikelListe());
                        gui.layoutFuegeWarenkorbTabelleHinzu();
                        gui.revalidate();
                        gui.repaint();
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unbekanntes MenuItem!");
        }
    }
}