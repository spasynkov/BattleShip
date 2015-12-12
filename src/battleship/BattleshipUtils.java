package battleship;

import java.io.Closeable;
import java.io.IOException;

/**
 * This class contains methods used by other classes.
 */
class BattleshipUtils {
    private static final Logger log = Logger.getInstance();

    static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
                if (!(stream instanceof Logger)) log.write(stream.getClass().getName() + " closed.");
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
