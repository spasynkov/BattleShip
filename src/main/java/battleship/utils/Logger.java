package battleship.utils;

import battleship.exceptions.ShipPlacementException;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logging actions class.
 */
public class Logger implements Closeable {

    // TODO get date format from properties
    private static String dateFormatPattern = "EEEE, d MMMM y HH:mm:ss ('GMT' XXX) : ";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(dateFormatPattern);

    private static Logger instance = null;
    private static BufferedWriter writer = null;

    static {
        try {
            writer = new BufferedWriter(new FileWriter("./game.log", true));
        } catch (IOException e) {
            System.out.println("Can't create log file. Program terminated.");
            e.printStackTrace();
        }
    }

    private Logger() {
        if (writer == null) System.exit(1);
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
            instance.write("Program started, log created.");
        }
        return instance;
    }

    public void write(String text) {
        try {
            writer.write(getTime() + text + "\n");
        } catch (IOException e) {
            System.out.println("There was some error while writing to log.\n");
            e.printStackTrace();
        }
    }

    public void write(String text, Throwable error) {
        try {
            writer.write(getTime() + text + "\n");
            writer.write("\t" + error.toString() + "\n");
            if (!(error instanceof ShipPlacementException)) {
                for (StackTraceElement x : error.getStackTrace()) {
                    writer.write("\t\t" + x.toString() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("There was some error while writing to log.\n");
            e.printStackTrace();
        }
    }

    public void writeSynchronized(String text) {
        synchronized (this) {
            write(text);
        }
    }

    public void writeSynchronized(String text, Throwable error) {
        synchronized (this) {
            write(text, error);
        }
    }

    public void write() {
        try {
            writer.write("----------\n");
        } catch (IOException e) {
            System.out.println("There was some error while writing to log.\n");
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    private String getTime() {
        return DATE_FORMAT.format(new Date());
    }
}
