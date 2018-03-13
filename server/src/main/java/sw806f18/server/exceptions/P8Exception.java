package sw806f18.server.exceptions;

import java.io.*;

/**
 * Created by augustkorvell on 13/03/2018.
 */
public class P8Exception extends Exception {
    public P8Exception(String message){
        super(message);
    }

    public P8Exception(String message, Exception ex){
        super(message, ex);
    }

    @Override
    public void printStackTrace() {
        try {
            FileOutputStream log = new FileOutputStream("~/log/exceptions.log", true);
            PrintStream stream = new PrintStream(log);
            super.printStackTrace(stream);
            stream.close();
            log.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
