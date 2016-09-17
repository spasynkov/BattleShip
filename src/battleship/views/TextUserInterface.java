package battleship.views;

import battleship.ShipPlacementException;
import battleship.entities.Coordinates;
import battleship.service.PlayersLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static battleship.utils.BattleshipUtils.closeStream;


/**
 * This class is responsible for interaction with a user using console.
 */
public class TextUserInterface implements UserInterface {
    private final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    private Map<String, String> lang = new HashMap<>();
    // coordinates values
    // x coordinates should be of type char
    private char[] xCoordinates = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
    // y coordinates should be of type int
    private int[] yCoordinates = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    private char emptyCellSign = '_';
    private char missedCellSign = '.';
    private char aliveShipCellSign = 'O';
    private char deadShipCellSign = 'X';

    // setters
    public void setLang(Map<String, String> lang) {
        this.lang = lang;
    }

    public void setXCoordinates(char[] xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public void setYCoordinates(int[] yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    public void setCellsSigns(char emptyCellSign, char missedCellSign, char aliveShipCellSign, char deadShipCellSign) {
        this.emptyCellSign = emptyCellSign;
        this.missedCellSign = missedCellSign;
        this.aliveShipCellSign = aliveShipCellSign;
        this.deadShipCellSign = deadShipCellSign;
    }

    public void printWelcomeMessage() {
        System.out.println(lang.get("welcome text"));
    }

    /**
     * Setting this player's name
     *
     * @return {@link String} name for user
     */
    public String askForUserName() throws IOException {
        // TODO replace hardcoded strings
        String name = "";
        System.out.print("Enter your name (or leave it blank): ");
        name = consoleReader.readLine();
        return name;
    }

    /**
     * Ask player for the computer's name
     *
     * @return {@link String} name for computer
     */
    public String askForComputersName() throws IOException {
        // TODO replace hardcoded strings
        System.out.print("Enter opponent's name (or leave it blank): ");
        String enemyName = consoleReader.readLine();
        System.out.println("Ok.");
        return enemyName;
    }

    public void showRules() {
        System.out.println(lang.get("Rules of placing ships"));
    }

    @Override
    public void showInputFormat() {
        System.out.println(lang.get("Show input format"));
    }

    public void gameStarted() {
        System.out.println(lang.get("Game started"));
    }

    @Override
    public Coordinates askForShipStartingCoordinate(int numberOfDecks) throws ShipPlacementException, IOException {
        String sNumberOfDecks = String.valueOf(numberOfDecks);
        if (numberOfDecks == 1) sNumberOfDecks = "single-";

        if (numberOfDecks != 1)
            System.out.print(lang.get("Ask for start point part1") + sNumberOfDecks + lang.get("Ask for start point part2"));
        else System.out.print(lang.get("Ask for single-deck"));

        return readCoordinatesFromConsole();
    }

    @Override
    public Coordinates askForShipEndingCoordinate(int numberOfDecks) throws ShipPlacementException, IOException {
        String sNumberOfDecks = String.valueOf(numberOfDecks);
        if (numberOfDecks == 1) sNumberOfDecks = "single-";

        System.out.print(lang.get("Ask for end point part1") + sNumberOfDecks + lang.get("Ask for end point part2"));

        return readCoordinatesFromConsole();
    }

    public void askToWait() {
        System.out.println(lang.get("Wait for computer ships"));
    }

    @Override
    public void drawField(PlayersLogic player, boolean drawWithMarksAroundShips) {
        boolean[][] field = player.getFieldService().getField().getFieldValues();
        int rowsNumberOfTheField = field.length;

        System.out.println(lineSeparator());

        for (int row = rowsNumberOfTheField - 1; row >= 0; row--) {
            System.out.println(
                    generateStringOfTheField(row, field, player.getPlayersShootsList(), drawWithMarksAroundShips));
        }
        System.out.println(lineSeparator());
        System.out.println(columnsNames());
    }

    @Override
    public void drawAllFields(PlayersLogic user, PlayersLogic enemy) {
        String spaceBetweenFields = " ";

        // printing player's names
        System.out.println(lang.get("Fields names1") + enemy.getPlayerName() + lang.get("Fields names2"));

        // printing line separators
        System.out.println(lineSeparator() + spaceBetweenFields + lineSeparator());

        boolean[][] userField = user.getFieldService().getField().getFieldValues();
        boolean[][] enemyField = enemy.getFieldService().getField().getFieldValues();
        int rowsNumberOfTheField = userField.length;

        // printing fields for each player line by line
        for (int row = rowsNumberOfTheField; row >= 0; row--) {
            System.out.println(generateStringOfTheField(row, userField, enemy.getPlayersShootsList(), false) +
                    spaceBetweenFields +
                    generateStringOfTheField(row, enemyField, user.getPlayersShootsList(), false));
        }

        System.out.println(lineSeparator() + spaceBetweenFields + lineSeparator());
        System.out.println(columnsNames() + spaceBetweenFields + columnsNames());
    }

    @Override
    public void showEnemyMove(Coordinates coordinates) {
        System.out.print(
                "Computer shoots at: " + xCoordinates[coordinates.getX()] + yCoordinates[coordinates.getY()] + "... ");
    }

    @Override
    public void enemyMissed(Coordinates coordinates) {
        System.out.println("But it missed. Your turn.");
    }

    @Override
    public void enemyInjuredYourShip(Coordinates coordinates) {
        System.out.println("Computer hits your ship.");
    }

    @Override
    public void enemyDestroyedYourShip(Coordinates coordinates) {
        System.out.println("Computer killed your ship.");
    }

    @Override
    public Coordinates askCoordinatesForShoot() throws ShipPlacementException, IOException {
        System.out.print("Enter coordinates for shoot: ");
        return readCoordinatesFromConsole();
    }

    @Override
    public void askToRepeatLastAction() {
        System.out.println("Ooops... Something bad happened. Could you please repeat your last action?");
    }

    @Override
    public void suchShootHasBeenMadeAlready(Coordinates coordinates) {
        System.out.println("You already shoot this cell!. Try another one.");
    }

    @Override
    public void userMissed(Coordinates coordinates) {
        System.out.println("You missed. Computer's turn.");
    }

    @Override
    public void userInjuredEnemysShip(Coordinates coordinates) {
        System.out.println("You hit computer's ship! Shoot again!");
    }

    @Override
    public void userDestroyedEnemysShip(Coordinates coordinates) {
        System.out.println("Great! You've just killed computer's ship!");
    }

    public void won(int theLongestStreak, int theNumberOfMovesPlayerDid) {
        // TODO replace hardcoded language strings
        System.out.println("You won!");
        System.out.println("You did it in " + theNumberOfMovesPlayerDid + " moves.");
        System.out.println("The longest streak of successful hits you did is " + theLongestStreak + " hits.");
    }

    public void loose(String playerName, int theLongestStreak, int theNumberOfMovesPlayerDid) {
        // TODO replace hardcoded language strings
        System.out.println(playerName + " loose.");
        System.out.println("You did only " + theNumberOfMovesPlayerDid + " moves.");
        System.out.println("The longest streak of successful hits you did is " + theLongestStreak + " hits.");
    }

    public void end() {
        closeStream(consoleReader);
    }

    private Coordinates readCoordinatesFromConsole() throws ShipPlacementException, IOException {
        Coordinates result = null;
        try {
            result = getCoordinatesFromString(consoleReader.readLine());
        } catch (ShipPlacementException e) {
            System.out.println("Bad coordinates");
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(lang.get("Abstract error"));
            throw e;
        }
        return result;
    }

    /**
     * This method will cast coordinates from "B4" view to normal one like "1, 5"
     * (equals to "B4". horizontal: A->0, B->1, ...; vertical: 1->0, 2->1, ...)
     * Or will throw an exception if coordinates are extremely wrong.
     */
    private Coordinates getCoordinatesFromString(String sCoordinates) throws ShipPlacementException {
        if (sCoordinates == null || sCoordinates.isEmpty()) throw new ShipPlacementException("Empty string");
        Coordinates result;
        if (sCoordinates.length() == 2 || sCoordinates.length() == 3) {
            try {
                char first = sCoordinates.toUpperCase().charAt(0);
                int second = Integer.parseInt(sCoordinates.substring(1));
                result = new Coordinates(getCoordinateIndex(first), getCoordinateIndex(second));
            } catch (NumberFormatException e) {
                throw new ShipPlacementException("Number format failed");
            }
        } else throw new ShipPlacementException("String length is wrong");
        return result;
    }

    private int getCoordinateIndex(char coordinate) throws ShipPlacementException {
        for (int i = 0; i < xCoordinates.length; i++) {
            if (xCoordinates[i] == coordinate) {
                return i;
            }
        }
        throw new ShipPlacementException("Coordinates are wrong or out of range");
    }

    private int getCoordinateIndex(int coordinate) throws ShipPlacementException {
        for (int i = 0; i < yCoordinates.length; i++) {
            if (yCoordinates[i] == coordinate) {
                return i;
            }
        }
        throw new ShipPlacementException("Coordinates are wrong or out of range");
    }

    private boolean ifThereAreShipsAround(boolean[][] field, Coordinates coordinates) {
        for (int i = coordinates.getY() - 1; i < coordinates.getY() + 1; i++) {
            for (int j = coordinates.getX() - 1; j < coordinates.getX() + 1; j++) {
                if (i >= 0 && j >= 0 && i < yCoordinates.length && j < xCoordinates.length) {
                    if (field[i][j]) return true;   // return true if there is a ship (boolean value is true)
                }
            }
        }
        return false;
    }

    /**
     * Constructs single line separator
     *
     * @return {@link String} of spaces and minuses that depends of the field's width
     */
    private String lineSeparator() {
        StringBuilder sb = new StringBuilder("    ");
        for (int i = 0; i < xCoordinates.length; i++) {
            sb.append("---");
        }
        return sb.toString();
    }

    /**
     * Constructs the string with column names of the field
     *
     * @return {@link String} of spaces and column names
     */
    private String columnsNames() {
        StringBuilder sb = new StringBuilder("   ");
        for (char c : xCoordinates) {
            sb.append("  ").append(c);
        }
        return sb.toString();
    }

    private String generateStringOfTheField(
            int rowIndex,
            boolean[][] fieldValues,
            Set<Coordinates> shootsPlayerDid,
            boolean drawWithMarksAroundEveryShip) {

        StringBuilder result = new StringBuilder();

        int rowsNumberOfTheField = fieldValues.length;
        int columnsNumberOfTheField = fieldValues[0].length;
        if (rowIndex < 9) System.out.print(" ");
        // System.out.print(generateSpacesForLinesIndexDigit(rowIndex));
        result.append(yCoordinates[rowIndex]).append(" |");
        for (int j = 0; j < columnsNumberOfTheField; j++) {
            Coordinates currentCoordinates = new Coordinates(j, rowIndex);
            boolean cellValue = fieldValues[rowIndex][j];
            if (cellValue) {                                        // if this cell have a ship in it
                if (shootsPlayerDid.contains(currentCoordinates)) {       // if we shoot here already
                    result.append(" ").append(deadShipCellSign).append(" ");
                } else {                                            // if we didn't shoot in this cell yet
                    result.append(" ").append(aliveShipCellSign).append(" ");
                }
            } else {                                                // if this cell DON'T have a ship in it
                if (shootsPlayerDid.contains(currentCoordinates)) {       // if we shoot here already
                    result.append(" ").append(missedCellSign).append(" ");
                } else {                                            // if we didn't shoot in this cell yet
                    if (drawWithMarksAroundEveryShip && ifThereAreShipsAround(fieldValues, currentCoordinates)) {
                        // if need marks around every ship and there is a ship around of these coordinates
                        result.append(" ").append(missedCellSign).append(" ");
                    } else {    // otherwise
                        result.append(" ").append(emptyCellSign).append(" ");
                    }
                }
            }
        }
        result.append("|");
        return result.toString();
    }

    private String generateSpacesForLinesIndexDigit(int rowIndex) {
        StringBuilder sb = new StringBuilder();
        byte digit = 1;
        for (byte i = 1; i < 100; i++) {
            if (rowIndex < 1 * Math.pow(i, 10)) {
                digit = i;
                break;
            }
        }
        for (int i = 0; i < digit; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
