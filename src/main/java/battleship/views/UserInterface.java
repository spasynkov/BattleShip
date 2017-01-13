package battleship.views;

import battleship.entities.Coordinates;
import battleship.entities.PlayerStatistics;
import battleship.exceptions.ShipPlacementException;
import battleship.service.PlayersLogic;

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

    void showEnemyMove(Coordinates coordinates);

    void enemyMissed(Coordinates coordinates);

    void enemyInjuredYourShip(Coordinates coordinates);

    void enemyDestroyedYourShip(Coordinates coordinates);

    Coordinates askForShipStartingCoordinate(int numberOfDecks) throws ShipPlacementException, IOException;

    Coordinates askForShipEndingCoordinate(int numberOfDecks) throws ShipPlacementException, IOException;

    Coordinates askCoordinatesForShoot() throws ShipPlacementException, IOException;

    /**
     * If something bad happend - ask user to repeat his last action
     */
    void askToRepeatLastAction();

    /**
     * Called when user tries to make a shoot that he already did before
     *
     * @param coordinates coordinates of the cell he shoots at
     */
    void suchShootHasBeenMadeAlready(Coordinates coordinates);

    void userMissed(Coordinates coordinates);

    void userInjuredEnemysShip(Coordinates coordinates);

    void userDestroyedEnemysShip(Coordinates coordinates);

    void won(PlayerStatistics userStats, PlayerStatistics enemyStats);

    void loose(PlayerStatistics userStats, PlayerStatistics enemyStats);
}
