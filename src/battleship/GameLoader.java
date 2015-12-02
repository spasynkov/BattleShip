package battleship;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by spasynkov on 29.11.2015.
 */
public class GameLoader {
    private static final Map<String, String> LANG = new HashMap<>();
    private static final BufferedReader CONSOLE_READER = new BufferedReader(new InputStreamReader(System.in));
    private static final Logger log = Logger.getInstance();

    static {
        createProperties();
    }

    public static void main(String[] args) {
        try {
            loadLanguage(args);
        } catch (IOException e) {
            //e.printStackTrace();
            // TODO add System.exit()
        }

        Field player = new Field();
        log.write("Player field created.");
        Field enemy = new Field();
        log.write("Enemy field created.");

        log.write("Asking user to place his ships.");
        putShips(player);

        closeStream(CONSOLE_READER);
        log.write("Program finished.");
        log.write();
        closeStream(log);
    }

    private static void putShips(Field field) {
        System.out.println(LANG.get("Rules of placing ships"));
        field.drawField();
        System.out.println(LANG.get("Show input format"));
        for (int i = 4; i > 0; i--) {
            placeShipByDeckNumber(field, i);
        }
        log.write("All ships are placed by user. Cleaning field...");
        field.clearField();
    }

    private static void placeShipByDeckNumber(Field field, int numberOfDecks) {
        String sNumberOfDecks = String.valueOf(numberOfDecks);
        if (numberOfDecks == 1) sNumberOfDecks = "single-";
        String start = null;
        String end = null;
        for (int i = numberOfDecks - 1; i < 4; i++) {
            boolean flag = true;
            do {
                if (numberOfDecks != 1)
                    System.out.print(LANG.get("Ask for start point part1") + sNumberOfDecks + LANG.get("Ask for start point part2"));
                else System.out.print(LANG.get("Ask for single-deck"));
                try {
                    start = CONSOLE_READER.readLine();
                    if (numberOfDecks != 1) {
                        System.out.print(LANG.get("Ask for end point part1") + sNumberOfDecks + LANG.get("Ask for end point part2"));
                        end = CONSOLE_READER.readLine();
                        flag = !field.putShip(numberOfDecks, start, end);
                    } else flag = !field.putShip(start);
                } catch (IOException e) {
                    log.write("Exception while reading from console.\n" + e.getMessage());
                    // TODO add System.exit()
                } catch (ShipPlacementException e) {
                    System.out.println("Bad coordinates");
                    // TODO обработать исключения
                    log.write("Bad coordinates for " + sNumberOfDecks + "decker (" + start + ", " + end + ")\n" + e.getMessage());
                } catch (Exception e) {
                    System.out.println(LANG.get("Abstract error"));
                    log.write("Something bad happened.\n" + e.getMessage());
                }
                if (!flag) {
                    if (numberOfDecks != 1)
                        log.write("Coordinates for " + sNumberOfDecks + "decker: (" + start.toUpperCase() + ", " + end.toUpperCase() + ").");
                    else log.write("Coordinates for " + sNumberOfDecks + "decker: (" + start.toUpperCase() + ").");
                }
            } while (flag);
            field.drawField();
            if (i != 3) System.out.println(LANG.get("Ask for another ship"));
        }
    }

    private static void createProperties() {
        LANG.put("welcome text", "Welcome in this game!");
        LANG.put("Rules of placing ships", "The ships should be placed in a line horizontally or vertically with at least one cell between ships.\n" +
                "Single-deck ships occupy one cell. There should be 4 single-deck ships, 3 double-deck ones (2 cells in a line),\n" +
                "2 triple-deckers (3 cells in a line) and 1 with 4 decks (4 cells in a line).");
        LANG.put("Show input format", "Let's use coordinates in format \"B4\".");
        LANG.put("Ask for start point part1", "Please, enter starting coordinate for ship with ");
        LANG.put("Ask for start point part2", " decks: ");
        LANG.put("Ask for end point part1", "And now ending coordinate for ship with ");
        LANG.put("Ask for end point part2", " decks, please: ");
        LANG.put("Ask for single-deck", "Please, enter coordinate for single-decker ship: ");
        LANG.put("Ask for another ship", "Now place another ship of same class.");
        LANG.put("Abstract error", "Oops, something bad happens and we don't know why... Could you try again, please?");

        log.write("Saving " + LANG.size() + " language strings in file.");
        Properties propertiesForSave = new Properties();
        for (Map.Entry<String, String> map : LANG.entrySet()) {
            propertiesForSave.setProperty(map.getKey(), (map.getValue()));
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream("./language" + "_en");
            propertiesForSave.store(os, null);
            log.write("Ok.");
        } catch (IOException e) {
            log.write("There was an error while saving language strings to file\n" + e.getMessage());
        } finally {
            closeStream(os);
        }
    }

    private static void loadLanguage(String[] args) throws IOException {
        if (args.length < 2) {
            String langCode = "_en";
            if (args.length == 1) {
                if ("ru".equals(args[0].toLowerCase()) ||
                        "rus".equals(args[0].toLowerCase()) ||
                        "russian".equals(args[0].toLowerCase()) ||
                        "русский".equals(args[0].toLowerCase()))
                    langCode = "_ru";
                if ("ua".equals(args[0].toLowerCase()) ||
                        "ukr".equals(args[0].toLowerCase()) ||
                        "ukrainian".equals(args[0].toLowerCase()) ||
                        "українська".equals(args[0].toLowerCase()))
                    langCode = "_ua";
            }
            Properties propertiesFromFile = new Properties();
            FileInputStream is = null;
            log.write("Loading language \"" + langCode.substring(1) + "\" from file.");
            try {
                is = new FileInputStream("./language" + langCode);
                propertiesFromFile.load(is);
                Enumeration languageStrings = propertiesFromFile.propertyNames();
                while (languageStrings.hasMoreElements()) {
                    String key = (String) languageStrings.nextElement();
                    LANG.put(key, propertiesFromFile.getProperty(key));
                }
            } catch (IOException e) {
                log.write("Failed!\n" + e.getMessage());
                throw e;
            } finally {
                closeStream(is);
            }
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
                if (!(stream instanceof Logger)) log.write(stream.getClass().getName() + " closed.");
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
