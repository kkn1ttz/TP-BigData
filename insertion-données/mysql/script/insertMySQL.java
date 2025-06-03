import java.io.*;
import java.sql.*;
import java.util.*;

public class insertMySQL {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/vehicledb";
        String username = "root";
        String password = ""; 

        String csvFilePath = "/vagrant/TpBigData/tpmysql/script/Electric_Vehicle_Population_Data.csv";

        try (
            Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
            BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
        ) {
            String line;
            br.readLine(); // ignorerna lay ligne d'entete
            String sql = "INSERT INTO electric_vehicles VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); // CSV parsing avec guillemets

                for (int i = 0; i < 17; i++) {
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

            System.out.println("Données insérées avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
