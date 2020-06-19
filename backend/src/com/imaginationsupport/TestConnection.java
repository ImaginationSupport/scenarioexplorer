package com.imaginationsupport;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class TestConnection {

	public static void main(String[] args) {

		TestConnection test = new TestConnection();

		for (int i = 0; i < 10000; i++) {
			test.addOne(i);
			test.addOneJavaObj();
		}
		test.compare();
		test.close();

	}

	private MongoClient mongo = null;
	private MongoDatabase db = null;
	private MongoCollection<Document> collection = null;
	private Datastore datastore = null;

	public TestConnection() {
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		mongo = new MongoClient(new MongoClientURI("mongodb://10.91.95.2:27017"));
		db = mongo.getDatabase( "test" );
		collection = db.getCollection("RandomNumbers");

		final Morphia morphia = new Morphia();
		morphia.mapPackage( "test" );
		datastore = morphia.createDatastore(mongo, "test_morphia");
		datastore.ensureIndexes();
	}

	public void addOne(int i) {
		Random rand = new Random();
		int r = rand.nextInt(9999);

		Document num = new Document("name", "Random Number").append("value", r).append("count", i);

		collection.insertOne(num);
	}

	public void addOneJavaObj() {
		TestObj t = new TestObj();
		datastore.save(t);
	}

	public void compare() {
		int count = 100;
		List<TestObj> list = new ArrayList<TestObj>(count);
		for (int i = 0; i < count; i++) {
			list.add(new TestObj());
		}

		long startTime = 0;
		long endTime = 0;

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		startTime = System.nanoTime();
		for (TestObj t : list) {
			datastore.save(t);
		}
		endTime = System.nanoTime();
		System.out.println("Morphia\t" + (endTime - startTime) / 1000000000.0);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		startTime = System.nanoTime();
		for (TestObj t : list) {
			Document obj = new Document("className", t.getClass().getName()).append("d", t.d).append("i", t.i)
					.append("s", t.s);
			collection.insertOne(obj);
		}
		endTime = System.nanoTime();
		System.out.println("Document\t" + (endTime - startTime) / 1000000000.0);

	}

	public void close() {
		mongo.close();
	}

}
