package battleship;

/**
 * Created by spasynkov on 07.12.15.
 */
class Ship {
    private int numberOfDecks;
    private int[][] coordinates = new int[numberOfDecks][2];
    boolean xDirection;

    public Ship(int x, int y) {
        this.numberOfDecks = 1;
        coordinates[0][0] = x;
        coordinates[0][1] = y;
    }

    public Ship(int startX, int startY, int numberOfDecks, int endX, int endY) {
        this.numberOfDecks = numberOfDecks;
        if (startY == endY) xDirection = true;
        // TODO add filling coordinate cells
    }
}
