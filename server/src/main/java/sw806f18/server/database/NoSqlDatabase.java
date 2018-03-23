package sw806f18.server.database;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import sw806f18.server.Configurations;
import sw806f18.server.model.Survey;

/**
 * Created by augustkorvell on 15/03/2018.
 */
public class NoSqlDatabase {
    private static MongoClient client;
    private static MongoDatabase database;

    private static final String moduleCollection = "module";

    private static void openConnection() {
        client = new MongoClient(Configurations.instance.getMongoIp(), Configurations.instance.getMongoPort());
        database = client.getDatabase(Configurations.instance.getMongoDatabase());
    }


    private static void closeConnection() {
        client.close();
    }

    static void addSurvey(Survey s) {
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Survey> collection = database.getCollection(moduleCollection, Survey.class);

        collection.insertOne(s);
    }

    static Survey getSurvey(int surveyID) {
        throw new NotImplementedException();
    }

    static Survey getSurveys(List<Integer> surveyIDs) {
        throw new NotImplementedException();
    }
}
