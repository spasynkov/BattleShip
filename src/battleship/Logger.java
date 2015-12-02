package battleship;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by spasynkov on 29.11.15.
 */
public class Logger implements Closeable {

    private static Logger instance = null;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE, d MMMM y HH:mm:ss ('GMT' XXX) : ");
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
        if (instance == null) instance = new Logger();
        instance.write("Program started, logger created.");
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
