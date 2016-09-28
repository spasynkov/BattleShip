package battleship.service;

import battleship.ShipPlacementException;
import battleship.entities.Coordinates;
import battleship.entities.Field;
import battleship.entities.Ship;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the logic for operating with {@link battleship.entities.Field} objects
 */
// TODO: remove hardcoded error messages
public class FieldService {
    private Field field;

    private int maxX;
    private int maxY;
    private boolean[][] fieldValues;

    public FieldService() {
    }

    public FieldService(Field field) {
        if (field != null) {
            this.field = field;
            maxX = field.getMaxXFieldSize();
            maxY = field.getMaxYFieldSize();
            fieldValues = field.getFieldValues();
        }
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        if (field != null) {
            this.field = field;
            maxX = field.getMaxXFieldSize();
            maxY = field.getMaxYFieldSize();
            fieldValues = field.getFieldValues();
        }
    }

    public boolean getCell(Coordinates coordinates) {
        return fieldValues[coordinates.getY()][coordinates.getX()];
    }

    public boolean getCell(int x, int y) {
        return fieldValues[y][x];
    }

    public List<Coordinates> getEmptyCells() {
        List<Coordinates> result = new ArrayList<>();

        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                if (!fieldValues[i][j]) result.add(new Coordinates(j, i));
            }
        }
        return result;
    }

    public int hitShipAtField(Coordinates coordinates) {
        if (fieldValues[coordinates.getY()][coordinates.getX()]) {
            // hit! lets check if if was the last deck of the ship
            Ship ship = findShipByCoordinates(field.getShips(), coordinates);
            ShipService.hit(ship);  // boom!
            // now lets check if it's still alive
            if (ShipService.isShipAlive(ship)) {
                return 1;           // partial success
            } else {
                field.getShips().remove(ship);
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
    private Ship findShipByCoordinates(List<Ship> ships, Coordinates coordinates) {
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
     * @param startX        starting X coordinate for a ship
     * @param startY        starting Y coordinate for a ship
     * @param numberOfDecks the number of decks in a ship
     * @param endX          ending X coordinate for a ship
     * @param endY          ending Y coordinate for a ship
     * @return <code>true</code> if ship was created and put at field
     * @throws ShipPlacementException if coordinates are wrong or not valid
     */
    public boolean putShip(int startX, int startY, int numberOfDecks, int endX, int endY)
            throws ShipPlacementException {

        if (startX >= maxX || startY >= maxY || endX >= maxX || endY >= maxY ||
                startX < 0 || startY < 0 || endX < 0 || endY < 0) {
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
            if (xDirection) checkIfCloseByXY(i, startY);
            else checkIfCloseByXY(startX, i);
        }

        // creating ship instance
        Ship ship = ShipService.createShip(
                Math.min(startX, endX),
                Math.min(startY, endY),
                numberOfDecks,
                Math.max(startX, endX),
                Math.max(startY, endY));

        // setting true values at the field at needed coordinates
        for (Coordinates coordinates : ShipService.getCoordinatesOfTheShip(ship)) {
            fieldValues[coordinates.getY()][coordinates.getX()] = true;
        }
        field.getShips().add(ship);     // ship added

        return true;
    }

    /**
     * This method will check if some ship you're trying to put could be placed.<br>
     * Works only for ships with 1 deck.<br>
     *
     * @param x     X coordinate for a ship
     * @param y     Y coordinate for a ship
     * @return <code>true</code> if ship was created and put at field
     * @throws ShipPlacementException if coordinates are wrong or not valid
     */
    public boolean putShip(int x, int y) throws ShipPlacementException {
        // Checking if coordinates for this ship are not too close to other ships. If it is - exception would be thrown.
        checkIfCloseByXY(x, y);

        Ship ship = ShipService.createShip(x, y);   // creating ship instance
        fieldValues[y][x] = true;                   // setting true value at the field at needed coordinates
        field.getShips().add(ship);                 // ship added

        return true;
    }

    /**
     * Clears the field
     */
    public void clear() {
        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                fieldValues[i][j] = false;
            }
        }
    }

    public boolean[] getLine(int row) {
        boolean[] result = new boolean[maxX];
        System.arraycopy(fieldValues[row], 0, result, 0, result.length);
        return result;
    }

    public int getShipsNumber() {
        return field.getShips().size();
    }

    /**
     * Checking if there any other ships close to that one you're trying to put.
     * Returns nothing except new exceptions for you to work with :)
     */
    private void checkIfCloseByXY(int x, int y) throws ShipPlacementException {
        ShipPlacementException e = new ShipPlacementException("Too close to other ships");

        // check current cell
        if (getCell(x, y)) throw new ShipPlacementException("Cell isn't empty");

        if (x > 0) {                                                    // if we could move at left
            if (y - 1 >= 0 && getCell(x - 1, y - 1)) throw e;       // check top-left cell
            if (getCell(x - 1, y)) throw e;                         // check left cell
            if (y + 1 < maxY && getCell(x - 1, y + 1)) throw e;     // check bottom-left cell
        }
        if (x < maxX) {                                                 // if we could go at right
            if (y - 1 >= 0 && getCell(x + 1, y - 1)) throw e;       // check top-right cell
            if (getCell(x + 1, y)) throw e;                         // check right cell
            if (y + 1 < maxY && getCell(x + 1, y + 1)) throw e;     // check bottom-right cell
        }
        if (y - 1 >= 0 && getCell(x, y - 1)) throw e;               // check top cell
        if (y + 1 < maxY && getCell(x, y + 1)) throw e;             // check bottom cell
    }
}
