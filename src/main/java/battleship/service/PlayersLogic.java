package battleship.service;

import battleship.entities.Coordinates;
import battleship.entities.Player;
import battleship.exceptions.ShipPlacementException;
import battleship.utils.BattleshipUtils;
import battleship.utils.Logger;

import java.util.Set;

/**
 * Abstract class that describes the logic of some player.
 */
public abstract class PlayersLogic implements PlayersActions {
    static Logger logger;
    private static BattleshipUtils utils;
    protected Player player;
    private FieldService field;
    private int theLongestStreak = 0;
    private int theNumberOfMovesPlayerDid = 0;

    PlayersLogic(Player player) {
        this.player = player;
    }

    public static void setLogger(Logger logger) {
        PlayersLogic.logger = logger;
    }

    public static void setUtils(BattleshipUtils utils) {
        PlayersLogic.utils = utils;
    }

    public Set<Coordinates> getPlayersShootsList() {
        return player.getCoordinatesList();
    }

    public String getPlayerName() {
        return player.getName();
    }

    public final int getTheLongestStreak() {
        return theLongestStreak;
    }

    void setTheLongestStreak(int value) throws NumberFormatException {
        int fieldSize = field.getField().getMaxXFieldSize() * field.getField().getMaxYFieldSize();
        if (value > 0 && value < fieldSize) theLongestStreak = value;
        else {
            NumberFormatException e = new NumberFormatException(
                    String.format(utils.getMessage("Value out of range exception"), value, fieldSize));
            logger.write(utils.getMessage("Value out of range log"), e);
            throw e;
        }
    }

    public final int getTheNumberOfMovesPlayerDid() {
        return theNumberOfMovesPlayerDid;
    }

    void incrementTheNumberOfMovesPlayerDid() {
        theNumberOfMovesPlayerDid++;
    }

    final boolean putShipsAtField(Coordinates startingCoordinates, int numberOfDecks, Coordinates endingCoordinates)
            throws ShipPlacementException {

        return field.putShip(startingCoordinates.getX(),
                startingCoordinates.getY(),
                numberOfDecks,
                endingCoordinates.getX(),
                endingCoordinates.getY());
    }

    final boolean putShipsAtField(Coordinates coordinates) throws ShipPlacementException {
        return field.putShip(coordinates.getX(), coordinates.getY());
    }

    public final boolean isMoreShips() {
        return player.getField().getShips().size() > 0;
    }

    public FieldService getFieldService() {
        return field;
    }

    public final void setField(FieldService fieldService) {
        this.field = fieldService;
    }

    final int checkDeckAtField(Coordinates coordinates) {
        return field.hitShipAtField(coordinates);
    }

    /*
    protected static void closeStream(Closeable object) {
        BattleshipUtils.closeStream(object);
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
    */
}
