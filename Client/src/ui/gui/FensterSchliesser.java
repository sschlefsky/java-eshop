package ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import common.IEShop;
import common.exceptions.IOPersistenceException;

public class FensterSchliesser extends WindowAdapter {

    private final IEShop eShop;

    public FensterSchliesser(IEShop eShop) {
        this.eShop = eShop;
    }

    @Override
    public void windowClosing(WindowEvent we) {
        Window window = we.getWindow();
        int result = JOptionPane.showConfirmDialog(window, "Wollen Sie das Programm wirklich beenden?", "Warnung!", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                eShop.speichernDaten();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
            } catch (IOPersistenceException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            window.setVisible(false);
            window.dispose();
            System.exit(0);
        }
    }
}