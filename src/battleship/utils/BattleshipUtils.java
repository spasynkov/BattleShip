package battleship.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * This class contains methods used by other classes.
 */
public class BattleshipUtils {
    private static Logger logger;

    public static void setLogger(Logger logger) {
        BattleshipUtils.logger = logger;
    }

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
                if (!(stream instanceof Logger)) logger.write(stream.getClass().getName() + " closed.");
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
