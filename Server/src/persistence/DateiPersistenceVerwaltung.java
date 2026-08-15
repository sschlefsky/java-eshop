package persistence;

import common.Artikel;
import common.Benutzer;
import common.Ereignis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DateiPersistenceVerwaltung {

    private static final Path DATA_DIRECTORY = Path.of("data");

    private static final Path ARTIKEL_DATEI =
            DATA_DIRECTORY.resolve("artikelDaten.ser");

    private static final Path BENUTZER_DATEI =
            DATA_DIRECTORY.resolve("benutzerDaten.ser");

    private static final Path EREIGNISSE_DATEI =
            DATA_DIRECTORY.resolve("ereignisseDaten.ser");

    public void schreibeArtikelListe(List<Artikel> artikelListe) throws IOException {
        schreibeObjekt(ARTIKEL_DATEI, new ArrayList<>(artikelListe));
    }

    public List<Artikel> leseArtikelListe()
            throws IOException, ClassNotFoundException {

        return leseObjekt(ARTIKEL_DATEI);
    }

    public void schreibeBenutzerListe(List<Benutzer> benutzerListe)
            throws IOException {

        schreibeObjekt(BENUTZER_DATEI, new ArrayList<>(benutzerListe));
    }

    public List<Benutzer> leseBenutzerListe()
            throws IOException, ClassNotFoundException {

        return leseObjekt(BENUTZER_DATEI);
    }

    public void schreibeEreignisListe(List<Ereignis> ereignisListe)
            throws IOException {

        schreibeObjekt(EREIGNISSE_DATEI, new ArrayList<>(ereignisListe));
    }

    public List<Ereignis> leseEreignisListe()
            throws IOException, ClassNotFoundException {

        return leseObjekt(EREIGNISSE_DATEI);
    }

    private void schreibeObjekt(Path datei, Object daten) throws IOException {
        Files.createDirectories(DATA_DIRECTORY);

        try (ObjectOutputStream output =
                     new ObjectOutputStream(Files.newOutputStream(datei))) {

            output.writeObject(daten);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> leseObjekt(Path datei)
            throws IOException, ClassNotFoundException {

        if (Files.notExists(datei)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(datei))) {

            return (List<T>) input.readObject();
        }
    }
}