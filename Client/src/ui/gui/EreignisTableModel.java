package ui.gui;
import javax.swing.table.AbstractTableModel;
import java.util.List;

import common.Ereignis;

public class EreignisTableModel extends AbstractTableModel {

    private final List<Ereignis> ereignisListe;
    private final String[] spaltenNamen;

    public EreignisTableModel(List<Ereignis> ereignisListe) {
        this.ereignisListe = ereignisListe;
        this.spaltenNamen = new String[]{"Ereignisse", "Datum"};
    }

    @Override
    public int getRowCount() {
        return ereignisListe.size();
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
        Ereignis gewaehltesEreignis =ereignisListe.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return gewaehltesEreignis.toString();
            case 1:
                return gewaehltesEreignis.getDatum();
            default:
                return null;
        }
    }
}
