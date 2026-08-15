package ui.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import common.Mitarbeiter;
import common.IEShop;
import common.exceptions.*;

import static ui.gui.EingabeExtras.stringEingeben;

public class MitarbeiterMenue extends JMenu implements ActionListener {
    private final IEShop eShop;

    public MitarbeiterMenue(IEShop e) {
        super("Mitarbeiter");
        this.eShop = e;

        JMenuItem item = new JMenuItem("Mitarbeiter registrieren");
        item.addActionListener(this);
        add(item);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Mitarbeiter registrieren")) {
            try {
                String name = stringEingeben("Benutzername eingeben");
                String passwort = stringEingeben("Passwort eingeben");

                Mitarbeiter m = eShop.mitarbeiterRegistrieren(name, passwort);
                int mitarbeiternummer = m.getNummer();

                JOptionPane.showMessageDialog(null, "Registrierung erfolgreich. Ihre Benutzernummer ist " + mitarbeiternummer);

            } catch (NutzerExisitiertBereitsException | LeereEingabeException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            } catch (EingabeUnterbrechungException ignored) {
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
            }
        } else {
            throw new IllegalArgumentException("Unbekanntes MenuItem!");
        }
    }
}




