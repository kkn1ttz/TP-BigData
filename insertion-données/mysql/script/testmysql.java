import java.io.*;
import java.sql.*;

public class testmysql {

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/vehicledb?user=root&password=&useSSL=false&serverTimezone=UTC";
        String csvFilePath = "/vagrant/TpBigData/tpmysql/script/Electric_Vehicle_Population_Data.csv";
        String tableName = "electric_vehicles";

        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            createTableFromCSV(tableName, csvFilePath, conn);
            insertDataFromCSV(tableName, csvFilePath, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createTableFromCSV(String tableName, String csvPath, Connection conn) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(csvPath));
        String headerLine = br.readLine();
        br.close();

        String[] headers = headerLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");
        for (int i = 0; i < headers.length; i++) {
            String colName = headers[i].trim().replaceAll("[^a-zA-Z0-9_]", "_");
            sb.append("  `").append(colName).append("` VARCHAR(255)");
            if (i < headers.length - 1) sb.append(",\n");
        }
        sb.append("\n);");

        String sql = sb.toString();
        System.out.println("🔧 Génération de la table avec :\n" + sql);

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public static void insertDataFromCSV(String tableName, String csvPath, Connection conn) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(csvPath));
        String line = br.readLine(); // ignorerna lay ligne d'entete
        String[] headers = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // CSV parsing avec guillemets
        int columnCount = headers.length;

        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
        for (int i = 0; i < columnCount; i++) {
            query.append("?");
            if (i < columnCount - 1) query.append(", ");
        }
        query.append(")");

        PreparedStatement stmt = conn.prepareStatement(query.toString());

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            for (int i = 0; i < columnCount; i++) {
                if (i == 5 || i == 10 || i == 11) { // model_year, electric_range, base_msrp (INT)
                    if (data[i].isEmpty()) stmt.setNull(i + 1, java.sql.Types.INTEGER);
                    else stmt.setInt(i + 1, Integer.parseInt(data[i]));
                } else if (i == 13) { // dol_vehicle_id (identifiant lava be (BIGINT))
                    if (data[i].isEmpty()) stmt.setNull(i + 1, java.sql.Types.BIGINT);
                    else stmt.setLong(i + 1, Long.parseLong(data[i]));
                } else {
                    stmt.setString(i + 1, data[i]);
                }
            }

            stmt.executeUpdate();
        }

        br.close();
        stmt.close();
        System.out.println("Données insérées avec succès !");
    }
}
