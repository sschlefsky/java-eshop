package ui.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import common.IEShop;
import common.exceptions.*;

import static ui.gui.EingabeExtras.*;

public class ArtikelMenue extends JMenu implements ActionListener {

    private final IEShop eShop;
    private final EshopGUI gui;

    public ArtikelMenue(IEShop eShop, EshopGUI gui) {
        super("Artikel");
        this.eShop = eShop;
        this.gui = gui;

        JMenuItem item = new JMenuItem("Einzelartikel anlegen");
        item.addActionListener(this);
        add(item);

        item = new JMenuItem("Massengutartikel anlegen");
        item.addActionListener(this);
        add(item);

        item = new JMenuItem("Bestand erhöhen");
        item.addActionListener(this);
        add(item);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Einzelartikel anlegen":
                try {
                    int artikelnummer = intEingeben("Artikelnummer eingeben");
                    String bezeichnung = stringEingeben("Bezeichnung eingeben:");
                    int bestand = intEingeben("Bestand eingeben: ");
                    double preis = doubleEingeben("Preis eingeben");

                    eShop.anlegenArtikel(eShop.getAngemeldet(), artikelnummer, bezeichnung, bestand, preis);

                    JOptionPane.showMessageDialog(null, "Artikel wurde angelegt");
                    gui.panalCenter.removeAll();
                    gui.panalEast.removeAll();
                    gui.layoutFuegeArtikelListeHinzu(eShop.getArtikelListe());
                    gui.layoutfuegeEreignisListeHinzu(eShop.getEreignisListe());
                    gui.revalidate();
                    gui.repaint();

                } catch (EingabeUnterbrechungException ignored) {
                } catch (ArtikelExistiertBereitsException | NegativeEingabeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
                }
                break;
            case "Massengutartikel anlegen":
                try {
                    int artikelnummer = intEingeben("Artikelnummer eingeben");
                    String bezeichnung = stringEingeben("Bezeichnung eingeben");
                    int bestand = intEingeben("Bestand eingeben");
                    double preis = doubleEingeben("Preis eingeben");
                    int packungsGroesse = intEingeben("Packungsgröße eingeben");

                    eShop.anlegenMassenArtikel(eShop.getAngemeldet(), artikelnummer, bezeichnung, bestand, preis, packungsGroesse);

                    JOptionPane.showMessageDialog(null, "Artikel wurde angelegt");
                    gui.panalCenter.removeAll();
                    gui.panalEast.removeAll();
                    gui.layoutFuegeArtikelListeHinzu(eShop.getArtikelListe());
                    gui.layoutfuegeEreignisListeHinzu(eShop.getEreignisListe());
                    gui.revalidate();
                    gui.repaint();

                } catch (EingabeUnterbrechungException ignored) {
                } catch (ArtikelExistiertBereitsException | NegativeEingabeException | FalschePackungsgroesseException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");                }
                break;
            case "Bestand erhöhen" :
                try {
                    int artikelnummer = intEingeben("Artikelnummer eingeben");
                    int bestand = intEingeben("Anzahl der Artikel eingeben");

                    eShop.erhoehenArtikelBestand(eShop.getAngemeldet(), artikelnummer, bestand);

                    JOptionPane.showMessageDialog(null, "Die Anzahl des Artikels wurde erhöht");
                    gui.panalCenter.removeAll();
                    gui.panalEast.removeAll();
                    gui.layoutFuegeArtikelListeHinzu(eShop.getArtikelListe());
                    gui.layoutfuegeEreignisListeHinzu(eShop.getEreignisListe());
                    gui.revalidate();
                    gui.repaint();

                } catch (ArtikelExistiertNichtException | NegativeEingabeException | UngueltigeBestandsException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                } catch (EingabeUnterbrechungException ignored) {}
                catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");}
                break;
            default:
                throw new IllegalArgumentException("Unbekanntes MenuItem!");
        }
    }
}