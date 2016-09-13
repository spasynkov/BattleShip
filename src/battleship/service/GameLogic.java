package battleship.service;

/**
 * The main logic of every player in the game
 */
public interface GameLogic {
    /**
     * Place different ships at field
     */
    void placeShips();

    /**
     * Place some particular ship at field
     *
     * @param numberOfDecks Number of decks in a ship you're going to place at player's field
     */
    void placeShipByDeckNumber(int numberOfDecks);

    /**
     * This method calls when some other player attacks this player
     *
     * @param x X coordinate at player's field
     * @param y Y coordinate at player's field
     * @return integer value as a result. It could be:<br>
     * <b>0</b> - if there is NO ship/deck by these coordinates <i>(miss)</i><br>
     * <b>1</b> - if there IS a deck by these coordinates,
     * but the ship is still alive and have unbroken deck(s) <i>(partial success)</i><br>
     * <b>2</b> - if there is last deck of a ship. The ship is completely destroyed now <i>(success)</i><br>
     * <b>-1</b> - if these coordinates were already attacked before
     */
    int beingAttacked(int x, int y);

    /**
     * Makes a shoot at enemy's field
     *
     * @param enemy particular {@link GameService} to shoot at
     */
    void makeShoot(GameService enemy);
}
