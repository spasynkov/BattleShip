package battleship.service;

import battleship.ShipPlacementException;
import battleship.entities.Coordinates;
import battleship.entities.Ship;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple logic for calculating ship's stats
 */
public class ShipService {
    /**
     * Creates the ship and return link at it
     *
     * @param startX starting X coordinate of a ship. Should be a positive number
     * @param startY starting Y coordinate of a ship. Should be a positive number
     * @return the link for a particular {@link Ship} that just created
     * @throws ShipPlacementException if bad coordinates would be found
     */
    public static Ship createShip(int startX, int startY) throws ShipPlacementException {
        checkCoordinates(startX, startY);
        return new Ship(startX, startY);
    }

    /**
     * Creates the ship and return link at it
     *
     * @param startX        starting X coordinate of a ship. Should be a positive number
     * @param startY        starting Y coordinate of a ship. Should be a positive number
     * @param numberOfDecks number of decks that ship will have. Should be a positive number
     * @param endX          ending X coordinate of a ship. Should be a positive number
     * @param endY          ending Y coordinate of a ship. Should be a positive number
     * @return the link for a particular {@link Ship} that just created
     * @throws ShipPlacementException if bad coordinates or number of decks would be found
     */
    public static Ship createShip(int startX, int startY, int numberOfDecks, int endX, int endY)
            throws ShipPlacementException {

        if (numberOfDecks == 1 ||
                (startX == endX && startY == endY)) {
            return createShip(startX, startY);
        }

        checkCoordinates(startX, startY, numberOfDecks, endX, endY);
        boolean xDirection;
        //
        if (startX == endX) {
            // if this ship should be horizontal then it's Y coordinates should be differ at number of decks -1
            if (Math.abs(startY - endY) != numberOfDecks - 1) {
                // we got inclined ship by Y coordinate
                throw new ShipPlacementException("Ships could be placed only at horizontal or vertical lines.");
            }
            xDirection = false;
        } else if (startY == endY) {
            // similar idea for vertical ships
            if (Math.abs(startX - endX) != numberOfDecks - 1) {
                // we got inclined ship by X coordinate
                throw new ShipPlacementException("Ships could be placed only at horizontal or vertical lines.");
            }
            xDirection = true;
        } else {
            // here we got right ship, but placed by diagonal (e.g. (0;0) - (3;3))
            throw new ShipPlacementException("Ships could be placed only at horizontal or vertical lines.");
        }
        return new Ship(Math.min(startX, endX), Math.min(startY, endY), numberOfDecks, xDirection);
    }

    // Could be redundant because we did this check in FieldService class
    private static void checkCoordinates(int startX, int startY, int numberOfDecks, int endX, int endY)
            throws ShipPlacementException {

        if (startX < 0 || startY < 0 || endX < 0 || endY < 0 || numberOfDecks < 1) {
            throw new ShipPlacementException("Coordinates for ship and decks number should be a positive value.");
        }
    }

    // Could be redundant because we did this check in FieldService class
    private static void checkCoordinates(int startX, int startY) throws ShipPlacementException {
        if (startX < 0 || startY < 0) {
            throw new ShipPlacementException("Coordinates for ship and decks number should be a positive value.");
        }
    }

    /**
     * Oh, shi..! We're in trouble now!
     */
    public static void hit(Ship ship) throws RuntimeException {
        int safeDecks = ship.getSafeDecks();
        if (safeDecks == 0) {
            throw new RuntimeException("No more alive decks left in this ship");
        }
        ship.setSafeDecks(safeDecks - 1);
    }

    /**
     * Checks if there are still some decks to gotShooted in
     */
    public static boolean isShipAlive(Ship ship) {
        return ship.getSafeDecks() > 0;
    }

    /**
     * Returns the full set of calculated coordinates that ship takes at the battlefield.
     * @param ship the ship for which to get it's coordinates
     * @return {@link Set} of {@link Coordinates} this ships takes at player's field
     */
    public static Set<Coordinates> getCoordinatesOfTheShip(Ship ship) {
        int shipLength = ship.getLength();
        Set<Coordinates> result = new HashSet<>(shipLength);

        int constantIndex;    // the same number for coordinates pair. X if ship is horizontal, or Y if it's not
        if (ship.isHorizontal()) {
            constantIndex = ship.getStartY();
            for (int i = ship.getStartX(); i < ship.getStartX() + shipLength; i++) {
                result.add(new Coordinates(i, constantIndex));
            }
        } else {
            constantIndex = ship.getStartX();
            for (int i = ship.getStartY(); i < ship.getStartY() + shipLength; i++) {
                result.add(new Coordinates(constantIndex, i));
            }
        }
        return result;
    }
}
