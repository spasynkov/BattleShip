package battleship.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class contains methods used by other classes.
 */
public class BattleshipUtils {
    private static final Map<String, String> LANG = new HashMap<>();

    private static Logger logger;

    public static Map<String, String> getLANG() {
        return LANG;
    }

    public static void setLogger(Logger logger) {
        BattleshipUtils.logger = logger;
    }

    public static void createDefaultLanguagePack() {
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

    public static void loadLanguage(String[] args) {
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

    public static void closeStream(Closeable stream) {
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
