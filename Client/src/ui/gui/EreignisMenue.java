package ui.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import common.Ereignis;
import common.IEShop;
import common.exceptions.ArtikelExistiertNichtException;
import common.exceptions.EingabeUnterbrechungException;

import static ui.gui.EingabeExtras.intEingeben;

public class EreignisMenue extends JMenu implements ActionListener {

    private final IEShop eShop;
    private final EshopGUI gui;

    public EreignisMenue(IEShop eShop, EshopGUI gui) {

        super("Ereignisse");
        this.eShop = eShop;
        this.gui = gui;

        JMenuItem item = new JMenuItem("Nach Ereignis filtern");
        item.addActionListener(this);
        add(item);

        item = new JMenuItem("Filter aufheben");
        item.addActionListener(this);
        add(item);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Nach Ereignis filtern":
                try {
                    int artikelnummer = intEingeben("Artikelnummer eingeben");

                    List<Ereignis> ereignisliste = eShop.filterEreignisliste(artikelnummer);

                    gui.panalEast.removeAll();
                    gui.layoutfuegeEreignisListeHinzu(ereignisliste);
                    gui.revalidate();
                    gui.repaint();

                } catch (ArtikelExistiertNichtException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                } catch (EingabeUnterbrechungException ignored) {
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");}
                break;
            case "Filter aufheben":
                try {
                    gui.panalEast.removeAll();
                    gui.layoutfuegeEreignisListeHinzu(eShop.getEreignisListe());
                    gui.revalidate();
                    gui.repaint();

                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");}
                break;
            default:
                throw new IllegalArgumentException("Unbekanntes MenuItem!");
        }
    }
}