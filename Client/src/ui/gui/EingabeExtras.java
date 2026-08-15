package ui.gui;

import javax.swing.*;

import common.exceptions.EingabeUnterbrechungException;

public class EingabeExtras {

    public static int intEingeben(String message) throws EingabeUnterbrechungException {
        Integer nummer = null;
        while (nummer == null) {
            try {
                String nummerEingabe = JOptionPane.showInputDialog(message);
                if (nummerEingabe == null) { // cancel-Option
                    throw new EingabeUnterbrechungException();
                }
                nummer = Integer.parseInt(nummerEingabe);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Versuchen Sie es erneut.");
            }
        }
        return nummer;
    }

    public static String stringEingeben(String message) throws EingabeUnterbrechungException {
        String stringEingabe = JOptionPane.showInputDialog(message);
        if (stringEingabe == null) {
            throw new EingabeUnterbrechungException();
        }
        return stringEingabe;
    }

    public static double doubleEingeben(String message) throws EingabeUnterbrechungException {
        Double doppel = null;
        while (doppel == null) {
            try {
                String doubleEingabe = JOptionPane.showInputDialog(message);
                if (doubleEingabe == null) {
                    throw new EingabeUnterbrechungException();
                }
                doppel = Double.parseDouble(doubleEingabe);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Versuchen Sie es erneut.");
            }
        }
        return doppel;
    }
}
