package battleship.service;

import battleship.ShipPlacementException;
import battleship.models.Ship;

/**
 * Simple logic for calculating ship's stats
 */
public class ShipService {
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
    public static Ship placeShip(int startX, int startY, int numberOfDecks, int endX, int endY)
            throws ShipPlacementException {

        checkCoordinates(startX, startY, numberOfDecks, endX, endY);
        if (numberOfDecks == 1) {
            return placeShip(startX, startY);
        }

        boolean xDirection;
        if (startX == startY) {
            xDirection = false;
        } else if (startY == endY) {
            xDirection = true;
        } else throw new ShipPlacementException("Ships could be placed only at horizontal or vertical lines.");
        return new Ship(startX, startY, numberOfDecks, xDirection);
    }

    /**
     * Creates the ship and return link at it
     *
     * @param startX starting X coordinate of a ship. Should be a positive number
     * @param startY starting Y coordinate of a ship. Should be a positive number
     * @return the link for a particular {@link Ship} that just created
     * @throws ShipPlacementException if bad coordinates would be found
     */
    public static Ship placeShip(int startX, int startY) throws ShipPlacementException {
        checkCoordinates(startX, startY);
        return new Ship(startX, startY);
    }

    private static void checkCoordinates(int startX, int startY, int numberOfDecks, int endX, int endY)
            throws ShipPlacementException {

        if (startX < 1 || startY < 1 || endX < 1 || endY < 1 || numberOfDecks < 1) {
            throw new ShipPlacementException("Coordinates for ship and decks number should be a positive value.");
        }
    }

    private static void checkCoordinates(int startX, int startY) throws ShipPlacementException {
        if (startX < 1 || startY < 1) {
            throw new ShipPlacementException("Coordinates for ship and decks number should be a positive value.");
        }
    }

    /**
     * Oh, shi..! We're in trouble now!
     */
    public static void hit(Ship ship) {
        ship.setSafeDecks(ship.getSafeDecks() - 1);
    }

    /**
     * Checks if there are still some decks to gotShooted in
     */
    public static boolean isShipAlive(Ship ship) {
        return ship.getSafeDecks() > 0;
    }

    /**
     * Returns the full list of coordinates this ship takes at the battlefield.
     * [deck number] [x, y]
     */
    public static int[][] getCoordinatesOfTheShip(Ship ship) {
        int[][] result = new int[ship.getLength()][2];
        if (ship.isxDirection()) {
            for (int deckNumber = 0; deckNumber < ship.getLength(); deckNumber++) {
                result[deckNumber] = new int[]{ship.getStartX() + deckNumber, ship.getStartY()};
            }
        } else {
            for (int deckNumber = 0; deckNumber < ship.getLength(); deckNumber++) {
                result[deckNumber] = new int[]{ship.getStartX(), ship.getStartY() + deckNumber};
            }
        }
        return result;
    }
}
