package sw806f18.server.database;

import static com.mongodb.client.model.Filters.eq;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.*;

import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import sw806f18.server.Configurations;
import sw806f18.server.exceptions.NotImplementedException;
import sw806f18.server.model.Survey;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by augustkorvell on 15/03/2018.
 */
public class NoSqlDatabase {
    private static MongoClient client;
    private static MongoDatabase database;

    private static final String moduleCollection = "module";

    private static void openConnection() {
        MongoClientURI uri = new MongoClientURI("mongodb://root:power123@192.168.1.111:27017/?authSource=admin");
        client = new MongoClient(uri);
        database = client.getDatabase(Configurations.instance.getMongoDatabase());
    }


    private static void closeConnection() {
        client.close();
    }

    static void cleanMongoDB() {
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Survey> collection = database.getCollection(moduleCollection, Survey.class);

        collection.drop();

        closeConnection();

    }

    static void addSurvey(Survey s) {
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Survey> collection = database.getCollection(moduleCollection, Survey.class);

        collection.insertOne(s);

        closeConnection();
    }

    static Survey getSurvey(int surveyID) {
        Survey survey;
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        database = database.withCodecRegistry(pojoCodecRegistry);

        MongoCollection<Survey> collection = database.getCollection(moduleCollection, Survey.class);

        survey = collection.find((eq("_id", surveyID))).first();


        closeConnection();

        return survey;
    }

    static List<Survey> getSurveys(List<Integer> surveyIDs) {
        List<Survey> surveys = new ArrayList<>();
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        database = database.withCodecRegistry(pojoCodecRegistry);

        MongoCollection<Survey> collection = database.getCollection(moduleCollection, Survey.class);

        for (int i : surveyIDs) {
            surveys.add(collection.find((eq("_id", i))).first());
        }

        closeConnection();

        return surveys;
    }
}
