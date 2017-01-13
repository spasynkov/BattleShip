package battleship.service;

import battleship.entities.Player;
import battleship.entities.PlayerStatistics;
import battleship.exceptions.ShipPlacementException;
import battleship.utils.BattleshipUtils;
import battleship.utils.Logger;
import javafx.util.Pair;

import java.util.List;
import java.util.Set;

/**
 * Abstract class that describes the logic of some player.
 */
public abstract class PlayersLogicAbstractImpl implements PlayersActions, PlayersLogic {
    static Logger logger;

    private static BattleshipUtils utils;
    protected Player player;
    PlayerStatistics stats;
    private FieldService field;

    PlayersLogicAbstractImpl(Player player, PlayerStatistics stats) {
        this.player = player;
        this.stats = stats;
    }

    public static void setLogger(Logger logger) {
        PlayersLogicAbstractImpl.logger = logger;
    }

    public static void setUtils(BattleshipUtils utils) {
        PlayersLogicAbstractImpl.utils = utils;
    }

    @Override
    public Set<Pair<Integer, Integer>> getPlayersShootsList() {
        return player.getCoordinatesList();
    }

    @Override
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

    @Override
    public List<Pair<Integer, Integer>> getEmptyCells() {
        return field.getEmptyCells();
    }

    @Override
    public boolean[][] getFieldValues() {
        return field.getFieldValues();
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

    FieldService getFieldService() {
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
