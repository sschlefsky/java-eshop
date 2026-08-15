package ui.gui;

import javax.swing.table.AbstractTableModel;
import java.util.List;

import common.Artikel;
import common.Massengutartikel;

public class ArtikelTableModel extends AbstractTableModel {

    private final List<Artikel> artikelListe;
    private final String[] spaltenNamen;

    public ArtikelTableModel(List<Artikel> artikelListe) {
        this.artikelListe = artikelListe;
        spaltenNamen = new String[]{"Artikelnummer", "Bezeichnung", "Bestand", "Preis", "Packungsgröße"};
    }

    @Override
    public int getRowCount() {
        return artikelListe.size();
    }

    @Override
    public int getColumnCount() {
        return spaltenNamen.length;
    }

    @Override
    public String getColumnName(int column) {
        return spaltenNamen[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Artikel gewaehlterArtikel = artikelListe.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return gewaehlterArtikel.getArtikelnummer();
            case 1:
                return gewaehlterArtikel.getBezeichnung();
            case 2:
                return gewaehlterArtikel.getBestand();
            case 3:
                return gewaehlterArtikel.getPreis();
            case 4:
                if (gewaehlterArtikel instanceof Massengutartikel) {
                    return ((Massengutartikel) gewaehlterArtikel).getPackungsGroesse();
                } else {
                    return "/";
                }
            default:
                return null;
        }
    }
}

