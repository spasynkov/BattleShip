package battleship.service;

import battleship.ShipPlacementException;
import battleship.entities.Coordinates;
import battleship.entities.Player;

import java.util.List;
import java.util.Random;

/**
 * This class contains methods for implementation some computer's logic in this game.
 */
// TODO: remove hardcoded log strings
public class MachineLogic extends PlayersLogic implements Runnable {
    private static final Random random = new Random();

    private final Thread thisThread = new Thread(this, "Placing ships by computer");

    /**
     * Calling {@link PlayersLogic}'s constructor
     *
     * @param player player to be used as a computer player
     */
    public MachineLogic(Player player) {
        super(player);
    }

    @Override
    public void placeShips() {
        logger.write("Placing computer's ships by running a new thread.");
        thisThread.start();
    }

    @Override
    public void run() {
        generateShips();
    }

    public boolean isAlive() {
        return thisThread.isAlive();
    }

    public void waitForFinishingThread() throws InterruptedException {
        thisThread.join();
    }

    private void generateShips() {
        for (int i = 4; i > 0; i--) {
            placeShipByDeckNumber(i);
        }
        logger.writeSynchronized("All computer's ships were placed. Cleaning field...");
    }

    @Override
    public void placeShipByDeckNumber(int numberOfDecks) {
        for (int i = numberOfDecks - 1; i < 4; i++) {   // for each ship of this type (with same number of decks)
            boolean flag = true;
            Coordinates startingCoordinates = null;
            Coordinates endingCoordinates = null;
            do {
                int startX = random.nextInt(getFieldService().getField().getMaxXFieldSize());
                int startY = random.nextInt(getFieldService().getField().getMaxYFieldSize());
                startingCoordinates = new Coordinates(startX, startY);
                int endX, endY;
                boolean xDirection = random.nextBoolean();
                if (numberOfDecks != 1) {
                    if (xDirection) {
                        endX = startX + numberOfDecks - 1;
                        endY = startY;
                        endingCoordinates = new Coordinates(endX, endY);
                    }
                    else {
                        endX = startX;
                        endY = startY + numberOfDecks - 1;
                        endingCoordinates = new Coordinates(endX, endY);
                    }
                }

                try {
                    if (numberOfDecks != 1) {
                        flag = !putShipsAtField(
                                startingCoordinates,
                                numberOfDecks,
                                endingCoordinates);
                    } else flag = !putShipsAtField(new Coordinates(startX, startY));
                } catch (ShipPlacementException e) {
                    // ok, we cant place ship here. let's try again
                } catch (Exception e) {
                    logger.writeSynchronized("Something bad happened while placing computer's ships.", e);
                }
            } while (flag);

            if (numberOfDecks != 1)
                logger.writeSynchronized("Computer put his ship with " + numberOfDecks + " decks at: " +
                        startingCoordinates + " -> " + endingCoordinates);
            else logger.writeSynchronized("Computer put his ship with 1 deck at: " + startingCoordinates);
        }
    }

    @Override
    public void makeShootAt(PlayersLogic enemy) {
        boolean repeat;
        do {
            if (!isMoreShips()) break;
            List<Coordinates> cells = enemy.getFieldService().getEmptyCells();
            Coordinates coordinatesToShootAt = cells.get(random.nextInt(cells.size()));
            repeat = enemy.attackedAt(coordinatesToShootAt) != 0;
            player.addCoordinates(coordinatesToShootAt);
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                logger.write("Main thread was interrupted while sleeping after computer's turn", e);
            }
        } while (repeat);
    }

    @Override
    public int attackedAt(Coordinates coordinates) {
        return checkDeckAtField(coordinates);
    }
}
