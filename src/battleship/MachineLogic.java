package battleship;

import java.util.Random;

/**
 * This class contains methods for implementation some computer's logic in this game.
 */
class MachineLogic implements Runnable {
    private final Field field = new Field();
    private static final Logger log = Logger.getInstance();
    private static final Random random = new Random();

    @Override
    public void run() {
        placeShips();
    }

    private void placeShips() {
        for (int i = 4; i > 0; i--) {
            placeShipByDeckNumber(i);
        }
        synchronized (log) {
            log.write("All computer's ships were placed. Cleaning field...");
        }
        field.clear();
    }

    private void placeShipByDeckNumber(int numberOfDecks) {
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
                        flag = !field.putShip(startX, startY, numberOfDecks, endX, endY);
                    } else flag = !field.putShip(startX, startY);
                } catch (ShipPlacementException e) {
                    // ok, we cant place ship here. let's try again
                } catch (Exception e) {
                    synchronized (log) {
                        log.write("Something bad happened while placing computer's ships.", e);
                    }
                }
            } while (flag);

            synchronized (log) {
                if (numberOfDecks != 1)
                    log.write("Computer put his ship with " + numberOfDecks + " decks at: (" + start.toUpperCase() + ", " + end.toUpperCase() + ").");
                else log.write("Computer put his ship with 1 deck at: (" + start.toUpperCase() + ").");
            }
        }
    }

    char getCell(int x, int y) {
        char result = field.getCell(x, y);
        if (result == Field.getFilledCell()) result = Field.getEmptyCell();
        return result;
    }

    boolean isMoreShips() {
        return field.getShipsNumber() > 0;
    }

    public void makeShoot(UserInterface user) {
        int x, y;
        boolean repeat;
        do {
            if (!user.isMoreShips()) break;
            int[][] cells = user.getEmptyCells();
            int cell = random.nextInt(cells.length);
            x = cells[cell][0];
            y = cells[cell][1];
            repeat = user.gotShooted(x, y);
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                log.write("Main thread was interrupted while sleeping after computer's turn", e);
            }
        } while (repeat);
    }

    int shoot(int x, int y) {
        return field.checkDeckAtField(x, y);
    }
}
