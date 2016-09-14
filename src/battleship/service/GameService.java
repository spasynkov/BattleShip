package battleship.service;

import battleship.ShipPlacementException;
import battleship.entities.Field;
import battleship.entities.Player;
import battleship.utils.BattleshipUtils;
import battleship.utils.Logger;

import java.io.Closeable;

/**
 * Abstract class that describes the logic of the game.
 */
public abstract class GameService implements GameLogic {
    protected static Logger logger;
    private final Field field = new Field();
    private Player player;
    private int theLongestStreak;
    private int theNumberOfMovesPlayerDid;

    public GameService() {
        theLongestStreak = 0;
        theNumberOfMovesPlayerDid = 0;
    }

    public GameService(Player player) {
        this();
        this.player = player;
    }

    public static void setLogger(Logger logger) {
        GameService.logger = logger;
    }

    protected static void closeStream(Closeable object) {
        BattleshipUtils.closeStream(object);
    }

    public String getPlayerName() {
        return player.getName();
    }

    public final char getCell(int x, int y) {
        return field.getCell(x, y);
    }

    public final char getCellSafe(int x, int y) {
        char result = field.getCell(x, y);
        if (result == Field.getFilledCell()) result = Field.getEmptyCell();
        return result;
    }

    public final int[][] getEmptyCells() {
        return field.getEmptyCells();
    }

    protected final boolean isAvailableForShoot(char symbolInCell) {
        return (symbolInCell == Field.getEmptyCell() || symbolInCell == Field.getFilledCell());
    }

    protected final int getTheLongestStreak() {
        return theLongestStreak;
    }

    protected void setTheLongestStreak(int value) throws NumberFormatException {
        int fieldSize = 10*10;  // TODO Get property from Field class
        if (value > 0 && value < fieldSize) theLongestStreak = value;
        else {
            NumberFormatException e =
                    new NumberFormatException("Value \'" + value + "\' is out of range (<1 or >" + fieldSize + ").");
            logger.write("New value of the longest streak is out of range.", e);
            throw e;
        }
    }

    protected final int getTheNumberOfMovesPlayerDid() {
        return theNumberOfMovesPlayerDid;
    }

    protected void incrementTheNumberOfMovesPlayerDid() {
        theNumberOfMovesPlayerDid++;
    }

    protected final boolean putShipsAtField(int startX, int startY, int numberOfDecks, int endX, int endY)
            throws ShipPlacementException {

        return field.putShip(startX, startY, numberOfDecks, endX, endY);
    }

    protected final boolean putShipsAtField(int startX, int startY) throws ShipPlacementException {
        return field.putShip(startX, startY);
    }

    protected final void clearField() {
        field.clear();
    }

    protected final int checkDeckAtField(int x, int y) {
        return field.checkDeckAtField(x, y);
    }

    public final boolean isMoreShips() {
        return field.getShipsNumber() > 0;
    }

    protected void won() {
        logger.write("User won. Moves = " + theNumberOfMovesPlayerDid + ", the longest streak = " + theLongestStreak);
    }
}
