package sw806f18.server.database;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;

import static com.mongodb.client.model.Filters.gt;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.*;

import java.util.Arrays;
import java.util.List;

import org.bson.conversions.Bson;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import sw806f18.server.Configurations;
import sw806f18.server.model.*;

import java.util.ArrayList;


/**
 * Created by augustkorvell on 15/03/2018.
 */
public class NoSqlDatabase {
    private static MongoClient client;
    private static MongoDatabase database;
    private static PojoCodecProvider modulePojoCodecProvider;
    private static PojoCodecProvider hubPojoCodecProvider;
    private static PojoCodecProvider answerPojoCodecProvider;

    private static final String MODULE_COLLECTION = "module";
    private static final String ANSWER_COLLECTION = "answers";
    private static final String hubCollection = "hub";

    private static void openConnection() {
        MongoClientURI uri = new MongoClientURI("mongodb://" + Configurations.instance.getMongoUser() + ":"
                + Configurations.instance.getMongoPassword() + "@" + Configurations.instance.getMongoIp()
                + ":" + Configurations.instance.getMongoPort() + "/?authSource=admin");

        client = new MongoClient(uri);
        database = client.getDatabase(Configurations.instance.getMongoDatabase());

        ClassModel<Hub> hubModel = ClassModel.builder(Hub.class).build();
        ClassModel<Survey> surveyModel = ClassModel.builder(Survey.class).build();
        ClassModel<Question> questionModel = ClassModel.builder(Question.class).enableDiscriminator(true).build();
        ClassModel<DropdownQuestion> dropdownQuestionModel =
                ClassModel.builder(DropdownQuestion.class).enableDiscriminator(true).build();
        ClassModel<TextQuestion> textQuestionModel =
                ClassModel.builder(TextQuestion.class).enableDiscriminator(true).build();
        ClassModel<NumberQuestion> numberQuestionModel =
                ClassModel.builder(NumberQuestion.class).enableDiscriminator(true).build();

        ClassModel<Answer> answerModel = ClassModel.builder(Answer.class).build();

        modulePojoCodecProvider = PojoCodecProvider.builder().register(
                surveyModel, questionModel, dropdownQuestionModel, textQuestionModel, numberQuestionModel).build();
        hubPojoCodecProvider = PojoCodecProvider.builder().register(hubModel).build();
        answerPojoCodecProvider = PojoCodecProvider.builder().register(
            surveyModel, questionModel, dropdownQuestionModel,
            textQuestionModel, numberQuestionModel, answerModel).build();
    }


    private static void closeConnection() {
        client.close();
    }

    static void cleanMongoDB() {
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Survey> collection = database.getCollection(MODULE_COLLECTION, Survey.class);

        collection.drop();

        closeConnection();

    }

    /**
     * Add survey to the NoSQL Database.
     * @param s Survey
     */
    public static void addSurvey(Survey s) {
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(modulePojoCodecProvider));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Survey> collection = database.getCollection(MODULE_COLLECTION, Survey.class);

        try {
            collection.insertOne(s);
        } catch (Exception e) {
            e.printStackTrace();
        }


        closeConnection();
    }

    static void addHub(Hub h) {
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(hubPojoCodecProvider));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Hub> collection = database.getCollection(hubCollection, Hub.class);

        collection.insertOne(h);

        closeConnection();
    }

    static Hub getHub(int hubID) {
        Hub hub;
        openConnection();
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(hubPojoCodecProvider));
        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Hub> collection = database.getCollection(hubCollection, Hub.class);
        hub = collection.find((eq("_id", hubID))).first();
        closeConnection();
        return hub;
    }

    static Survey getSurvey(int surveyID) {
        Survey survey;
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(modulePojoCodecProvider));

        database = database.withCodecRegistry(pojoCodecRegistry);

        MongoCollection<Survey> collection = database.getCollection(MODULE_COLLECTION, Survey.class);

        survey = collection.find((eq("_id", surveyID))).first();


        closeConnection();

        return survey;
    }

    static List<Survey> getSurveys(List<Integer> surveyIDs) {
        List<Survey> surveys = new ArrayList<>();
        openConnection();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(modulePojoCodecProvider));

        database = database.withCodecRegistry(pojoCodecRegistry);

        MongoCollection<Survey> collection = database.getCollection(MODULE_COLLECTION, Survey.class);

        for (int i : surveyIDs) {
            surveys.add(collection.find((eq("_id", i))).first());
        }

        closeConnection();

        return surveys;
    }

    static void addAnswer(Answer answer) {
        openConnection();
        CodecRegistry pojoCodeRegestry = fromRegistries(
            MongoClient.getDefaultCodecRegistry(),
            fromProviders(answerPojoCodecProvider));
        database = database.withCodecRegistry(pojoCodeRegestry);
        MongoCollection<Answer> collection = database.getCollection(ANSWER_COLLECTION, Answer.class);

        collection.insertOne(answer);
        closeConnection();

    }

    static List<Survey> getAllSurveys() {

        openConnection();
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(modulePojoCodecProvider));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Survey> collection = database.getCollection(MODULE_COLLECTION, Survey.class);
        List<Survey> surveys = (List<Survey>) collection.find().into(new ArrayList<Survey>());

        closeConnection();
        return surveys;
    }

    static void removeQuestionFromSurvey(int surveyId, int questionId) {
        openConnection();
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(modulePojoCodecProvider));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Survey> collection = database.getCollection(MODULE_COLLECTION, Survey.class);

        Survey survey = Database.getSurvey(surveyId);
        Question question = null;

        for (Question q : survey.getQuestions()) {
            if (q.getId() == questionId) {
                question = q;
            }
        }

        survey.removeQuestion(question);

        collection.replaceOne(eq("_id", surveyId), survey);
    }

    static void updateQuestionFromSurvey(Survey survey) {
        openConnection();
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(modulePojoCodecProvider));

        database = database.withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Survey> collection = database.getCollection(MODULE_COLLECTION, Survey.class);

        collection.replaceOne(eq("_id", survey.getId()), survey);
    }

    static Answer getNewestAnswer(int userId) {
        openConnection();
        CodecRegistry pojoCodeRegestry = fromRegistries(
            MongoClient.getDefaultCodecRegistry(),
            fromProviders(answerPojoCodecProvider));
        database = database.withCodecRegistry(pojoCodeRegestry);
        MongoCollection<Answer> collection = database.getCollection(ANSWER_COLLECTION, Answer.class);

        collection.find().into(new ArrayList<>());

        Answer answer = collection.find().sort(new BasicDBObject("timeStamp", -1)).limit(1).first();
        closeConnection();
        return answer;
    }

    static List<Tuple<String, Integer>> getPersonsFromSurveyBasedOnQuery(
            Tuple<String, Integer> survey, List<QueryRow> query) {
        //Todo return a list of people that answered a survey
        return null;
    }
}