package battleship;

import java.util.HashMap;

/**
 * This class will be responsible for controlling each field's logic.
 * As we will have at least 2 instances of this class (player's field and enemy's/computer's field)
 * so this class have both methods for 2 different kinds of logic.
 * I will try to split player's logic and UI's one at 2 different classes... But it will be here now for some time :)
 */
class Field {
    /**
     * How not empty cells (with ships) will look
     */
    private static final char filledCell = '8';

    /**
     * How empty cells will look
     */
    private static final char emptyCell = ' ';

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
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field[i][j] = '-';
            }
        }
    }

    /**
     * Returns {@code String} view of some particular string of the field.
     * Method takes the index of field's string to return.
     */
    public String getLine(int index) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < field[index].length; i++) {
            sb.append(" ").append(field[index][i]).append(" ");
        }
        return sb.append("|").toString();
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

    /**
     * Call this if you need to draw full field.
     */
    public void draw() {
        System.out.println("   --------------------------------");
        for (int i = 9; i >= 0; i--) {
            if (i != 9) System.out.print(" ");
            System.out.print((i + 1) + " |");
            for (int j = 0; j < 10; j++) {
                System.out.print(" " + field[i][j] + " ");
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("   --------------------------------");
        System.out.println("     A  B  C  D  E  F  G  H  I  J");
    }

    /**
     * This method will check if some ship you're trying to put could be placed. It will return "true" if it's ok.
     * Takes the number of decks of that ship you're trying to put at field and starting coordinates and ending ones.
     * Coordinates should be in format B4.
     *
     * There is no difference which ones will be grater than other. It will convert it so you don't need to think about this.
     * If coordinates are wrong - it will @throw ShipPlacementException with a short reason message in it.
     */
    public boolean putShip(int numberOfDecks, String startingCoordinates, String endingCoordinates) throws ShipPlacementException {
        int[] start = getCoordinatesFromString(startingCoordinates);
        int[] end = getCoordinatesFromString(endingCoordinates);
        if (start == null || end == null) return false;
        int startX = start[0];
        int startY = start[1];
        int endX = end[0];
        int endY = end[1];

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

            /**
             * Checking before filling field
             */
            for (int i = startCell; i < endCell; i++) {
                if (xDirection) checkIfCloseByXY(i, startY);
                else checkIfCloseByXY(startX, i);
            }

            /**
             * Adding new ship to list and filling field with dots and 8's
             */
            Ship ship = new Ship(Math.min(startX, endX), Math.min(startY, endY), numberOfDecks, Math.max(startX, endX), Math.max(startY, endY));
            int[][] coordinates = ship.getCoordinates();
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
     * Coordinates should be in format B4.
     *
     * If coordinates are wrong - it will @throw ShipPlacementException with a short reason message in it.
     */
    public boolean putShip(String coordinates) throws ShipPlacementException {
        int[] start = getCoordinatesFromString(coordinates);
        if (start == null) return false;
        checkIfCloseByXY(start[0], start[1]);
        Ship ship = new Ship(start[0], start[1]);
        ships.put(ship, "(" + start[0] + "," + start[1] + ")");
        this.field[start[1]][start[0]] = filledCell;
        surroundWithDots(start[0], start[1]);
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
            if (y - 1 >= 0 && this.field[y - 1][x - 1] != filledCell) this.field[y - 1][x - 1] = '.';
            if (this.field[y][x - 1] != filledCell) this.field[y][x - 1] = '.';
            if (y + 1 < 10 && this.field[y + 1][x - 1] != filledCell) this.field[y + 1][x - 1] = '.';
        }
        if (x < 9) {
            if (y - 1 >= 0 && this.field[y - 1][x + 1] != filledCell) this.field[y - 1][x + 1] = '.';
            if (this.field[y][x + 1] != filledCell) this.field[y][x + 1] = '.';
            if (y + 1 < 10 && this.field[y + 1][x + 1] != filledCell) this.field[y + 1][x + 1] = '.';
        }
        if (y - 1 >= 0 && this.field[y - 1][x] != filledCell) this.field[y - 1][x] = '.';
        if (y + 1 < 10 && this.field[y + 1][x] != filledCell) this.field[y + 1][x] = '.';

    }

    /**
     * This method will cast coordinates from "B4" view to normal one like "1, 5"
     * (equals to "B4". horizontal: A->0, B->1, ...; vertical: 1->0, 2->1, ...)
     * Or will throw an exception if coordinates are extremely wrong.
     */
    private int[] getCoordinatesFromString(String sCoordinates) throws ShipPlacementException {
        int[] result;
        if (sCoordinates.length() == 2 || sCoordinates.length() == 3) {
            try {
                char first = sCoordinates.toUpperCase().charAt(0);
                int second = Integer.parseInt(sCoordinates.substring(1));
                if (first >= 'A' && first <= 'J' &&
                        second > 0 && second < 11) {
                    result = new int[]{first - 'A', second - 1};
                } else throw new ShipPlacementException("Coordinates out of range");
            } catch (NumberFormatException e) {
                throw new ShipPlacementException("Number format failed");
            }
        } else throw new ShipPlacementException("String too long");
        return result;
    }
}
