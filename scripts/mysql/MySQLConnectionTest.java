import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import com.opencsv.CSVReader;

public class MySQLConnectionTest {

    // Paramètres de connexion
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "accident_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try {
            loadJDBCDriver();
            createDatabase();
            try (Connection conn = connectToDatabase()) {
                createTables(conn);
                String path="/vagrant/datasets/mysql/";
                insertFromCSV_AccidentDetails(conn, path+"Accident_Event_Details.csv");
                insertFromCSV_AccidentSummary(conn, path+"Accidents_Core_Facts.csv");
                insertFromCSV_WorkZoneInfo(conn, path+"Ref_Work_Zone_Status.csv");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Charger le driver JDBC
    private static void loadJDBCDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("✅ Driver JDBC chargé");
    }

    // 2. Créer la base de données si elle n'existe pas
    private static void createDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            System.out.println("✅ Base de données créée ou déjà existante");
        }
    }

    // 3. Connexion à la base de données
    private static Connection connectToDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(BASE_URL + DATABASE_NAME, USER, PASSWORD);
        System.out.println("✅ Connecté à la base de données " + DATABASE_NAME);
        return conn;
    }

    // 4. Créer les tables
    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            String createAccidentDetails = "CREATE TABLE IF NOT EXISTS accident_details (    OBJECTID INT, HARM_EV INT, HARM_EVNAME VARCHAR(255),    MAN_COLL INT, MAN_COLLNAME VARCHAR(255),    TYP_INT INT, TYP_INTNAME VARCHAR(255),    REL_ROAD INT, REL_ROADNAME VARCHAR(255))";
            String createAccidentSummary = "CREATE TABLE IF NOT EXISTS accident_summary (    OBJECTID INT, ST_CASE INT, FATALS INT, PERSONS INT,    VE_TOTAL INT, VE_FORMS INT, PEDS INT, PERNOTMVIT INT,    SCH_BUS INT, RAIL VARCHAR(20), WRK_ZONE INT)";
            String createWorkZoneInfo = "CREATE TABLE IF NOT EXISTS work_zone_info (    WRK_ZONE INT, WRK_ZONENAME VARCHAR(255))";
            stmt.executeUpdate(createAccidentDetails);
            stmt.executeUpdate(createAccidentSummary);
            stmt.executeUpdate(createWorkZoneInfo);

            System.out.println("✅ Tables créées");
        }
    }

    
    private static void insertFromCSV_AccidentDetails(Connection conn, String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath));
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO accident_details VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            String[] line;
            reader.readNext(); // Skip header
            int rowCount = 0;
            System.out.println("Debut de l'insertion accident_details depuis " + filePath);
            while ((line = reader.readNext()) != null) {
                for (int i = 0; i < 9; i++) ps.setString(i + 1, line[i]);
                ps.executeUpdate();
                rowCount++;
                System.out.println("Ligne insérée : " + (rowCount ));
            }
            System.out.println("✅ Données insérées dans accident_details depuis " + filePath);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'insertion accident_details");
            e.printStackTrace();
        }
    }

    private static void insertFromCSV_AccidentSummary(Connection conn, String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath));
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO accident_summary VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            String[] line;
            reader.readNext(); // Skip header
            int rowCount = 0;
            System.out.println("Debut de l'insertion accident_summary depuis " + filePath);
            while ((line = reader.readNext()) != null) {
                for (int i = 0; i < 11; i++) ps.setString(i + 1, line[i]);
                ps.executeUpdate();
                rowCount++;
                System.out.println("Ligne insérée : " + (rowCount ));
            }
            System.out.println("✅ Données insérées dans accident_summary depuis " + filePath);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'insertion accident_summary");
            e.printStackTrace();
        }
    }

    private static void insertFromCSV_WorkZoneInfo(Connection conn, String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath));
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO work_zone_info VALUES (?, ?)")) {
            String[] line;
            reader.readNext(); // Skip header
            int rowCount = 0;
            System.out.println("Debut de l'insertion work_zone_info depuis " + filePath);
            while ((line = reader.readNext()) != null) {
                ps.setInt(1, Integer.parseInt(line[0]));
                ps.setString(2, line[1]);
                ps.executeUpdate();
                rowCount++;
                System.out.println("Ligne insérée : " + (rowCount ));
            }
            System.out.println("✅ Données insérées dans work_zone_info depuis " + filePath);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'insertion work_zone_info");
            e.printStackTrace();
        }
    }
}
