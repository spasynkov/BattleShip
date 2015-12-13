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

    private int theLongestStreak = 0;
    private int theNumberOfMovesUserDid = 0;

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
            log.write("There was an error while saving language strings to file", e);
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
                log.write("Failed!", e);
            } finally {
                BattleshipUtils.closeStream(is);
            }
        }
    }

    void placeShips() {
        System.out.println(LANG.get("Rules of placing ships"));
        drawField();
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
                    int[] startXY = getCoordinatesFromString(start);
                    int startX = startXY[0];
                    int startY = startXY[1];
                    if (numberOfDecks != 1) {
                        System.out.print(LANG.get("Ask for end point part1") + sNumberOfDecks + LANG.get("Ask for end point part2"));
                        end = CONSOLE_READER.readLine();
                        int[] endXY = getCoordinatesFromString(end);
                        int endX = endXY[0];
                        int endY = endXY[1];
                        flag = !field.putShip(startX, startY, numberOfDecks, endX, endY);
                    } else flag = !field.putShip(startX, startY);
                } catch (IOException e) {
                    synchronized (log) {
                        log.write("Exception while reading from console.", e);
                    }
                    // TODO add System.exit()
                } catch (ShipPlacementException e) {
                    System.out.println("Bad coordinates");
                    // TODO обработать исключения
                    synchronized (log) {
                        log.write("Bad coordinates for " + sNumberOfDecks + "decker (" + start + ", " + end + ")", e);
                    }
                } catch (Exception e) {
                    System.out.println(LANG.get("Abstract error"));
                    synchronized (log) {
                        log.write("Something bad happened while user was trying to place his ships.", e);
                    }
                }
            } while (flag);
            synchronized (log) {
                log.write("Coordinates for user's " + sNumberOfDecks + "decker: (" +
                        start.toUpperCase() + (numberOfDecks > 1 ? ", " + end.toUpperCase() : "") + ").");
            }
            drawField();
            if (i != 3) System.out.println(LANG.get("Ask for another ship"));
        }
    }

    /**
     * This method will cast coordinates from "B4" view to normal one like "1, 5"
     * (equals to "B4". horizontal: A->0, B->1, ...; vertical: 1->0, 2->1, ...)
     * Or will throw an exception if coordinates are extremely wrong.
     */
    private int[] getCoordinatesFromString(String sCoordinates) throws ShipPlacementException {
        if (sCoordinates == null || sCoordinates.isEmpty()) throw new ShipPlacementException("Empty string");
        int[] result;
        if (sCoordinates.length() == 2 || sCoordinates.length() == 3) {
            try {
                char first = sCoordinates.toUpperCase().charAt(0);
                int second = Integer.parseInt(sCoordinates.substring(1));
                if (first >= 'A' && first <= 'J' &&
                        second > 0 && second < 11) {
                    result = new int[]{first - 'A', second - 1};
                } else throw new ShipPlacementException("Coordinates out of range");
            } catch (NumberFormatException e) {
                throw new ShipPlacementException("Number format failed");
            }
        } else throw new ShipPlacementException("String length is wrong");
        return result;
    }

    /**
     * Call this if you need to draw full field.
     */
    void drawField() {
        System.out.println("   --------------------------------");
        for (int i = 9; i >= 0; i--) {
            if (i != 9) System.out.print(" ");
            System.out.print((i + 1) + " |");
            for (int j = 0; j < 10; j++) {
                System.out.print(" " + field.getCell(j, i) + " ");
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("   --------------------------------");
        System.out.println("     A  B  C  D  E  F  G  H  I  J");
    }

    void drawAllFields(MachineLogic enemy) {
        System.out.println(LANG.get("Fields names"));
        System.out.println("   --------------------------------    --------------------------------");
        for (int i = 9; i >= 0; i--) {
            String number;
            if (i == 9) number = String.valueOf(i + 1);
            else number = " " + String.valueOf(i + 1);
            StringBuilder sb = new StringBuilder();
            sb.append(number).append(" |");
            for (int j = 0; j < 10; j++) {
                sb.append(" ").append(field.getCell(j, i)).append(" ");
            }
            sb.append("| ").append(number).append(" |");
            for (int j = 0; j < 10; j++) {
                sb.append(" ").append(enemy.getCell(j, i)).append(" ");
            }
            sb.append("|");
            System.out.println(sb);
        }
        System.out.println("   --------------------------------    --------------------------------");
        System.out.println("     A  B  C  D  E  F  G  H  I  J        A  B  C  D  E  F  G  H  I  J");
    }

    void askToWait() {
        System.out.println(LANG.get("Wait for computer ships"));
    }

    void gameStarted() {
        System.out.println(LANG.get("Game started"));
    }

    boolean isMoreShips() {
        return field.getShipsNumber() > 0;
    }

    int[][] getEmptyCells() {
        return field.getEmptyCells();
    }

    boolean gotShooted(int x, int y) {
        int result = field.checkDeckAtField(x, y);
        System.out.print("Computer shoots at: (" + (char)('A' + x) + (y + 1) + ")... ");
        log.write("Computer shoots at: (" + (char)('A' + x) + (y + 1) + ").");
        if (result == 0) {
            System.out.println("But it missed. Your turn.");
            log.write("Computer misses.");
            return false;
        }
        if (result > 0) {
            if (result == 1) {
                System.out.println("Computer hits your ship.");
                log.write("Computer hits ship.");
            }
            if (result == 2) {
                System.out.println("Computer killed your ship.");
                log.write("Computer kills ship.");
            }
        }
        if (result == -1) System.out.println("Computer already hit this cell. It should choose another one.");
        return true;
    }

    void makeShoot(MachineLogic enemy) {
        int x = -1, y = -1;
        int repeat = 1;
        theNumberOfMovesUserDid++;
        int cycleCounter = 0;
        do {
            if (!enemy.isMoreShips()) break;
            cycleCounter++;
            if (x >= 0) drawAllFields(enemy);
            System.out.print("Enter coordinates for shoot: ");
            String sCoordinates = "";
            try {
                sCoordinates = CONSOLE_READER.readLine();
                int[] coordinates = getCoordinatesFromString(sCoordinates);
                x = coordinates[0];
                y = coordinates[1];
            } catch (ShipPlacementException e) {
                log.write("User input had bad coordinates for shoot: " + sCoordinates, e);
            } catch (Exception e) {
                log.write("There was an error while getting user's coordinates for next shoot", e);
            }
            if (x >= 0 && enemy.getCell(x, y) != Field.getEmptyCell()) {
                System.out.println("You already shoot this cell. Try another one.");
                x = -1;
            }
            if (x >= 0) {
                repeat = enemy.shoot(x, y);
                log.write("User shoots: (" + (char)('A' + x) + (y + 1) + ").");
                switch (repeat) {
                    case -1 : {
                        System.out.println("You already shoot this cell. Try another one.");
                        log.write("User already shoot this cell.");
                        break;
                    }
                    case 0 : {
                        System.out.println("You missed. Computer's turn.");
                        log.write("User misses.");
                        break;
                    }
                    case 1 : {
                        System.out.println("You hit computer's ship! Shoot again!");
                        log.write("User hits a ship.");
                        break;
                    }
                    case 2 : {
                        System.out.println("Great! You've just killed computer's ship!");
                        log.write("User kills computer's ship.");
                        break;
                    }
                }
            }
        } while (repeat != 0);
        if (cycleCounter > theLongestStreak) theLongestStreak = cycleCounter;
    }

    public void won() {
        System.out.println("You won!");
        log.write("User won. Moves = " + theNumberOfMovesUserDid + ", the longest streak = " + theLongestStreak);
        System.out.println("You did it in " + theNumberOfMovesUserDid + " moves.");
        System.out.println("The longest streak of successful hits you did is " + theLongestStreak + " hits.");
    }

    public void loose() {
        System.out.println("You loose.");
        log.write("Computer won. User moves = " + theNumberOfMovesUserDid + ", the longest streak = " + theLongestStreak);
        System.out.println("You did only " + theNumberOfMovesUserDid + " moves.");
        System.out.println("The longest streak of successful hits you did is " + theLongestStreak + " hits.");

    }

    static void end() {
        BattleshipUtils.closeStream(CONSOLE_READER);
    }

}