package battleship;

/**
 * The loader of the game.
 * Main method is here.
 */
// todo: add comments
// todo: add loggers levels
// todo: add names in messages
// todo: add statusbars
// todo: add statistics (time + hits efficiency)
// todo: add default properties opening
// todo: add splitter's signs
// todo: add other then ABCDEFGHIJ strings
// todo: add different fields sizes and ships number
// todo: incapsulate getCells
// todo: add languages
// todo: play with comp against comp
// todo: int -> byte
class GameLoader {
    private static final Logger log = Logger.getInstance();

    public static void main(String[] args) {
        UserInterface ui = new UserInterface(args);
        MachineLogic ai = new MachineLogic(ui.getDesirableComputerName());

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
        if (ui.isMoreShips()) ui.won();
        else ui.loose();

        UserInterface.end();
        log.write("Program finished.");
        log.write();
        BattleshipUtils.closeStream(log);
    }
}
