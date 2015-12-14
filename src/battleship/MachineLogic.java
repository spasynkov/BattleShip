package battleship;

import java.util.Random;

/**
 * This class contains methods for implementation some computer's logic in this game.
 */
class MachineLogic extends Player implements Runnable {
    private static final Random random = new Random();

    MachineLogic() {
        this("Computer");
    }

    MachineLogic(String name) {
        super(name);
    }

    @Override
    protected void setName(String name) {
        if (name != null && !name.isEmpty()) super.setName(name);
    }

    @Override
    public void run() {
        placeShips();
    }

    @Override
    protected void placeShips() {
        for (int i = 4; i > 0; i--) {
            placeShipByDeckNumber(i);
        }
        synchronized (log) {
            log.write("All computer's ships were placed. Cleaning field...");
        }
        clearField();
    }

    @Override
    void placeShipByDeckNumber(int numberOfDecks) {
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

    @Override
    void makeShoot(Player enemy) {
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
                log.write("Main thread was interrupted while sleeping after computer's turn", e);
            }
        } while (repeat);
    }

    @Override
    int beingAttacked(int x, int y) {
        return checkDeckAtField(x, y);
    }
}
