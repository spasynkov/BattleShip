package battleship.views;

import battleship.entities.PlayerStatistics;
import battleship.exceptions.ShipPlacementException;
import battleship.service.PlayersLogic;
import javafx.util.Pair;

import java.io.IOException;

/**
 * Defines common methods of human user's interface
 */
public interface UserInterface {
    /**
     * Show rules of the game to user
     */
    void showRules();

    /**
     * Explains how to interact with player's field
     */
    void showInputFormat();

    void drawField(PlayersLogic player, boolean drawWithMarksAroundShips);

    void drawAllFields(PlayersLogic user, PlayersLogic enemy);

    void showEnemyMove(Pair<Integer, Integer> coordinates);

    void enemyMissed(Pair<Integer, Integer> coordinates);

    void enemyInjuredYourShip(Pair<Integer, Integer> coordinates);

    void enemyDestroyedYourShip(Pair<Integer, Integer> coordinates);

    Pair<Integer, Integer> askForShipStartingCoordinate(int numberOfDecks) throws ShipPlacementException, IOException;

    Pair<Integer, Integer> askForShipEndingCoordinate(int numberOfDecks) throws ShipPlacementException, IOException;

    Pair<Integer, Integer> askCoordinatesForShoot() throws ShipPlacementException, IOException;

    /**
     * If something bad happend - ask user to repeat his last action
     */
    void askToRepeatLastAction();

    /**
     * Called when user tries to make a shoot that he already did before
     *
     * @param coordinates coordinates of the cell he shoots at
     */
    void suchShootHasBeenMadeAlready(Pair<Integer, Integer> coordinates);

    void userMissed(Pair<Integer, Integer> coordinates);

    void userInjuredEnemysShip(Pair<Integer, Integer> coordinates);

    void userDestroyedEnemysShip(Pair<Integer, Integer> coordinates);

    void won(PlayerStatistics userStats, PlayerStatistics enemyStats);

    void loose(PlayerStatistics userStats, PlayerStatistics enemyStats);
}
