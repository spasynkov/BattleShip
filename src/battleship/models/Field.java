package battleship.models;

import battleship.ShipPlacementException;
import battleship.service.ShipService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will be responsible for controlling each field's logic.
 * As we will have at least 2 instances of this class (player's field and enemy's/computer's field)
 * so this class have both methods for 2 different kinds of logic.
 * I will try to split computer's logic and UI's one at 2 different classes... But it will be here now for some time :)
 */
public class Field {
    /**
     * How not empty cells (with ships) will look
     */
    private static final char filledCell = 'O';

    /**
     * How empty cells will look
     */
    private static final char emptyCell = '.';

    /**
     * How will look those cells where it was a hit but was'nt any ships there.
     * You will get "missed" message when hit those cells.
     */
    private static final char missedSign = '*';

    /**
     * How will look cells with hited ships/decks
     */
    private static final char hitedSign = 'X';

    /**
     * Battlefield initialisation
     */
    private final char[][] field = new char[10][10];

    /**
     * Every ship instance with it's coordinates
     */
    private final HashMap<Ship, String> ships = new HashMap<>(10);

    /**
     * Default and the only one constructor.
     * The only thing it doing is filling every cells with "-" sign for easier ships placement planning.
     */
    public Field() {
        // TODO add getting symbols like . * O X from properties
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field[i][j] = emptyCell;
            }
        }
    }

    /**
     * @return char value of filled cell
     */
    public static char getFilledCell() {
        return filledCell;
    }

    /**
     * @return char value of empty cell
     */
    public static char getEmptyCell() {
        return emptyCell;
    }

    /**
     * Returns {@code char} array with all cells in some particular row of the field.
     * Method takes the index of field's row to return.
     */
    char[] getLine(int row) {
        char[] result = new char[field[row].length];
        System.arraycopy(field[row], 0, result, 0, result.length);
        return result;
    }

    /**
     * Clears field. Filling every "empty" cell with an "emptyCell" sign;
     */
    public void clear() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (field[i][j] != filledCell) field[i][j] = emptyCell;
            }
        }
    }

    public char getCell(int x, int y) {
        return field[y][x];
    }

    /**
     * This method will check if some ship you're trying to put could be placed. It will return "true" if it's ok.
     * Takes the number of decks of that ship you're trying to put at field and starting coordinates and ending ones.
     *
     * There is no difference what coordinates will be grater than other.
     * It will convert it so you don't need to think about this.
     * If coordinates are wrong - it will @throw ShipPlacementException with a short reason message in it.
     */
    public boolean putShip(int startX, int startY, int numberOfDecks, int endX, int endY) throws ShipPlacementException {
        if (startX > 9 || startY > 9 || endX > 9 || endY > 9) throw new ShipPlacementException("Coordinates out of range");
        if (Math.abs(startX - endX) != numberOfDecks ^ Math.abs(startY - endY) != numberOfDecks) {
            throw new ShipPlacementException("Wrong size");
        }

        boolean xDirection = false;
        boolean yDirection = false;
        if (startX == endX) yDirection = true;
        if (startY == endY) xDirection = true;

        if (xDirection && yDirection) throw new ShipPlacementException("Not single-deck");
        else if (!xDirection && !yDirection) throw new ShipPlacementException("Wrong placement");
        else {
            int startCell;
            int endCell;

            if (xDirection) {
                startCell = Math.min(startX, endX);
                endCell = startCell + numberOfDecks;
            } else {
                startCell = Math.min(startY, endY);
                endCell = startCell + numberOfDecks;
            }

            /*
             * Checking by every deck before filling field
             */
            for (int i = startCell; i < endCell; i++) {
                if (xDirection) checkIfCloseByXY(i, startY);
                else checkIfCloseByXY(startX, i);
            }

            /*
             * Adding new ship to list and filling field with dots and 8's
             */
            Ship ship = ShipService.placeShip(Math.min(startX, endX), Math.min(startY, endY), numberOfDecks, Math.max(startX, endX), Math.max(startY, endY));
            int[][] coordinates = ShipService.getCoordinatesOfTheShip(ship);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numberOfDecks; i++) {
                int x = coordinates[i][0];
                int y = coordinates[i][1];
                sb.append("(").append(x).append(",").append(y).append(")");
                field[y][x] = filledCell;
                surroundWithDots(x, y);
            }
            ships.put(ship, sb.toString());
        }
        return true;
    }

    /**
     * This method will check if some ship you're trying to put could be placed. It will return "true" if it's ok.
     * Works only for ships with 1 deck. Takes coordinates of that ship you're trying to put at field.
     *
     * If coordinates are wrong - it will @throw ShipPlacementException with a short reason message in it.
     */
    public boolean putShip(int x, int y) throws ShipPlacementException {
        checkIfCloseByXY(x, y);
        Ship ship = ShipService.placeShip(x, y);
        ships.put(ship, "(" + x + "," + y + ")");
        this.field[y][x] = filledCell;
        surroundWithDots(x, y);
        return true;
    }

    /**
     * Checking if there any other ships close to that one you're trying to put.
     * Returns nothing except new exceptions for you to work with :)
     */
    private void checkIfCloseByXY(int x, int y) throws ShipPlacementException {
        ShipPlacementException e = new ShipPlacementException("Too close to other ships");
        if (x > 0) {
            if (y - 1 >= 0 && this.field[y - 1][x - 1] == filledCell) throw e;
            if (this.field[y][x - 1] == filledCell) throw e;
            if (y + 1 < 10 && this.field[y + 1][x - 1] == filledCell) throw e;
        }
        if (x < 9) {
            if (y - 1 >= 0 && this.field[y - 1][x + 1] == filledCell) throw e;
            if (this.field[y][x + 1] == filledCell) throw e;
            if (y + 1 < 10 && this.field[y + 1][x + 1] == filledCell) throw e;
        }
        if (y - 1 >= 0 && this.field[y - 1][x] == filledCell) throw e;
        if (this.field[y][x] == filledCell) throw new ShipPlacementException("Cell isn't empty");
        if (y + 1 < 10 && this.field[y + 1][x] == filledCell) throw e;
    }

    /**
     * Draws dots around some coordinates that it takes. Draws only in empty cells.
     */
    private void surroundWithDots(int x, int y) {
        if (x > 0) {
            if (y - 1 >= 0 && this.field[y - 1][x - 1] != filledCell && this.field[y - 1][x - 1] != hitedSign) this.field[y - 1][x - 1] = missedSign;
            if (this.field[y][x - 1] != filledCell && this.field[y][x - 1] != hitedSign) this.field[y][x - 1] = missedSign;
            if (y + 1 < 10 && this.field[y + 1][x - 1] != filledCell && this.field[y + 1][x - 1] != hitedSign) this.field[y + 1][x - 1] = missedSign;
        }
        if (x < 9) {
            if (y - 1 >= 0 && this.field[y - 1][x + 1] != filledCell && this.field[y - 1][x + 1] != hitedSign) this.field[y - 1][x + 1] = missedSign;
            if (this.field[y][x + 1] != filledCell && this.field[y][x + 1] != hitedSign) this.field[y][x + 1] = missedSign;
            if (y + 1 < 10 && this.field[y + 1][x + 1] != filledCell && this.field[y + 1][x + 1] != hitedSign) this.field[y + 1][x + 1] = missedSign;
        }
        if (y - 1 >= 0 && this.field[y - 1][x] != filledCell && this.field[y - 1][x] != hitedSign) this.field[y - 1][x] = missedSign;
        if (y + 1 < 10 && this.field[y + 1][x] != filledCell && this.field[y + 1][x] != hitedSign) this.field[y + 1][x] = missedSign;

    }

    public int getShipsNumber() {
        return ships.size();
    }

    public int[][] getEmptyCells() {
        ArrayList<int[]> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (field[j][i] == emptyCell || field[j][i] == filledCell) list.add(new int[]{i, j});
            }
        }
        int[][] result = new int[list.size()][2];
        list.toArray(result);
        return result;
    }

    public int checkDeckAtField(int x, int y) {
        if (field[y][x] == filledCell) {
            // hit
            field[y][x] = hitedSign;
            HashMap<Ship, String> copy = new HashMap<>(ships);
            for (Map.Entry<Ship, String> entry : copy.entrySet()) {
                if (entry.getValue().contains(String.valueOf(x) + "," + String.valueOf(y))) {
                    Ship ship = entry.getKey();
                    ShipService.hit(ship);
                    if (!ShipService.isShipAlive(ship)) {
                        boolean stop = false;
                        String s = entry.getValue();
                        do {
                            if (s.length() > 4) {
                                int i = Integer.parseInt(String.valueOf(s.charAt(1)));
                                int j = Integer.parseInt(String.valueOf(s.charAt(3)));
                                surroundWithDots(i, j);
                                s = s.substring(5);
                            } else stop = true;
                        } while(!stop);
                        ships.remove(entry.getKey());
                        return 2;
                    }
                }
            }
            return 1;
        } else if (field[y][x] == emptyCell) {
            // miss
            field[y][x] = missedSign;
            return 0;
        } else return -1;   // Already
    }
}
