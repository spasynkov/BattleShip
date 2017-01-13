package battleship.controllers;

import battleship.entities.Field;
import battleship.entities.Player;
import battleship.service.FieldService;
import battleship.service.HumanLogic;
import battleship.service.MachineLogic;
import battleship.service.PlayersLogic;
import battleship.utils.BattleshipUtils;
import battleship.utils.Logger;
import battleship.views.TextUserInterface;

import java.io.IOException;

import static battleship.service.PlayersLogic.setLogger;

/**
 * The loader of the game.
 * Main method is here.
 */
// todo: add comments
// todo: add loggers levels
// todo: add names in messages
// todo: add statusbars
// todo: add statistics (time + hits efficiency)
// todo: add default properties opening
// todo: add other then ABCDEFGHIJ strings
// todo: add different fields sizes and ships number
// todo: incapsulate getCells
// todo: add languages
// todo: play with comp against comp
// todo: int -> byte
// TODO: fix javadocs
class GameLoader {

    private static final Logger log = Logger.getInstance();
    private static BattleshipUtils utils = new BattleshipUtils();

    public static void main(String[] args) {
        // setting logger
        setLogger(log);
        utils.setLogger(log);
        utils.createDefaultLanguagePack();
        utils.loadLanguage(args);


        PlayersLogic.setUtils(utils);

        // creating players
        Player humanUser = new Player("User");
        Player computer = new Player("Computer");

        // TODO: get fields size and number of ships from properties

        // creating fields for every player
        Field usersField = new Field(10, 10, 10);
        humanUser.setField(usersField);
        Field computersField = new Field(10, 10, 10);
        computer.setField(computersField);

        // creating field services to operate with fields
        FieldService userFieldService = new FieldService(usersField);
        FieldService computerFieldService = new FieldService(computersField);

        // creating roles
        HumanLogic human = new HumanLogic(humanUser);
        MachineLogic machine = new MachineLogic(computer);

        // adding opportunity for roles to work with their fields
        human.setField(userFieldService);
        machine.setField(computerFieldService);

        // creating user interface and injecting it in object that works with a human player
        TextUserInterface userInterface = new TextUserInterface();
        userInterface.setUtils(utils);
        human.setUserInterface(userInterface);

        // starting the game
        userInterface.printWelcomeMessage();


        // asking user for names
        String newName;
        try {       // asking for username
            newName = userInterface.askForUserName();
            if (!newName.isEmpty()) humanUser.setName(newName);
        } catch (IOException e) {
            log.write("Error while reading user's name.", e);
        }

        try {       // asking for computer's name
            newName = userInterface.askForComputersName();
            if (!newName.isEmpty()) computer.setName(newName);
        } catch (IOException e) {
            log.write("Error while reading computer's name.", e);
        }

        // ships placement
        machine.placeShips();
        log.writeSynchronized("Asking user to place his ships.");

        userInterface.showRules();
        userInterface.drawField(human, false);  // draw empty field
        userInterface.showInputFormat();
        human.placeShips();
        log.writeSynchronized("All ships are placed by user.");

        if (machine.isAlive()) {
            userInterface.askToWait();
            try {
                machine.waitForFinishingThread();
                log.write("Computer's ships placement finished.");
            } catch (InterruptedException e) {
                log.write("Computer's ships placement was interrupted by something.", e);
                System.exit(1);     // TODO: define statuses
            }
        }

        userInterface.gameStarted();

        boolean playersOrderSwitcher = true;
        while (human.isMoreShips() && machine.isMoreShips()) {
            if (playersOrderSwitcher) {
                userInterface.drawAllFields(human, machine);
                human.makeShootAt(machine);
            } else machine.makeShootAt(human);
            playersOrderSwitcher = !playersOrderSwitcher;
        }

        // game finished
        userInterface.drawAllFields(human, machine);
        // preparing stats
        String playerName = humanUser.getName();
        int theNumberOfMovesPlayerDid = human.getTheNumberOfMovesPlayerDid();
        int theLongestStreak = human.getTheLongestStreak();
        if (human.isMoreShips()) {
            userInterface.won(theLongestStreak, theNumberOfMovesPlayerDid);
            log.write("User \'" + playerName + "\' won. " +
                    "User moves = " + theNumberOfMovesPlayerDid + ", the longest streak = " + theLongestStreak);
        } else {
            userInterface.loose(playerName, theLongestStreak, theNumberOfMovesPlayerDid);
            log.write("User \'" + playerName + "\' loose. " +
                    "User moves = " + theNumberOfMovesPlayerDid + ", the longest streak = " + theLongestStreak);
        }

        userInterface.end();
        log.write("Program finished.");
        log.write();
        utils.closeStream(log);
    }
}
