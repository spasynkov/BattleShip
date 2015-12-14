package battleship;

import java.io.Closeable;

/**
 * Abstract class that describes some players logic.
 * UserInterface and MachineLogic inherits this class.
 */
abstract class Player {
    private String name;
    protected static final Logger log = Logger.getInstance();
    protected final Field field = new Field();

    private int theLongestStreak = 0;
    private int theNumberOfMovesPlayerDid = 0;

    protected Player() {
        super();
    }

    protected Player(String name) {
        this.name = name;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        if (name != null && !name.isEmpty()) this.name = name;
    }

    protected char getCell(int x, int y) {
        char result = field.getCell(x, y);
        if (result == Field.getFilledCell()) result = Field.getEmptyCell();
        return result;
    }

    protected int[][] getEmptyCells() {
        return field.getEmptyCells();
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

    abstract void placeShips();

    abstract void placeShipByDeckNumber(int numberOfDecks);

    protected final boolean isMoreShips() {
        return field.getShipsNumber() > 0;
    }

    abstract int beingAttacked(int x, int y);

    abstract void makeShoot(Player enemy);

    protected void won() {
        log.write("User won. Moves = " + theNumberOfMovesPlayerDid + ", the longest streak = " + theLongestStreak);
    }

    protected static void closeStream(Closeable object) {
        BattleshipUtils.closeStream(object);
    }
}
