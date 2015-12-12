package battleship;

import java.util.Random;

/**
 * This class contains methods for implementation some computer's logic in this game.
 */
class MachineLogic implements Runnable {
    private final Field field = new Field();
    private static final Logger log = Logger.getInstance();

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
        Random rnd = new Random();
        for (int i = numberOfDecks - 1; i < 4; i++) {   // for each ship of this type (with same number of decks)
            boolean flag = true;
            String start, end = "";
            do {
                int startX = rnd.nextInt(10);
                int startY = rnd.nextInt(10);
                boolean xDirection = rnd.nextBoolean();
                start = String.valueOf((char) ('A' + startX)) + (startY + 1);
                if (numberOfDecks != 1) {
                    if (xDirection) end = String.valueOf((char) ('A' + startX + numberOfDecks - 1)) + (startY + 1);
                    else end = String.valueOf((char) ('A' + startX)) + (startY + numberOfDecks);
                }

                try {
                    if (numberOfDecks != 1) {
                        flag = !field.putShip(numberOfDecks, start, end);
                    } else flag = !field.putShip(start);
                } catch (ShipPlacementException e) {
                    // ok, we cant place ship here. let's try again
                } catch (Exception e) {
                    synchronized (log) {
                        log.write("Something bad happened.\n" + e.getMessage());
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

    String[] getField() {
        String[] result = new String[10];
        for (int i = 0; i < result.length; i++) {
            result[i] = field.getLine(i).replaceAll(String.valueOf(Field.getFilledCell()), String.valueOf(Field.getEmptyCell()));
        }
        return result;
    }
}
