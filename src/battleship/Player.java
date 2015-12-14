package battleship;

import java.io.Closeable;

/**
 * Abstract class that describes some player's logic.
 * UserInterface and MachineLogic inherits this class.
 */
abstract class Player {
    private String name;
    private int theLongestStreak = 0;
    private int theNumberOfMovesPlayerDid = 0;

    protected static final Logger log = Logger.getInstance();
    protected final Field field = new Field();

    abstract void placeShips();
    abstract void placeShipByDeckNumber(int numberOfDecks);
    abstract int beingAttacked(int x, int y);
    abstract void makeShoot(Player enemy);

    protected Player() {
        super();
    }

    protected Player(String name) {
        this.name = name;
    }

    protected final String getName() {
        return name;
    }

    protected final void setName(String name) {
        if (name != null && !name.isEmpty()) this.name = name;
    }

    protected final char getCell(int x, int y) {
        char result = field.getCell(x, y);
        if (result == Field.getFilledCell()) result = Field.getEmptyCell();
        return result;
    }

    protected final int[][] getEmptyCells() {
        return field.getEmptyCells();
    }

    protected final boolean isAvailableForShoot(char symbolInCell) {
        return symbolInCell == Field.getEmptyCell();
    }

    protected final int getTheLongestStreak() {
        return theLongestStreak;
    }

    protected final int getTheNumberOfMovesPlayerDid() {
        return theNumberOfMovesPlayerDid;
    }

    protected void setTheLongestStreak(int value) throws NumberFormatException {
        int fieldSize = 10*10;  // TODO Get property from Field class
        if (value > 0 && value < fieldSize) theLongestStreak = value;
        else {
            NumberFormatException e = new NumberFormatException("Value \'" + value + "\' is out of range (<1 or >" + fieldSize + ").");
            log.write("New value of the longest streak is out of range.", e);
            throw e;
        }
    }

    protected void incrementTheNumberOfMovesPlayerDid() {
        theNumberOfMovesPlayerDid++;
    }

    protected final boolean putShipsAtField(int startX, int startY, int numberOfDecks, int endX, int endY) throws ShipPlacementException {
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

    protected final boolean isMoreShips() {
        return field.getShipsNumber() > 0;
    }

    protected void won() {
        log.write("User won. Moves = " + theNumberOfMovesPlayerDid + ", the longest streak = " + theLongestStreak);
    }

    protected static void closeStream(Closeable object) {
        BattleshipUtils.closeStream(object);
    }
}
