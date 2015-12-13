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
                log.write("Computer's ships placement was interrupted by something.", e);
                // TODO add System.exit()
            }
        }
        ui.gameStarted();

        int counter = 0;
        while (ui.isMoreShips() && ai.isMoreShips()) {
            if (counter++ % 2 == 0) {
                ui.drawAllFields(ai);
                ui.makeShoot(ai);
            }
            else ai.makeShoot(ui);
        }

        ui.drawAllFields(ai);
        if (ai.isMoreShips()) ui.loose();
        else ui.won();

        UserInterface.end();
        log.write("Program finished.");
        log.write();
        BattleshipUtils.closeStream(log);
    }
}
