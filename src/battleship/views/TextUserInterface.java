package battleship.views;

import battleship.ShipPlacementException;
import battleship.entities.Player;
import battleship.service.PlayersLogic;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static battleship.utils.BattleshipUtils.closeStream;

/**
 * This class is responsible for interaction with a user using console.
 */
public class TextUserInterface {
    private static final Map<String, String> LANG = new HashMap<>();
    private static final BufferedReader CONSOLE_READER = new BufferedReader(new InputStreamReader(System.in));

    private String enemyName;

    /*TextUserInterface() {
        super();
        //createDefaultLanguagePack();
        loadLanguage(null);
    }*/

    public TextUserInterface(Player player, String[] args) {
        super(player);
        createDefaultLanguagePack();
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
        LANG.put("Fields names1", "             Your field                         ");
        LANG.put("Fields names2", "\'s field");
        LANG.put("Game started", "Ok, game is starting right now!\n\n");

        logger.write("Saving " + LANG.size() + " language strings in file...");
        Properties propertiesForSave = new Properties();
        for (Map.Entry<String, String> map : LANG.entrySet()) {
            propertiesForSave.setProperty(map.getKey(), (map.getValue()));
        }
        FileOutputStream os = null;
        logger.write("Opening file for save.");
        try {
            os = new FileOutputStream("./language" + "_en");
            propertiesForSave.store(os, null);
            logger.write("Ok.");
        } catch (IOException e) {
            logger.write("There was an error while saving language strings to file", e);
        } finally {
            closeStream(os);
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
        boolean fileExists = file.exists() && !file.isDirectory();

        if (!fileExists) {
            // If not exists - check if english language pack file exists
            logger.write("Can't find " + file.getAbsolutePath() + " file. Trying to find default language pack...");
            langCode = defaultLangCode;
            file = new File("./language" + langCode);
            if (file.exists() && !file.isDirectory()) {
                fileExists = true;
            } else {
                logger.write("Can't load default language pack from " + file.getAbsolutePath() +
                        ". Generating file from available strings...");
                createDefaultLanguagePack();   // If still not - create it from template
            }
        } else {
            logger.write("Loading language \"" + langCode.substring(1) + "\" from file...");
            Properties propertiesFromFile = new Properties();
            FileInputStream is = null;
            logger.write("Opening file " + file.getAbsolutePath());
            try {
                is = new FileInputStream(file);
                propertiesFromFile.load(is);
                Enumeration languageStrings = propertiesFromFile.propertyNames();
                while (languageStrings.hasMoreElements()) {
                    String key = (String) languageStrings.nextElement();
                    LANG.put(key, propertiesFromFile.getProperty(key));
                }
                logger.write("Ok.");
            } catch (IOException e) {
                logger.write("Failed!", e);
            } finally {
                closeStream(is);
            }
        }
    }

    public static void end() {
        closeStream(CONSOLE_READER);
    }

    public void printWelcomeMessage() {
        System.out.println(LANG.get("welcome text"));
    }

    /**
     * Setting this player's name
     *
     * @return {@link String} name for user
     */
    public String askForUserName() {
        // TODO replace hardcoded strings
        String name = "";
        try {
            System.out.print("Enter your name (or leave it blank): ");
            name = CONSOLE_READER.readLine();
        } catch (Exception e) {
            logger.write("Error while reading user's name.", e);
        }
        return name;
    }

    /**
     * Ask player for the computer's name
     *
     * @return {@link String} name for computer
     */
    public String askForComputersName() {
        // TODO replace hardcoded strings
        try {
            System.out.print("Enter opponent's name (or leave it blank): ");
            enemyName = CONSOLE_READER.readLine();
            System.out.println("Ok.");
        } catch (Exception e) {
            logger.write("Error while reading computer's name.", e);
        }
        return enemyName;
    }

    public String getDesirableComputerName() {
        return !enemyName.isEmpty() ? enemyName : null;
    }

    public void placeShips() {
        System.out.println(LANG.get("Rules of placing ships"));
        drawField();
        System.out.println(LANG.get("Show input format"));
        for (int i = 4; i > 0; i--) {
            placeShipByDeckNumber(i);
        }
        synchronized (logger) {
            logger.write("All ships are placed by user. Cleaning field...");
        }
        clearField();
    }

    public void placeShipByDeckNumber(int numberOfDecks) {
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
                        flag = !putShipsAtField(startX, startY, numberOfDecks, endX, endY);
                    } else flag = !putShipsAtField(startX, startY);
                } catch (IOException e) {
                    synchronized (logger) {
                        logger.write("Exception while reading from console.", e);
                    }
                    // TODO add System.exit()
                } catch (ShipPlacementException e) {
                    System.out.println("Bad coordinates");
                    // TODO обработать исключения
                    synchronized (logger) {
                        logger.write("Bad coordinates for " + sNumberOfDecks + "decker (" + start + ", " + end + ")", e);
                    }
                } catch (Exception e) {
                    System.out.println(LANG.get("Abstract error"));
                    synchronized (logger) {
                        logger.write("Something bad happened while user was trying to place his ships.", e);
                    }
                }
            } while (flag);
            synchronized (logger) {
                logger.write("Coordinates for user's " + sNumberOfDecks + "decker: (" +
                        start.toUpperCase() + (numberOfDecks > 1 ? ", " + end.toUpperCase() : "") + ").");
            }
            drawField();
            if (i != 3) System.out.println(LANG.get("Ask for another ship"));
        }
    }

    public int beingAttacked(int x, int y) {
        // TODO hardcoded strings
        int result = checkDeckAtField(x, y);
        System.out.print("Computer shoots at: " + (char)('A' + x) + (y + 1) + "... ");
        logger.write("Computer shoots at: " + (char) ('A' + x) + (y + 1) + ".");
        if (result == 0) {
            System.out.println("But it missed. Your turn.");
            logger.write("Computer misses.");
            return result;
        }
        if (result > 0) {
            if (result == 1) {
                System.out.println("Computer hits your ship.");
                logger.write("Computer hits ship.");
                return result;
            }
            if (result == 2) {
                System.out.println("Computer killed your ship.");
                logger.write("Computer kills ship.");
                return result;
            }
        }
        if (result == -1) System.out.println("Computer already hit this cell. It should choose another one.");
        return result;
    }

    public void makeShoot(PlayersLogic enemy) {
        // TODO replace hardcoded language strings
        int x = -1, y = -1;
        int repeat = 1;
        incrementTheNumberOfMovesPlayerDid();
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
                logger.write("User input had bad coordinates for shoot: " + sCoordinates, e);
            } catch (Exception e) {
                logger.write("There was an error while getting user's coordinates for next shoot", e);
            }
            if (x >= 0 && !isAvailableForShoot(enemy.getCell(x, y))) {
                System.out.println("You already shoot this cell. Try another one.");
                x = -1;
            }
            if (x >= 0) {
                repeat = enemy.beingAttacked(x, y);
                logger.write("User shoots: " + (char) ('A' + x) + (y + 1) + ".");
                switch (repeat) {
                    case -1 : {
                        System.out.println("You already shoot this cell1. Try another one.");
                        logger.write("User already shoot this cell.");
                        break;
                    }
                    case 0 : {
                        System.out.println("You missed. Computer's turn.");
                        logger.write("User misses.");
                        break;
                    }
                    case 1 : {
                        System.out.println("You hit computer's ship! Shoot again!");
                        logger.write("User hits a ship.");
                        break;
                    }
                    case 2 : {
                        System.out.println("Great! You've just killed computer's ship!");
                        logger.write("User kills computer's ship.");
                        break;
                    }
                }
            }
        } while (repeat != 0);
        if (cycleCounter > getTheLongestStreak()) setTheLongestStreak(cycleCounter);
    }

    public void won() {
        super.won();
        // TODO replace hardcoded language strings
        System.out.println("You won!");
        System.out.println("You did it in " + getTheNumberOfMovesPlayerDid() + " moves.");
        System.out.println("The longest streak of successful hits you did is " + getTheLongestStreak() + " hits.");
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
    private void drawField() {
        System.out.println("   --------------------------------");
        for (int i = 9; i >= 0; i--) {
            if (i != 9) System.out.print(" ");
            System.out.print((i + 1) + " |");
            for (int j = 0; j < 10; j++) {
                System.out.print(" " + getCell(j, i) + " ");
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("   --------------------------------");
        System.out.println("     A  B  C  D  E  F  G  H  I  J");
    }

    public void drawAllFields(PlayersLogic enemy) {
        System.out.println(LANG.get("Fields names1") + enemy.getPlayerName() + LANG.get("Fields names2"));
        System.out.println("   --------------------------------    --------------------------------");
        for (int i = 9; i >= 0; i--) {
            String number;
            if (i == 9) number = String.valueOf(i + 1);
            else number = " " + String.valueOf(i + 1);
            StringBuilder sb = new StringBuilder();
            sb.append(number).append(" |");
            for (int j = 0; j < 10; j++) {
                sb.append(" ").append(getCell(j, i)).append(" ");
            }
            sb.append("| ").append(number).append(" |");
            for (int j = 0; j < 10; j++) {
                sb.append(" ").append(enemy.getCellSafe(j, i)).append(" ");
            }
            sb.append("|");
            System.out.println(sb);
        }
        System.out.println("   --------------------------------    --------------------------------");
        System.out.println("     A  B  C  D  E  F  G  H  I  J        A  B  C  D  E  F  G  H  I  J");
    }

    public void askToWait() {
        System.out.println(LANG.get("Wait for computer ships"));
    }

    public void gameStarted() {
        System.out.println(LANG.get("Game started"));
    }

    public void loose() {
        // TODO replace hardcoded language strings
        System.out.println(getPlayerName() + " loose.");
        logger.write("User \'" + getPlayerName() + "\' loose. User moves = " + getTheNumberOfMovesPlayerDid() + ", the longest streak = " + getTheLongestStreak());
        System.out.println("You did only " + getTheNumberOfMovesPlayerDid() + " moves.");
        System.out.println("The longest streak of successful hits you did is " + getTheLongestStreak() + " hits.");

    }
}
