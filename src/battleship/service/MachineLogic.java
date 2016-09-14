package battleship.service;

import battleship.ShipPlacementException;
import battleship.entities.Player;

import java.util.Random;

/**
 * This class contains methods for implementation some computer's logic in this game.
 */
public class MachineLogic extends GameService implements Runnable {
    private static final Random random = new Random();

    private final Thread thisThread = new Thread(this, "Placing ships by computer");

    /**
     * Calling {@link GameService}'s constructor
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
        synchronized (logger) {
            logger.write("All computer's ships were placed. Cleaning field...");
        }
        clearField();
    }

    @Override
    public void placeShipByDeckNumber(int numberOfDecks) {
        for (int i = numberOfDecks - 1; i < 4; i++) {   // for each ship of this type (with same number of decks)
            boolean flag = true;
            String start, end = "";
            do {
                int startX = random.nextInt(10);
                int startY = random.nextInt(10);
                int endX = 0, endY = 0;
                boolean xDirection = random.nextBoolean();
                start = String.valueOf((char) ('A' + startX)) + (startY + 1);
                if (numberOfDecks != 1) {
                    if (xDirection) {
                        end = String.valueOf((char) ('A' + startX + numberOfDecks - 1)) + (startY + 1);
                        endX = startX + numberOfDecks - 1;
                        endY = startY;
                    }
                    else {
                        end = String.valueOf((char) ('A' + startX)) + (startY + numberOfDecks);
                        endX = startX;
                        endY = startY + numberOfDecks - 1;
                    }
                }

                try {
                    if (numberOfDecks != 1) {
                        flag = !putShipsAtField(startX, startY, numberOfDecks, endX, endY);
                    } else flag = !putShipsAtField(startX, startY);
                } catch (ShipPlacementException e) {
                    // ok, we cant place ship here. let's try again
                } catch (Exception e) {
                    synchronized (logger) {
                        logger.write("Something bad happened while placing computer's ships.", e);
                    }
                }
            } while (flag);

            synchronized (logger) {
                if (numberOfDecks != 1)
                    logger.write("Computer put his ship with " + numberOfDecks + " decks at: (" + start.toUpperCase() + ", " + end.toUpperCase() + ").");
                else logger.write("Computer put his ship with 1 deck at: (" + start.toUpperCase() + ").");
            }
        }
    }

    @Override
    public void makeShoot(GameService enemy) {
        int x, y;
        boolean repeat;
        do {
            if (!enemy.isMoreShips()) break;
            int[][] cells = enemy.getEmptyCells();
            int cell = random.nextInt(cells.length);
            x = cells[cell][0];
            y = cells[cell][1];
            repeat = enemy.beingAttacked(x, y) != 0;
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                logger.write("Main thread was interrupted while sleeping after computer's turn", e);
            }
        } while (repeat);
    }

    @Override
    public int beingAttacked(int x, int y) {
        return checkDeckAtField(x, y);
    }
}
