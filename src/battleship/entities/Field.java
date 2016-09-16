package battleship.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a field for placing ships.
 * It's a two-dimensional array of boolean values. It's easier to imagine it like a table.
 * Every cell could be filled or empty. Filled cell (true) means that its a ship here (or ship's part, a deck).
 * Empty cell (false) means there is nothing in it.
 */
public class Field {
    /**
     * Max length of the field by X
     */
    private final int maxXFieldSize;

    /**
     * Max length of the field by Y
     */
    private final int maxYFieldSize;
    /**
     * Every ship instance with it's coordinates
     */
    private final List<Ship> ships;
    /**
     * The field itself.
     * Every cell could be filled or empty. Filled cell (true) means that its a ship here (or ship's part, a deck).
     * Empty cell (false) means there is nothing in it.
     */
    private boolean[][] field;

    /**
     * Creates a field instance
     * @param maxXFieldSize max length of the field by X
     * @param maxYFieldSize max length of the field by Y
     * @param numberOfShipsAtField max number of ships that could be placed at this field
     */
    public Field(int maxXFieldSize, int maxYFieldSize, int numberOfShipsAtField) {
        this.maxXFieldSize = maxXFieldSize;
        this.maxYFieldSize = maxYFieldSize;
        field = new boolean[maxYFieldSize][maxXFieldSize];
        ships = new ArrayList<>(numberOfShipsAtField);
    }

    public int getMaxXFieldSize() {
        return maxXFieldSize;
    }

    public int getMaxYFieldSize() {
        return maxYFieldSize;
    }

    public boolean[][] getFieldValues() {
        return field;
    }

    public List<Ship> getShips() {
        return ships;
    }
}
