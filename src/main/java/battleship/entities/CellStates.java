package battleship.entities;

/**
 * Represents available states for each cell on the field
 */
public enum CellStates {
    /**
     * Empty cell
     */
    EMPTY,

    /**
     * Cell with a ship in it (or some deck of the ship)
     */
    SHIP,

    /**
     * Empty cell that being shoot
     */
    MISSED,

    /**
     * Cell with a ship in it (or some deck of the ship) that being shoot
     */
    DEAD_SHIP
}
