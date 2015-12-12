package battleship;

/**
 * The loader of the game.
 * Main method is here.
 */
class GameLoader {
    private static final Logger log = Logger.getInstance();

    public static void main(String[] args) {
        UserInterface ui = new UserInterface(args);
        MachineLogic ai = new MachineLogic();

        log.write("Placing computer's ships by running a new thread.");
        Thread thread = new Thread(ai, "Placing ships by computer");
        thread.start();
        synchronized (log) {
            log.write("Asking user to place his ships.");
        }
        ui.placeShips();
        if (thread.isAlive()) {
            ui.askToWait();
            try {
                thread.join();
                log.write("Computer's ships placement finished.");
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.write("Computer's ships placement was interrupted by something.");
                // TODO add System.exit()
            }
        }
        ui.gameStarted();
        ui.drawFields(ai.getField());

        UserInterface.end();
        log.write("Program finished.");
        log.write();
        BattleshipUtils.closeStream(log);
    }
}
