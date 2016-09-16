package battleship.service;

import battleship.ShipPlacementException;
import battleship.entities.Coordinates;
import battleship.entities.Player;
import battleship.views.UserInterface;

import java.io.IOException;

/**
 * Methods to be called while working with human user
 */
public class HumanLogic extends PlayersLogic {

    private UserInterface userInterface;

    public HumanLogic(Player player) {
        super(player);
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    @Override
    public void placeShips() {
        for (int i = 4; i > 0; i--) {
            placeShipByDeckNumber(i);
        }
    }

    @Override
    public void placeShipByDeckNumber(int numberOfDecks) {
        for (int i = numberOfDecks - 1; i < 4; i++) {       // for each ship
            Coordinates startingCoordinates = null;
            Coordinates endingCoordinates = null;
            boolean shipPutFailed = true;
            do {
                try {
                    if (numberOfDecks != 1) {
                        startingCoordinates = userInterface.askForShipStartingCoordinate(numberOfDecks);
                        endingCoordinates = userInterface.askForShipEndingCoordinate(numberOfDecks);
                        shipPutFailed = !putShipsAtField(startingCoordinates, numberOfDecks, endingCoordinates);
                    } else {
                        startingCoordinates = userInterface.askForShipStartingCoordinate(numberOfDecks);
                        shipPutFailed = !putShipsAtField(startingCoordinates);
                    }
                } catch (ShipPlacementException e) {
                    logger.writeSynchronized("Bad coordinates for " + numberOfDecks + "decker " +
                            "(" + startingCoordinates + ", " + endingCoordinates + ")", e);
                } catch (IOException e) {
                    logger.writeSynchronized("Exception while reading from console.", e);
                } catch (Exception e) {
                    logger.writeSynchronized("Something bad happened while user was trying to place his ships.", e);
                }
            } while (shipPutFailed);
            // saving coordinates in log
            logger.writeSynchronized("Coordinates for user's " + numberOfDecks + "decker: (" +
                    startingCoordinates + (numberOfDecks > 1 ? ", " + endingCoordinates : "") + ").");

            userInterface.drawField(this, true);
            /* if (i != 3) System.out.println(LANG.get("Ask for another ship")); */
        }
    }

    @Override
    public int attackedAt(Coordinates coordinates) {
        int result = checkDeckAtField(coordinates);
        userInterface.showEnemyMove(coordinates);
        logger.write("Enemy shoots at: " + coordinates + ".");
        if (result == 0) {
            userInterface.enemyMissed(coordinates);
            logger.write("Computer misses.");
            return result;
        }
        if (result > 0) {
            if (result == 1) {
                userInterface.enemyInjuredYourShip(coordinates);
                logger.write("Computer hits ship.");
                return result;
            }
            if (result == 2) {
                userInterface.enemyDestroyedYourShip(coordinates);
                logger.write("Computer kills ship.");
                return result;
            }
        }
        if (result == -1) {     // TODO: looks like something impossible, remove this after testing
            System.out.println("Computer already hit this cell. It should choose another one.");
        }
        return result;
    }

    @Override
    public void makeShootAt(PlayersLogic enemy) {
        // TODO replace hardcoded language strings
        int x = -1, y = -1;
        int repeat = 0;
        incrementTheNumberOfMovesPlayerDid();
        int cycleCounter = 0;
        do {
            if (!enemy.isMoreShips()) break;
            cycleCounter++;
            Coordinates coordinates = null;
            if (repeat != 0) {
                userInterface.drawAllFields(this, enemy);
            }

            try {
                coordinates = userInterface.askCoordinatesForShoot();
            } catch (ShipPlacementException e) {
                logger.write("User input had bad coordinates for shoot", e);
            } catch (IOException e) {
                logger.write("There was an error while getting user's coordinates for next shoot", e);
            } finally {
                if (coordinates != null) {
                    userInterface.askToRepeatLastAction();
                }
            }

            if (coordinates != null) {
                logger.write("User shoots: " + coordinates);
                if (player.getCoordinatesList().contains(coordinates)) {
                    userInterface.suchShootHasBeenMadeAlready(coordinates);
                    logger.write("User already shoot this cell.");
                    continue;   // try again
                }
                repeat = enemy.attackedAt(coordinates);
                player.addCoordinates(coordinates);
                switch (repeat) {
                    case 0: {
                        userInterface.userMissed(coordinates);
                        logger.write("User misses.");
                        break;
                    }
                    case 1: {
                        userInterface.userInjuredEnemysShip(coordinates);
                        logger.write("User hits a ship.");
                        break;
                    }
                    case 2: {
                        userInterface.userDestroyedEnemysShip(coordinates);
                        logger.write("User kills computer's ship.");
                        break;
                    }
                }
            }
        } while (repeat != 0);
        if (cycleCounter > getTheLongestStreak()) setTheLongestStreak(cycleCounter);
    }
}
