package ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import common.*;
import common.exceptions.*;
import net.EShopClient;
import static ui.gui.EingabeExtras.*;

public class EshopGUI extends JFrame {

    private final IEShop eShop;
    private final JPanel panalSouth = new JPanel();
    protected final JPanel panalCenter = new JPanel();
    private final JPanel panalWest = new JPanel();
    private final JPanel panalNorth = new JPanel();
    protected final JPanel panalEast = new JPanel();

    private final JButton buttonEinloggen = new JButton("Einloggen");
    private final JButton buttonRegistrieren = new JButton("Registrieren");
    private final JButton buttonAusloggen = new JButton("Ausloggen");
    private final JButton nachArtikelnummerButton = new JButton("nach Artikelnummer");
    private final JButton nachBezeichnungButton = new JButton("nach Bezeichnung");

    private JScrollPane scrollPane;
    private WarenkorbTableModel warenkorbTableModel;
    private JTable warenkorbTabelle;
    private JTable artikelTabelle;
    private ArtikelTableModel artikelTableModel;
    private EreignisTableModel ereignisTableModel;
    private JTable ereignisTabelle;

    public EshopGUI() throws IOException {
        super("Eshop");

        // Frühere Standalone-Version:
        // eShop = new EShop();

        // Finale Client/Server-Version:
        eShop = new EShopClient();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocation(0, 500);
        setLayout(new GridBagLayout());
        frameLayoutErstellen();
        addWindowListener(new FensterSchliesser(eShop));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);

        buttonEinloggen.addActionListener(new Einloggen());
        buttonRegistrieren.addActionListener(new KundeRegistrieren());
        buttonAusloggen.addActionListener(new Ausloggen());
        nachArtikelnummerButton.addActionListener(this::verarbeiteSortierenKlick);
        nachBezeichnungButton.addActionListener(this::verarbeiteSortierenKlick);
    }

    public void frameLayoutErstellen() {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(5, 5, 5, 5);

        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 3;
        g.weightx = 1.0;
        g.weighty = 0.1;
        add(panalNorth, g);

        g.gridx = 0;
        g.gridy = 1;
        g.gridwidth = 1;
        g.weightx = 0.2;
        g.weighty = 1.0;
        add(panalWest, g);

        g.gridx = 1;
        g.gridy = 1;
        g.gridwidth = 1;
        g.weightx = 0.4;
        g.weighty = 1.0;
        add(panalCenter, g);

        g.gridx = 2;
        g.gridy = 1;
        g.gridwidth = 1;
        g.weightx = 0.4;
        g.weighty = 1.0;
        add(panalEast, g);

        g.gridx = 0;
        g.gridy = 2;
        g.gridwidth = 3;
        g.weightx = 1.0;
        g.weighty = 0.1;
        add(panalSouth, g);
    }

    private void loginRegistierenLayout() {
        JPanel northPanel = new JPanel(new FlowLayout());
        northPanel.add(buttonEinloggen);
        northPanel.add(buttonRegistrieren);
        panalNorth.add(northPanel);
    }

    private void ausloggenLayout() {
       JPanel southPanel = new JPanel();
       southPanel.add(buttonAusloggen);
       panalSouth.add(southPanel);
    }

    public void layoutFuegeArtikelListeHinzu(List<Artikel> artikelListe) throws IOException {
        artikelTableModel = new ArtikelTableModel(artikelListe);
        artikelTabelle = new JTable(artikelTableModel);
        scrollPane = new JScrollPane(artikelTabelle);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panalCenter.setLayout(new BorderLayout());
        panalCenter.add(scrollPane);
    }

    public void layoutArtikelSortieren() {
        JPanel westPanel = new JPanel(new GridBagLayout());
        Dimension buttonGroesse = new Dimension(180, 30);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);  //Abstände der Komponenten
        g.anchor = GridBagConstraints.NORTHWEST;

        JLabel labelArtikelSortieren = new JLabel("Artikel sortieren:");
        g.gridx = 0;
        g.gridy = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        westPanel.add(labelArtikelSortieren, g);

        nachArtikelnummerButton.setPreferredSize(buttonGroesse);
        g.gridy = 1;
        g.fill = GridBagConstraints.NONE;
        westPanel.add(nachArtikelnummerButton, g);

        nachBezeichnungButton.setPreferredSize(buttonGroesse);
        g.gridy = 2;
        westPanel.add(nachBezeichnungButton, g);
        panalWest.add(westPanel);
    }

    public void layoutfuegeEreignisListeHinzu(List<Ereignis> ereignisListe) throws IOException, ClassNotFoundException {
        ereignisTableModel = new EreignisTableModel(ereignisListe);
        ereignisTabelle = new JTable(ereignisTableModel);
        scrollPane = new JScrollPane(ereignisTabelle);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panalEast.setLayout(new BorderLayout());
        panalEast.add(scrollPane);
    }

    public void layoutFuegeWarenkorbTabelleHinzu() throws IOException, ClassNotFoundException {
        Kunde angemeldeterKunde = (Kunde) eShop.getAngemeldet();
        Warenkorb warenkorb = angemeldeterKunde.getWarenkorb();
        warenkorbTableModel = new WarenkorbTableModel(warenkorb);
        warenkorbTabelle = new JTable(warenkorbTableModel);
        scrollPane = new JScrollPane(warenkorbTabelle);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panalEast.setLayout(new BorderLayout());
        panalEast.add(scrollPane);
    }

    public void verarbeiteSortierenKlick(ActionEvent e) {
        try {
            panalCenter.removeAll();
            List<Artikel> artikelListe;
            if (e.getSource().equals(nachBezeichnungButton)){
                artikelListe = eShop.sortiertNachBezeichnung();
                layoutFuegeArtikelListeHinzu(artikelListe);
            } else if (e.getSource().equals(nachArtikelnummerButton)){
                artikelListe = eShop.sortiertNachArtikelnummer();
                layoutFuegeArtikelListeHinzu(artikelListe);
            }
            revalidate();
            repaint();
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
        }
    }

    public class Ausloggen implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int result = JOptionPane.showConfirmDialog(null, "Wollen Sie sich wirklich ausloggen und zum Hauptmenü zurückkehren?", "Ausloggen", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    eShop.speichernDaten();
                    eShop.beenden();
                    hauptMenue();
                }
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
            } catch (IOPersistenceException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    public class KundeRegistrieren implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String name = stringEingeben("Benutzername eingeben");
                String passwort = stringEingeben("Passwort eingeben");
                String strasse = stringEingeben("Staße eingeben");
                int hausnummer = intEingeben("Hausnummer eingeben");
                int plz = intEingeben("Postleitzahl eingeben");
                String ort = stringEingeben("Ort eingeben");

                Adresse adresse = eShop.erstellenAdresse(strasse, hausnummer, plz, ort);
                Kunde kunde = eShop.kundeRegistrieren(passwort, name, adresse);
                int benutzernummer = kunde.getNummer();
                JOptionPane.showMessageDialog(null,"Registrierung erfolgreich. Ihre Benutzernummer ist " + benutzernummer);
            } catch (NutzerExisitiertBereitsException | LeereEingabeException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            } catch (EingabeUnterbrechungException ignored) {
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
            }
        }
    }

    public class Einloggen implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                int benutzerNummer = intEingeben("Gib Kundennumer oder Mitarbeiternummer ein.");
                String benutzerKennwort = stringEingeben("Gib Passwort ein.");

                eShop.bestaetigen(benutzerKennwort, benutzerNummer);
                if (eShop.istKunde()) {
                    JOptionPane.showMessageDialog(null, "Willkommen Kunde " + eShop.getAngemeldet().getName());
                    kundenMenue();
                } else {
                    JOptionPane.showMessageDialog(null, "Willkommen Mitarbeiter " + eShop.getAngemeldet().getName());
                    mitarbeiterMenue();
                }
            } catch (NutzerExisistiertNichtException ex) {
                JOptionPane.showMessageDialog(null, "Nutzer existiert nicht.");
            } catch (EingabeUnterbrechungException ignored) {
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
            }
        }
    }

    public void mitarbeiterMenueBar() {
        JMenuBar mb = new JMenuBar();

        mb.add(new ArtikelMenue(eShop,this));
        mb.add(new MitarbeiterMenue(eShop));
        mb.add(new EreignisMenue(eShop,this));
        setJMenuBar(mb);
    }

    public void kundenMenueBar() {
        JMenuBar mb = new JMenuBar();

        mb.add(new WarenkorbMenue(eShop, this));
        setJMenuBar(mb);
    }

    public void zuruecksetzen() {
        panalNorth.removeAll();
        panalCenter.removeAll();
        panalWest.removeAll();
        panalSouth.removeAll();
        panalEast.removeAll();
    }

    public void kundenMenue() throws IOException, ClassNotFoundException {
        zuruecksetzen();
        kundenMenueBar();
        ausloggenLayout();
        layoutArtikelSortieren();
        layoutFuegeArtikelListeHinzu(eShop.getArtikelListe());
        layoutFuegeWarenkorbTabelleHinzu();
        revalidate();
        repaint();
    }

    public void mitarbeiterMenue() throws IOException, ClassNotFoundException {
        zuruecksetzen();
        mitarbeiterMenueBar();
        ausloggenLayout();
        layoutArtikelSortieren();
        layoutFuegeArtikelListeHinzu(eShop.getArtikelListe());
        layoutfuegeEreignisListeHinzu(eShop.getEreignisListe());
        revalidate();
        repaint();
    }

    public void hauptMenue() throws IOException, ClassNotFoundException {
        zuruecksetzen();
        setJMenuBar(null);
        loginRegistierenLayout();
        layoutArtikelSortieren();
        layoutFuegeArtikelListeHinzu(eShop.getArtikelListe());
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        try {
            EshopGUI gui = new EshopGUI();
            gui.eShop.ladenDaten();
            gui.hauptMenue();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Fehler bei Server-Client-Kommunikation.");
        } catch (ClassNotFoundPersistenceException | IOPersistenceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
