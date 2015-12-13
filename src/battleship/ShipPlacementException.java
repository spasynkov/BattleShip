package battleship;

/**
 * Simple exception class to manage placement errors.
 */
class ShipPlacementException extends Exception{
    public ShipPlacementException(String message) {
        super(message);
    }
}
