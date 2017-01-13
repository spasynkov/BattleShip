package battleship.views;

import battleship.entities.Coordinates;
import battleship.exceptions.ShipPlacementException;
import battleship.service.PlayersLogic;
import battleship.utils.BattleshipUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * This class is responsible for interaction with a user using console.
 */
public class TextUserInterface implements UserInterface {
    private final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    private BattleshipUtils utils;
    // coordinates values
    // x coordinates should be of type char
    private char[] xCoordinatesNames = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
    // y coordinates should be of type int
    private int[] yCoordinatesNames = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    private char emptyCellSign = '.';
    private char missedCellSign = '*';
    private char aliveShipCellSign = 'O';
    private char deadShipCellSign = 'X';

    // setters
    public void setUtils(BattleshipUtils utils) {
        this.utils = utils;
    }

    public void setXCoordinates(char[] xCoordinates) {
        this.xCoordinatesNames = xCoordinates;
    }

    public void setYCoordinates(int[] yCoordinates) {
        this.yCoordinatesNames = yCoordinates;
    }

    public void setCellsSigns(char emptyCellSign, char missedCellSign, char aliveShipCellSign, char deadShipCellSign) {
        this.emptyCellSign = emptyCellSign;
        this.missedCellSign = missedCellSign;
        this.aliveShipCellSign = aliveShipCellSign;
        this.deadShipCellSign = deadShipCellSign;
    }

    public void printWelcomeMessage() {
        System.out.println(utils.getMessage("Welcome text"));
    }

    /**
     * Setting this player's name
     *
     * @return {@link String} name for user
     */
    public String askForUserName() throws IOException {
        System.out.print(utils.getMessage("Ask for user name"));
        return consoleReader.readLine();
    }

    /**
     * Ask player for the computer's name
     *
     * @return {@link String} name for computer
     */
    public String askForComputersName() throws IOException {
        System.out.print(utils.getMessage("Ask for enemy name"));
        return consoleReader.readLine();
    }

    public void showRules() {
        System.out.println(utils.getMessage("Rules of placing ships"));
    }

    @Override
    public void showInputFormat() {
        System.out.println(utils.getMessage("Show input format"));
    }

    public void gameStarted() {
        System.out.println(utils.getMessage("Game started"));
    }

    @Override
    public Coordinates askForShipStartingCoordinate(int numberOfDecks) throws ShipPlacementException, IOException {
        String sNumberOfDecks = String.valueOf(numberOfDecks);
        if (numberOfDecks != 1) {
            System.out.print(String.format(utils.getMessage("Ask for start point"), sNumberOfDecks));
        } else System.out.print(utils.getMessage("Ask for single-deck"));

        return readCoordinatesFromConsole();
    }

    @Override
    public Coordinates askForShipEndingCoordinate(int numberOfDecks) throws ShipPlacementException, IOException {
        String sNumberOfDecks = String.valueOf(numberOfDecks);
        System.out.print(String.format(utils.getMessage("Ask for end point"), sNumberOfDecks));

        return readCoordinatesFromConsole();
    }

    public void askToWait() {
        System.out.println(utils.getMessage("Wait for computer ships"));
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
        System.out.println(utils.getMessage("Fields names1")
                + enemy.getPlayerName()
                + utils.getMessage("Fields names2"));

        // printing line separators
        System.out.println(lineSeparator() + spaceBetweenFields + " " + lineSeparator());

        boolean[][] userField = user.getFieldService().getField().getFieldValues();
        boolean[][] enemyField = enemy.getFieldService().getField().getFieldValues();
        int rowsNumberOfTheField = userField.length;

        // printing fields for each player line by line
        for (int row = rowsNumberOfTheField - 1; row >= 0; row--) {
            System.out.println(generateStringOfTheField(row, userField, enemy.getPlayersShootsList(), false) +
                    spaceBetweenFields +
                    generateStringOfTheField(row, enemyField, user.getPlayersShootsList(), false));
        }

        System.out.println(lineSeparator() + spaceBetweenFields + " " + lineSeparator());
        System.out.println(columnsNames() + spaceBetweenFields + "  " + columnsNames());
    }

    @Override
    public void showEnemyMove(Coordinates coordinates) {
        System.out.print(
                String.format(
                        utils.getMessage("Enemt shoots"),
                        xCoordinatesNames[coordinates.getX()] + yCoordinatesNames[coordinates.getY()]));
    }

    @Override
    public void enemyMissed(Coordinates coordinates) {
        System.out.println(utils.getMessage("Enemy missed"));
    }

    @Override
    public void enemyInjuredYourShip(Coordinates coordinates) {
        System.out.println(utils.getMessage("Enemy hits"));
    }

    @Override
    public void enemyDestroyedYourShip(Coordinates coordinates) {
        System.out.println(utils.getMessage("Enemy kills"));
    }

    @Override
    public Coordinates askCoordinatesForShoot() throws ShipPlacementException, IOException {
        System.out.print(utils.getMessage("Ask for coordinates for shoot"));
        return readCoordinatesFromConsole();
    }

    @Override
    public void askToRepeatLastAction() {
        System.out.println(utils.getMessage("Abstract error"));
    }

    @Override
    public void suchShootHasBeenMadeAlready(Coordinates coordinates) {
        System.out.println(utils.getMessage("Repeated shoot"));
    }

    @Override
    public void userMissed(Coordinates coordinates) {
        System.out.println(utils.getMessage("You missed"));
    }

    @Override
    public void userInjuredEnemysShip(Coordinates coordinates) {
        System.out.println(utils.getMessage("You hit"));
    }

    @Override
    public void userDestroyedEnemysShip(Coordinates coordinates) {
        System.out.println(utils.getMessage("You killed"));
    }

    public void won(int theLongestStreak, int theNumberOfMovesPlayerDid) {
        System.out.println(
                String.format(
                        utils.getMessage("You won"),
                        theNumberOfMovesPlayerDid, theLongestStreak));
    }

    public void loose(String playerName, int theLongestStreak, int theNumberOfMovesPlayerDid) {
        System.out.println(
                String.format(
                        utils.getMessage("You loose"),
                        playerName, theNumberOfMovesPlayerDid, theLongestStreak));
    }

    public void end() {
        utils.closeStream(consoleReader);
    }

    private Coordinates readCoordinatesFromConsole() throws ShipPlacementException, IOException {
        String sCoordinates = "";
        Coordinates result;
        try {
            sCoordinates = consoleReader.readLine();
            result = getCoordinatesFromString(sCoordinates);
        } catch (ShipPlacementException e) {
            System.out.println(
                    String.format(
                            utils.getMessage("Bad coordinates"),
                            sCoordinates, e.getMessage()));
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(utils.getMessage("Abstract error"));
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
        if (sCoordinates == null || sCoordinates.isEmpty()) {
            throw new ShipPlacementException(utils.getMessage("Empty string"));
        }

        Coordinates result;
        if (sCoordinates.length() == 2 || sCoordinates.length() == 3) {
            try {
                char first = sCoordinates.toUpperCase().charAt(0);
                int second = Integer.parseInt(sCoordinates.substring(1));
                result = new Coordinates(getCoordinateIndex(first), getCoordinateIndex(second));
            } catch (NumberFormatException e) {
                throw new ShipPlacementException(utils.getMessage("Number format failed"));
            }
        } else throw new ShipPlacementException(utils.getMessage("String length is wrong"));
        return result;
    }

    private int getCoordinateIndex(char coordinate) throws ShipPlacementException {
        for (int i = 0; i < xCoordinatesNames.length; i++) {
            if (xCoordinatesNames[i] == coordinate) {
                return i;
            }
        }
        throw new ShipPlacementException(utils.getMessage("Coordinates are wrong or out of range"));
    }

    private int getCoordinateIndex(int coordinate) throws ShipPlacementException {
        for (int i = 0; i < yCoordinatesNames.length; i++) {
            if (yCoordinatesNames[i] == coordinate) {
                return i;
            }
        }
        throw new ShipPlacementException(utils.getMessage("Coordinates are wrong or out of range"));
    }

    private boolean ifThereAreShipsAround(boolean[][] field, Coordinates coordinates) {
        for (int i = coordinates.getY() - 1; i <= coordinates.getY() + 1; i++) {
            for (int j = coordinates.getX() - 1; j <= coordinates.getX() + 1; j++) {
                if (i >= 0 && j >= 0 && i < yCoordinatesNames.length && j < xCoordinatesNames.length) {
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
        String spaces = "    ";
        String delimiter = "---";
        StringBuilder sb = new StringBuilder(spaces);
        for (char xCoordinatesName : xCoordinatesNames) {
            sb.append(delimiter);
        }
        return sb.toString();
    }

    /**
     * Constructs the string with column names of the field
     *
     * @return {@link String} of spaces and column names
     */
    private String columnsNames() {
        String spaces = "  ";
        String moreSpaces = "   ";
        StringBuilder sb = new StringBuilder(moreSpaces);

        for (char c : xCoordinatesNames) {
            sb.append(spaces).append(c);
        }
        return sb.toString();
    }

    private String generateStringOfTheField(
            int rowIndex,
            boolean[][] fieldValues,
            Set<Coordinates> shootsPlayerDid,
            boolean drawWithMarksAroundEveryShip) {

        StringBuilder result = new StringBuilder();

        String space = " ";
        String leftDelimiter = " |";
        String delimiter = "|";

        int rowsNumberOfTheField = fieldValues.length;
        int columnsNumberOfTheField = fieldValues[0].length;
        if (rowIndex < 9) result.append(space);
        // System.out.print(generateSpacesForLinesIndexDigit(rowIndex));
        result.append(yCoordinatesNames[rowIndex]).append(leftDelimiter);
        for (int j = 0; j < columnsNumberOfTheField; j++) {
            Coordinates currentCoordinates = new Coordinates(j, rowIndex);
            boolean cellValue = fieldValues[rowIndex][j];
            if (cellValue) {                                        // if this cell have a ship in it
                if (shootsPlayerDid.contains(currentCoordinates)) {       // if we shoot here already
                    result.append(space).append(deadShipCellSign).append(space);
                } else {                                            // if we didn't shoot in this cell yet
                    result.append(space).append(aliveShipCellSign).append(space);
                }
            } else {                                                // if this cell DON'T have a ship in it
                if (shootsPlayerDid.contains(currentCoordinates)) {       // if we shoot here already
                    result.append(space).append(missedCellSign).append(space);
                } else {                                            // if we didn't shoot in this cell yet
                    if (drawWithMarksAroundEveryShip && ifThereAreShipsAround(fieldValues, currentCoordinates)) {
                        // if need marks around every ship and there is a ship around of these coordinates
                        result.append(space).append(missedCellSign).append(space);
                    } else {    // otherwise
                        result.append(space).append(emptyCellSign).append(space);
                    }
                }
            }
        }
        result.append(delimiter);
        return result.toString();
    }

    private String generateSpacesForLinesIndexDigit(int rowIndex) {
        StringBuilder sb = new StringBuilder();
        String space = " ";

        byte digit = 1;
        for (byte i = 1; i < 100; i++) {
            if (rowIndex < 1 * Math.pow(i, 10)) {
                digit = i;
                break;
            }
        }
        for (int i = 0; i < digit; i++) {
            sb.append(space);
        }
        return sb.toString();
    }
}
