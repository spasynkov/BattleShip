package battleship.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class contains some utility methods used by other classes.
 */
public class BattleshipUtils {
    private Properties properties;

    private Logger logger;

    public String getMessage(String key) {
        return properties.getProperty(key);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void createDefaultLanguagePack() {
        Map<String, String> lang = new HashMap<>();

        lang.put("Ask for user name", "Enter your name (or leave it blank): ");
        lang.put("Ask for enemy name", "Enter opponent's name (or leave it blank): ");
        lang.put("Welcome text", "Welcome in this game!");
        lang.put("Rules of placing ships", "The ships should be placed in a line horizontally or vertically " +
                "with at least one cell between ships.\n" +
                "Single-deck ships occupy one cell. " +
                "There should be 4 single-deck ships, 3 double-deck ones (2 cells in a line),\n" +
                "2 triple-deckers (3 cells in a line) and 1 with 4 decks (4 cells in a line).");
        lang.put("Show input format", "Let's use coordinates in format \"B4\".");
        lang.put("Ask for start point", "Please, enter starting coordinate for ship with %s decks: ");
        lang.put("Ask for end point", "And now ending coordinate for ship with %s decks, please: ");
        lang.put("Ask for single-deck", "Please, enter coordinate for single-decker ship: ");
        lang.put("Ask for another ship", "Now place another ship of same class.");
        lang.put("Abstract error", "Oops, something bad happens and we don't know why... Could you try again, please?");
        lang.put("Wait for computer ships", "Oh, you're so fast!\n" +
                "Please, give some time to your computer with finishing placing his ships.");
        lang.put("Fields names1", "             Your field                         ");
        lang.put("Fields names2", "\'s field");
        lang.put("Game started", "Ok, game is starting right now!\n\n");
        lang.put("Value out of range exception", "Value \'%s\' is out of range (less than 1 or greater than %s).");
        lang.put("Value out of range log", "New value of the longest streak is out of range.");
        lang.put("Enemy shoots", "Computer shoots at: %s%s... ");
        lang.put("Enemy missed", "But it missed. Your turn.");
        lang.put("Enemy hits", "Computer hits your ship.");
        lang.put("Enemy kills", "Computer killed your ship.");
        lang.put("Ask for coordinates for shoot", "Enter coordinates for shoot: ");
        lang.put("Repeated shoot", "You already shoot this cell!. Try another one.");
        lang.put("You missed", "You missed. Computer's turn.");
        lang.put("You hit", "You hit computer's ship! Shoot again!");
        lang.put("You killed", "Great! You've just killed computer's ship!");
        lang.put("You won", "You won!\n" +
                "You did it in %d moves.\n" +
                "The longest streak of successful hits you did is %d hits.");
        lang.put("You loose", "%s loose.\n" +
                "You did only in %d moves.\n" +
                "The longest streak of successful hits you did is %d hits.");
        lang.put("Bad coordinates", "The coordinates you entered (%s) are wrong or not recognized (%s). Try again...");
        lang.put("Empty string", "The input was empty");
        lang.put("Number format failed", "Coordinates were not in right format");
        lang.put("String length is wrong", "The length of your input was too short or too long");
        lang.put("Coordinates are wrong or out of range", "Coordinates are wrong or out of range");
        lang.put("Empty string", "The input was empty");

        logger.write("Saving " + lang.size() + " language strings in file...");

        Properties propertiesForSave = new Properties();
        propertiesForSave.putAll(lang);

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

    public void loadLanguage(String[] args) {
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
            if (!file.exists() || file.isDirectory()) {
                logger.write("Can't load default language pack from " + file.getAbsolutePath() +
                        ". Generating file from available strings...");
                createDefaultLanguagePack();   // If still not - create it from template
            }
        } else {
            logger.write("Loading language \"" + langCode.substring(1) + "\" from file...");
            properties = new Properties();
            FileInputStream is = null;
            logger.write("Opening file " + file.getAbsolutePath());
            try {
                is = new FileInputStream(file);
                properties.load(is);
                logger.write("Ok.");
            } catch (IOException e) {
                logger.write("Failed!", e);
            } finally {
                closeStream(is);
            }
        }
    }

    public void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
                if (!(stream instanceof Logger)) logger.write(stream.getClass().getName() + " closed.");
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
