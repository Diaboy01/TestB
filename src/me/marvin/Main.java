package me.marvin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Main {

    private static long guthaben;
    private static String apikey = "2604202201072001";
    private static String[] angebote = {"Bier", "Nudeln", "Chips"};
    private static double[] preise = {23804.3750, 18072.1600, 213.7500};
    private static int currentAngebot = -1;
    private static long ablauf = -1;
    private static SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        guthaben = Long.parseLong(getText("http://dl6mhw.de/~markt/mservice.php?apikey=2604202201072001&cmd=guthaben"));

        String[] splitted = "preis=266;2022-05-18 22:34:01;minuten=123".split(";");
        String datum = splitted[1];
        try {
            ablauf = format.parse(datum).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("Guthaben: " + guthaben);
        System.out.println("Angebot jetzt: " + (currentAngebot == -1 ? "Keine" : angebote[currentAngebot]));

        long nextAngebotCheck = System.currentTimeMillis() + 1000 * 60 * 10;

        while(true) {
            if(currentAngebot == -1) {
                if(System.currentTimeMillis() >= nextAngebotCheck) {
                    fetch();

                    if(currentAngebot != -1) {
                        System.out.println("Neues Angebot gefunden: " + angebote[currentAngebot]);
                        System.out.println("Neues Angebot gefunden: " + format.format(ablauf));
                    }

                    nextAngebotCheck = System.currentTimeMillis() + 1000 * 60 * 10;
                }
            } else {
                long left = ablauf - System.currentTimeMillis();
                if(left < 2000) { // kleiner als 2s
                    // wie viel hast du gewettet
                    // warte die letzte Zeit
                    // sende deine Wette raus in den letzten <10ms
                    double wette = Long.parseLong(getText("")) + 1000; //TODO: URL eintragen
                    if(wette > preise[currentAngebot]) {
                        wette = preise[currentAngebot];
                    }

                    long verblieben = ablauf - System.currentTimeMillis();
                    try {
                        Thread.sleep(verblieben - 10); // warte 10ms bis Ende der Auktion
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Anfrage rausschicken
                    try {
                        URL url = new URL(""); // TODO: Biet url eingeben
                        url.openConnection();
                    } catch (IOException e) {       //DREI / MEHR FACH MACHEN????
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    currentAngebot = -1; // Angebot abgelaufen
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Welches Angebot ist gerade am laufen?
     */
    public static void fetch() {
        for(int i = 0; i < angebote.length; i++) {
            String text = getText("http://dl6mhw.de/~markt/mservice.php?apikey=2604202201072001&cmd=angebot&produkt=" + angebote[i]);
            if(text.equals("nix")) continue;
            currentAngebot = i;
            String[] splitted = text.split(";");
            String datum = splitted[1];
            try {
                ablauf = format.parse(datum).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            break;
        }
    }

    public static String getText(String url) {
        StringBuilder response = new StringBuilder();

        try {
            URL website = new URL(url);
            URLConnection connection = website.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);

            in.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return response.toString();
    }
}
