package battleship;

/**
 * Ships in this game stores in memory like vectors:
 * they have starting coordinates, length and direction (in our case only 2 possible directions are: by X, or by Y).
 */
class Ship {
    /**
     * Starting coordinate: X
     */
    private int startX;

    /**
     * Starting coordinate: Y
     */
    private int startY;

    /**
     * The length of some ship
     */
    private int length;

    /**
     * Direction of the ship: if ship placed by X and false if by Y
     */
    private boolean xDirection;

    /**
     * The number of decks without fire and holes from bombs
     */
    int safeDecks;

    /**
     * Constructor for ship with only one deck. Saves coordinates you're passing.
     */
    public Ship(int x, int y) {
        this.startX = x;
        this.startY = y;
        safeDecks = this.length = 1;
    }

    /**
     * Constructor for ships with 2 and more decks.
     * You should pass starting coordinates in it, then number of decks in this ship and then ending coordinates.
     * endX one is not used right now but maybe it will be useful in future.
     */
    public Ship(int startX, int startY, int numberOfDecks, int endX, int endY) {
        if (startY == endY) {
            this.xDirection = true;
        }
        this.startX = startX;
        this.startY = startY;
        safeDecks = this.length = numberOfDecks;
    }

    /**
     * Oh, shi..! We're in trouble now!
     */
    public void hit() {
        safeDecks--;
    }

    /**
     * Checks if there are still some decks to shoot in
     */
    public boolean isAlive() {
        return safeDecks > 0 ;
    }

    /**
     * Returns the full list of coordinates this ship takes at the battlefield.
     * [deck number] [x, y]
     */
    public int[][] getCoordinates() {
        int[][] result = new int[length][2];
        if (xDirection) {
            for (int deckNumber = 0; deckNumber < length; deckNumber++) {
                result[deckNumber] = new int[]{startX + deckNumber, startY};
            }
        } else {
            for (int deckNumber = 0; deckNumber < length; deckNumber++) {
                result[deckNumber] = new int[]{startX, startY + deckNumber};
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "(" + startX + ", " + startY + ") -> " + length +
                ", direction: " + (xDirection? "x; " : "y; ");
    }
}
