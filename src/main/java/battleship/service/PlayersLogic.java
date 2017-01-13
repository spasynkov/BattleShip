package battleship.service;

import javafx.util.Pair;

import java.util.List;
import java.util.Set;

/**
 * Default logic of some player
 */
public interface PlayersLogic {
    /**
     * This method calls when some other player attacks this player
     *
     * @param coordinates X and Y pair of coordinates at player's field
     * @return integer value as a result. It could be:<br>
     * <b>0</b> - if there is NO ship/deck by these coordinates <i>(miss)</i><br>
     * <b>1</b> - if there IS a deck by these coordinates,
     * but the ship is still alive and have unbroken deck(s) <i>(partial success)</i><br>
     * <b>2</b> - if there is last deck of a ship. The ship is completely destroyed now <i>(success)</i><br>
     * <b>-1</b> - if these coordinates were already attacked before
     */
    int attackedAt(Pair<Integer, Integer> coordinates);

    /**
     * Checks if player have at least one ship "alive"
     *
     * @return <code>true</code> if there are some ships, or <code>false</code> if all player's ships are destroyed
     */
    boolean isMoreShips();

    boolean[][] getFieldValues();

    String getPlayerName();

    List<Pair<Integer, Integer>> getEmptyCells();

    Set<Pair<Integer, Integer>> getPlayersShootsList();
}
