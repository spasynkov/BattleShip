package battleship.controllers;

import battleship.entities.Field;
import battleship.entities.Player;
import battleship.entities.PlayerStatistics;
import battleship.service.FieldService;
import battleship.service.HumanLogic;
import battleship.service.MachineLogic;
import battleship.service.PlayersLogicAbstractImpl;
import battleship.utils.BattleshipUtils;
import battleship.utils.Logger;
import battleship.views.TextUserInterface;

import java.io.IOException;

import static battleship.service.PlayersLogicAbstractImpl.setLogger;

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


        PlayersLogicAbstractImpl.setUtils(utils);

        // creating players
        Player humanUser = new Player("User");
        Player computer = new Player("Computer");

        // creating statistics objects for players
        PlayerStatistics userStats = new PlayerStatistics();
        PlayerStatistics enemyStats = new PlayerStatistics();


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
        HumanLogic human = new HumanLogic(humanUser, userStats);
        MachineLogic machine = new MachineLogic(computer, enemyStats);

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
        try {
            // asking for username
            newName = userInterface.askForUserName();
            if (!newName.isEmpty()) {
                humanUser.setName(newName);
                userStats.setName(newName);
            }

            // asking for computer's name
            newName = userInterface.askForComputersName();
            if (!newName.isEmpty()) {
                computer.setName(newName);
                enemyStats.setName(newName);
            }
        } catch (IOException e) {
            log.write("Error while reading user's or enemy's name from console.", e);
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

        // defining if user won or loose
        if (human.isMoreShips()) {
            userInterface.won(userStats, enemyStats);
        } else {
            userInterface.loose(userStats, enemyStats);
        }

        log.write("User \'" + userStats.getName() + "\' " + (human.isMoreShips() ? "won" : "loose") + ". " +
                "User moves = " + userStats.getTheNumberOfMoves() + ", " +
                "the longest streak = " + userStats.getTheLongestStreak() +
                "Enemy moves = " + enemyStats.getTheNumberOfMoves() + ", " +
                "the longest streak = " + enemyStats.getTheLongestStreak());

        userInterface.end();
        log.write("Program finished.");
        log.write();
        utils.closeStream(log);
    }
}
