package sw806f18.server.exceptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class P8Exception extends Exception {
    public P8Exception(String message) {
        super(message);
    }

    public P8Exception(String message, Exception ex) {
        super(message, ex);
    }

    @Override
    public void printStackTrace() {
        try {
            File logFile = new File("exceptions.log");
            logFile.createNewFile();
            FileOutputStream log = new FileOutputStream(logFile, true);
            PrintStream stream = new PrintStream(log);
            super.printStackTrace(stream);
            stream.close();
            log.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
