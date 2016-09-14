package battleship.entities;

/**
 * Ships in this game stores in memory like vectors:
 * they have starting coordinates, length and direction (in our case only 2 possible directions are: by X, or by Y).
 */
public class Ship {
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
    private boolean isHorizontal;

    /**
     * The number of decks without fire and holes from bombs
     */
    private int safeDecks;

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
     */
    public Ship(int startX, int startY, int numberOfDecks, boolean isHorizontal) {
        this.startX = startX;
        this.startY = startY;
        this.isHorizontal = isHorizontal;
        safeDecks = this.length = numberOfDecks;
    }

    public int getSafeDecks() {
        return safeDecks;
    }

    public void setSafeDecks(int safeDecks) {
        this.safeDecks = safeDecks;
    }

    public int getLength() {
        return length;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    @Override
    public String toString() {
        return "(" + startX + ", " + startY + ") -> " + length +
                ", direction: " + (isHorizontal ? "x; " : "y; ");
    }
}
