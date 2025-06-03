import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HBaseCSVImporter {

    private static final String TABLE_NAME = "Accident_Response_Times";
    private static final String COLUMN_FAMILY = "info";
    private static final String CSV_PATH = "/vagrant/datasets/hbase/Accident_Response_Times.csv";

    public static void main(String[] args) {
        try (Connection connection = connectToHBase()) {
            insertCsvDataIntoHBase(connection);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Échec lors de la connexion ou de l'insertion.");
        }
    }

    private static Connection connectToHBase() throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "localhost");
        config.set("hbase.zookeeper.property.clientPort", "2181");

        return ConnectionFactory.createConnection(config);
    }

    private static void insertCsvDataIntoHBase(Connection connection) {
        try (Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
             BufferedReader reader = new BufferedReader(new FileReader(CSV_PATH))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // skip header
                }

                Put put = buildPut(line);
                if (put != null) {
                    table.put(put);
                    System.out.println("✅ Ligne insérée : " + Bytes.toString(put.getRow()));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de la lecture du CSV ou de l'insertion.");
        }
    }

    private static Put buildPut(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 5) return null;

        String objectId = parts[0].trim();
        String rowKey = "row" + objectId;

        Put put = new Put(Bytes.toBytes(rowKey));
        try {
            put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes("OBJECTID"), Bytes.toBytes(Integer.parseInt(parts[0])));
            put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes("ARR_HOUR"), Bytes.toBytes(Integer.parseInt(parts[1])));
            put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes("ARR_MIN"), Bytes.toBytes(Integer.parseInt(parts[2])));
            put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes("HOSP_HR"), Bytes.toBytes(Integer.parseInt(parts[3])));
            put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes("HOSP_MN"), Bytes.toBytes(Integer.parseInt(parts[4])));
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Ligne ignorée (mauvais format) : " + csvLine);
            return null;
        }

        return put;
    }
}
