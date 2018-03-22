package sw806f18.server;

import com.mongodb.MongoClient;

/**
 * Created by augustkorvell on 15/03/2018.
 */
public class NoSqlDatabase {
    private static MongoClient client;

    private static void openConnectiona(){
        client = new MongoClient( "192.168.1.125" , 27017 );

    }

    private static void closeConnection(){
        client.close();
    }
}
