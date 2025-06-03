import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import org.bson.Document;

import java.io.FileReader;

public class MongoDBInsertFromCSV {

    public static void main(String[] args) {
        try {
            MongoClient mongoClient = connectToMongoDB("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("accident_db");
            String path="/vagrant/datasets/mongo/";
            insertCSVToCollection(database, path + "Accident_Environmental_Conditions.csv", "Accident_Environmental_Conditions");
            insertCSVToCollection(database, path + "Accident_Locations.csv", "Accident_Locations");

            mongoClient.close();
            System.out.println("✅ Données insérées avec succès !");
        } catch (Exception e) {
            System.err.println("❌ Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔹 Connexion à MongoDB
    private static MongoClient connectToMongoDB(String uri) {
        return new MongoClient(new MongoClientURI(uri));
    }

    // 🔹 Insertion d’un fichier CSV dans une collection MongoDB
    private static void insertCSVToCollection(MongoDatabase database, String csvFilePath, String collectionName) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            String[] headers = reader.readNext();
            String[] row;
             System.out.printf("🔄 Insertion des données du fichier %s dans la collection %s...\n", csvFilePath, collectionName);
            int count = 0;
            while ((row = reader.readNext()) != null) {
                Document doc = createDocumentFromRow(headers, row);
                collection.insertOne(doc);
                count++;
                System.out.printf("🔹 Ligne insérée : %d\n", count);
            }

            System.out.printf("✅ Fichier %s inséré dans la collection %s\n", csvFilePath, collectionName);

        } catch (Exception e) {
            System.err.printf("❌ Erreur d'insertion dans la collection %s : %s\n", collectionName, e.getMessage());
        }
    }

    // 🔹 Convertit une ligne CSV en Document MongoDB
    private static Document createDocumentFromRow(String[] headers, String[] row) {
        Document doc = new Document();
        for (int i = 0; i < headers.length; i++) {
            doc.append(headers[i], parseValue(row[i]));
        }
        return doc;
    }

    // 🔹 Convertit les valeurs en Integer/Double si possible
    private static Object parseValue(String value) {
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value.trim();
        }
    }
}
