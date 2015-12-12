package battleship;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class is responsible for interaction with a user using console.
 */
class UserInterface {
    private final Field field = new Field();
    private static final Logger log = Logger.getInstance();
    private static final Map<String, String> LANG = new HashMap<>();
    private static final BufferedReader CONSOLE_READER = new BufferedReader(new InputStreamReader(System.in));

    public UserInterface(String[] args) {
        //createDefaultLanguagePack();
        loadLanguage(args);
    }

    private static void createDefaultLanguagePack() {
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
        LANG.put("Wait for computer ships", "Oh, you're so fast!\nPlease, give some time to your computer with finishing placing his ships.");
        LANG.put("Fields names", "             Your field                         Enemy's field");
        LANG.put("Game started", "Ok, game is starting right now!\n\n");

        log.write("Saving " + LANG.size() + " language strings in file...");
        Properties propertiesForSave = new Properties();
        for (Map.Entry<String, String> map : LANG.entrySet()) {
            propertiesForSave.setProperty(map.getKey(), (map.getValue()));
        }
        FileOutputStream os = null;
        log.write("Opening file for save.");
        try {
            os = new FileOutputStream("./language" + "_en");
            propertiesForSave.store(os, null);
            log.write("Ok.");
        } catch (IOException e) {
            log.write("There was an error while saving language strings to file\n" + e.getMessage());
        } finally {
            BattleshipUtils.closeStream(os);
        }
    }

    private static void loadLanguage(String[] args) {
        String defaultLangCode = "_en"; // English by default
        String langCode = defaultLangCode;
        if (args != null) {
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
        }

        // Check if file exists
        File file = new File("./language" + langCode);
        boolean fileExists = false;
        if (file.exists() && !file.isDirectory()) {
            fileExists = true;
        } else {
            // If not exists - check if english language pack file exists
            log.write("Can't find " + file.getAbsolutePath() + " file. Trying to find default language pack...");
            langCode = defaultLangCode;
            file = new File("./language" + langCode);
            if (file.exists() && !file.isDirectory()) {
                fileExists = true;
            } else {
                log.write("Can't load default language pack from " + file.getAbsolutePath() +
                        ". Generating file from available strings...");
                createDefaultLanguagePack();   // If still not - create it from template
            }
        }

        if (fileExists) {
            log.write("Loading language \"" + langCode.substring(1) + "\" from file...");
            Properties propertiesFromFile = new Properties();
            FileInputStream is = null;
            log.write("Opening file " + file.getAbsolutePath());
            try {
                is = new FileInputStream(file);
                propertiesFromFile.load(is);
                Enumeration languageStrings = propertiesFromFile.propertyNames();
                while (languageStrings.hasMoreElements()) {
                    String key = (String) languageStrings.nextElement();
                    LANG.put(key, propertiesFromFile.getProperty(key));
                }
                log.write("Ok.");
            } catch (IOException e) {
                log.write("Failed!\n" + e.getMessage());
            } finally {
                BattleshipUtils.closeStream(is);
            }
        }
    }

    void placeShips() {
        System.out.println(LANG.get("Rules of placing ships"));
        field.draw();
        System.out.println(LANG.get("Show input format"));
        for (int i = 4; i > 0; i--) {
            placeShipByDeckNumber(i);
        }
        synchronized (log) {
            log.write("All ships are placed by user. Cleaning field...");
        }
        field.clear();
    }

    private void placeShipByDeckNumber(int numberOfDecks) {
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
                    synchronized (log) {
                        log.write("Exception while reading from console.\n" + e.getMessage());
                    }
                    // TODO add System.exit()
                } catch (ShipPlacementException e) {
                    System.out.println("Bad coordinates");
                    // TODO обработать исключения
                    synchronized (log) {
                        log.write("Bad coordinates for " + sNumberOfDecks + "decker (" + start + ", " + end + ")\n" + e.getMessage());
                    }
                } catch (Exception e) {
                    System.out.println(LANG.get("Abstract error"));
                    synchronized (log) {
                        log.write("Something bad happened.\n" + e.getMessage());
                    }
                }
            } while (flag);
            synchronized (log) {
                log.write("Coordinates for user's " + sNumberOfDecks + "decker: (" +
                        start.toUpperCase() + (numberOfDecks > 1 ? ", " + end.toUpperCase() : "") + ").");
            }
            field.draw();
            if (i != 3) System.out.println(LANG.get("Ask for another ship"));
        }
    }

    String getLine(int i) {
        return field.getLine(i);
    }

    void drawFields(String[] enemyField) {
        System.out.println(LANG.get("Fields names"));
        System.out.println("   --------------------------------    --------------------------------");
        for (int i = 9; i >= 0; i--) {
            String number;
            if (i == 9) number = String.valueOf(i + 1);
            else number = " " + String.valueOf(i + 1);
            System.out.println(number  + " " + this.getLine(i) + " " + number + " " + enemyField[i]);
        }
        System.out.println("   --------------------------------    --------------------------------");
        System.out.println("     A  B  C  D  E  F  G  H  I  J        A  B  C  D  E  F  G  H  I  J");
    }

    static void end() {
        BattleshipUtils.closeStream(CONSOLE_READER);
    }

    public void askToWait() {
        System.out.println(LANG.get("Wait for computer ships"));
    }

    public void gameStarted() {
        System.out.println(LANG.get("Game started"));
    }
}
