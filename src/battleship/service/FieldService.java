package battleship.service;

import battleship.ShipPlacementException;
import battleship.entities.Coordinates;
import battleship.entities.Field;
import battleship.entities.Ship;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores the logic for operating with {@link battleship.entities.Field} objects
 */
public class FieldService {
    /**
     * Clears the field
     */
    public static void clear(Field field) {
        boolean[][] values = field.getField();
        for (int i = 0; i < field.getMaxYFieldSize(); i++) {
            for (int j = 0; j < field.getMaxXFieldSize(); j++) {
                values[i][j] = false;
            }
        }
    }

    public static boolean getCell(Field field, Coordinates coordinates) {
        return field.getField()[coordinates.getY()][coordinates.getX()];
    }

    public static boolean getCell(Field field, int x, int y) {
        return field.getField()[y][x];
    }

    public static boolean[] getLine(Field field, int row) {
        boolean[] result = new boolean[field.getMaxXFieldSize()];
        System.arraycopy(field.getField()[field.getMaxXFieldSize()], 0, result, 0, result.length);
        return result;
    }

    public static int getShipsNumber(Field field) {
        return field.getShips().size();
    }

    public static Set<Coordinates> getEmptyCells(Field field) {
        Set<Coordinates> result = new HashSet<>();
        boolean[][] values = field.getField();
        for (int i = 0; i < field.getMaxYFieldSize(); i++) {
            for (int j = 0; j < field.getMaxXFieldSize(); j++) {
                if (!values[i][j]) result.add(new Coordinates(j, i));
            }
        }
        return result;
    }

    // TODO: rename at "hitShipAtField"
    public static int checkDeckAtField(Field field, Coordinates coordinates) {
        if (field.getField()[coordinates.getY()][coordinates.getX()]) {
            // hit! lets check if if was the last deck of the ship
            Ship ship = findShipByCoordinates(field.getShips(), coordinates);
            ShipService.hit(ship);  // boom!
            // now lets check if it's still alive
            if (ShipService.isShipAlive(ship)) {
                return 1;           // partial success
            } else {
                return 2;           // full success
            }
        }
        // TODO: find where -1 is needed
        return 0;   // miss
    }

    /**
     * Looking for the ship with needed coordinates
     *
     * @param ships       {@link List} of ships
     * @param coordinates needed coordinates
     * @return link at {@link battleship.entities.Ship} we found,
     * or <code>null</code> if there are no ships with these coordinates
     */
    private static Ship findShipByCoordinates(List<Ship> ships, Coordinates coordinates) {
        for (Ship ship : ships) {
            for (Coordinates shipCoordinates : ShipService.getCoordinatesOfTheShip(ship)) {
                if (shipCoordinates.equals(coordinates)) {
                    // we found this ship
                    return ship;
                }
            }
        }
        return null;
    }

    /**
     * This method will check if some ship you're trying to put could be placed.<br>
     * <br>
     * There is no difference what coordinates will be grater than other.<br>
     * It will convert it so you don't need to think about this.<br>
     *
     * @param field         the field to operate with
     * @param startX        starting X coordinate for a ship
     * @param startY        starting Y coordinate for a ship
     * @param numberOfDecks the number of decks in a ship
     * @param endX          ending X coordinate for a ship
     * @param endY          ending Y coordinate for a ship
     * @return <code>true</code> if ship was created and put at field
     * @throws ShipPlacementException if coordinates are wrong or not valid
     */
    public static boolean putShip(Field field, int startX, int startY, int numberOfDecks, int endX, int endY)
            throws ShipPlacementException {

        int maxX = field.getMaxXFieldSize();
        int maxY = field.getMaxYFieldSize();
        if (startX >= maxX || startY >= maxY || endX >= maxX || endY >= maxY ||
                startX <= 0 || startY <= 0 || endX <= 0 || endY <= 0) {
            throw new ShipPlacementException("Coordinates out of range");
        }
        // TODO: check this place! could be some lags here
        if (Math.abs(startX - endX) != numberOfDecks ^ Math.abs(startY - endY) != numberOfDecks) {
            throw new ShipPlacementException("Wrong size");
        }

        // defining direction of a ship by its coordinates
        boolean xDirection = false;
        boolean yDirection = false;
        if (startX == endX) yDirection = true;
        if (startY == endY) xDirection = true;

        // if both true - we have equal starting/ending points of ship. like in single-deck one
        if (xDirection && yDirection) throw new ShipPlacementException("Not single-deck");
        /*
         * if both false - it means that all coordinates are differs
         * so the ship is not horizontal and it's not vertical either
        */
        else if (!xDirection && !yDirection) throw new ShipPlacementException("Wrong placement");

        int startCell;
        int endCell;
        if (xDirection) {
            startCell = Math.min(startX, endX);
            endCell = startCell + numberOfDecks;
        } else {
            startCell = Math.min(startY, endY);
            endCell = startCell + numberOfDecks;
        }

        // Checking if every deck is not too close to other ships. If it is - exception would be thrown.
        for (int i = startCell; i < endCell; i++) {
            if (xDirection) checkIfCloseByXY(field, i, startY);
            else checkIfCloseByXY(field, startX, i);
        }

        // creating ship instance
        Ship ship = ShipService.createShip(
                Math.min(startX, endX),
                Math.min(startY, endY),
                numberOfDecks,
                Math.max(startX, endX),
                Math.max(startY, endY));

        // setting true values at the field at needed coordinates
        boolean[][] values = field.getField();
        for (Coordinates coordinates : ShipService.getCoordinatesOfTheShip(ship)) {
            values[coordinates.getY()][coordinates.getX()] = true;
        }
        field.getShips().add(ship);     // ship added

        return true;
    }

    /**
     * This method will check if some ship you're trying to put could be placed.<br>
     * Works only for ships with 1 deck.<br>
     *
     * @param field the field to operate with
     * @param x     X coordinate for a ship
     * @param y     Y coordinate for a ship
     * @return <code>true</code> if ship was created and put at field
     * @throws ShipPlacementException if coordinates are wrong or not valid
     */
    public static boolean putShip(Field field, int x, int y) throws ShipPlacementException {
        // Checking if coordinates for this ship are not too close to other ships. If it is - exception would be thrown.
        checkIfCloseByXY(field, x, y);

        Ship ship = ShipService.createShip(x, y);   // creating ship instance
        field.getField()[y][x] = true;              // setting true value at the field at needed coordinates
        field.getShips().add(ship);                 // ship added

        return true;
    }

    /**
     * Checking if there any other ships close to that one you're trying to put.
     * Returns nothing except new exceptions for you to work with :)
     */
    private static void checkIfCloseByXY(Field field, int x, int y) throws ShipPlacementException {
        ShipPlacementException e = new ShipPlacementException("Too close to other ships");
        int maxX = field.getMaxXFieldSize();
        int maxY = field.getMaxYFieldSize();

        if (getCell(field, x, y)) throw new ShipPlacementException("Cell isn't empty");
        if (x > 0) {
            if (y - 1 >= 0 && getCell(field, x - 1, y - 1)) throw e;
            if (getCell(field, x - 1, y)) throw e;
            if (y + 1 < maxY && getCell(field, x - 1, y + 1)) throw e;
        }
        if (x < maxX) {
            if (y - 1 >= 0 && getCell(field, x + 1, y - 1)) throw e;
            if (getCell(field, x + 1, y)) throw e;
            if (y + 1 < maxY && getCell(field, x + 1, y + 1)) throw e;
        }
        if (y - 1 >= 0 && getCell(field, x, y - 1)) throw e;
        if (y + 1 < maxY && getCell(field, x, y + 1)) throw e;
    }
}
