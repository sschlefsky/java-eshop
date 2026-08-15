package ui.gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.Artikel;
import common.Massengutartikel;
import common.Warenkorb;

public class WarenkorbTableModel extends AbstractTableModel {

    private final List<Map.Entry<Artikel, Integer>> artikel;
    private final String[] spaltenNamen;

    public WarenkorbTableModel(Warenkorb warenkorb) {
        this.artikel = new ArrayList<>(warenkorb.getWarenkorbArtikel().entrySet());
        this.spaltenNamen = new String[]{"Artikelnummer", "Bezeichnung", "Stückzahl", "Preis", "Packungsgröße"};
    }

    @Override
    public int getRowCount() {
        return artikel.size();
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
        Map.Entry<Artikel, Integer> entry = artikel.get(rowIndex);
        Artikel artikel = entry.getKey();
        Integer stueckzahl = entry.getValue();
        switch (columnIndex) {
            case 0:
                return artikel.getArtikelnummer();
            case 1:
                return artikel.getBezeichnung();
            case 2:
                return stueckzahl;
            case 3:
                return artikel.getPreis();
            case 4:
                if (artikel instanceof Massengutartikel) {
                    return ((Massengutartikel) artikel).getPackungsGroesse();
                } else {
                    return "/";
                }
            default:
                return null;
        }
    }
}

