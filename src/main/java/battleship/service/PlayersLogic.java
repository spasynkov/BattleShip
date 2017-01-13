package battleship.service;

import battleship.entities.Player;
import battleship.entities.PlayerStatistics;
import battleship.exceptions.ShipPlacementException;
import battleship.utils.BattleshipUtils;
import battleship.utils.Logger;
import javafx.util.Pair;

import java.util.Set;

/**
 * Abstract class that describes the logic of some player.
 */
public abstract class PlayersLogic implements PlayersActions {
    static Logger logger;
    private static BattleshipUtils utils;
    protected Player player;
    PlayerStatistics stats;
    private FieldService field;

    PlayersLogic(Player player, PlayerStatistics stats) {
        this.player = player;
        this.stats = stats;
    }

    public static void setLogger(Logger logger) {
        PlayersLogic.logger = logger;
    }

    public static void setUtils(BattleshipUtils utils) {
        PlayersLogic.utils = utils;
    }

    public Set<Pair<Integer, Integer>> getPlayersShootsList() {
        return player.getCoordinatesList();
    }

    public String getPlayerName() {
        return player.getName();
    }

    void setTheLongestStreak(int value) throws NumberFormatException {
        int fieldSize = field.getField().getMaxXFieldSize() * field.getField().getMaxYFieldSize();
        if (value > 0 && value < fieldSize) stats.setTheLongestStreak(value);
        else {
            NumberFormatException e = new NumberFormatException(
                    String.format(utils.getMessage("Value out of range exception"), value, fieldSize));
            logger.write(utils.getMessage("Value out of range log"), e);
            throw e;
        }
    }

    void incrementTheNumberOfMovesPlayerDid() {
        stats.setTheNumberOfMoves(stats.getTheNumberOfMoves() + 1);
    }

    final boolean putShipsAtField(Pair<Integer, Integer> startingCoordinates, int numberOfDecks, Pair<Integer, Integer> endingCoordinates)
            throws ShipPlacementException {

        return field.putShip(startingCoordinates.getKey(),
                startingCoordinates.getValue(),
                numberOfDecks,
                endingCoordinates.getKey(),
                endingCoordinates.getValue());
    }

    final boolean putShipsAtField(Pair<Integer, Integer> coordinates) throws ShipPlacementException {
        return field.putShip(coordinates.getKey(), coordinates.getValue());
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

    final int checkDeckAtField(Pair<Integer, Integer> coordinates) {
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
