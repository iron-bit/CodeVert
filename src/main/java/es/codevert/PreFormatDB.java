package es.codevert;


import java.sql.*;
import java.util.*;

public class PreFormatDB {

    public static Map<String, Map<String, String>> getAllTablesAsCSVMap(Connection conn) throws SQLException {
        Map<String, Map<String, String>> dbContents = new LinkedHashMap<>();

        // Step 1: Get all table names
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

        // Step 2: Process each table
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            if (tableName.equals("sys_config")) {
                continue;
            }
            dbContents.put(tableName, getTableAsCSVMap(conn, tableName));
        }

        return dbContents;
    }

    private static Map<String, String> getTableAsCSVMap(Connection conn, String tableName) throws SQLException {
        Map<String, String> tableContents = new LinkedHashMap<>();

        // Step 3: Query the table
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Step 4: Store headers
        StringBuilder headerRow = new StringBuilder();
        for (int i = 1; i <= columnCount; i++) {
            headerRow.append(metaData.getColumnName(i));
            if (i < columnCount) headerRow.append(",");
        }
        tableContents.put("HEADER", headerRow.toString());

        // Step 5: Store rows
        int rowNumber = 1;
        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                row.append(rs.getString(i) != null ? rs.getString(i) : "");
                if (i < columnCount) row.append(",");
            }
            tableContents.put(String.valueOf(rowNumber++), row.toString());
        }

        rs.close();
        stmt.close();
        return tableContents;
    }

    public static void main(String[] args) {
        try {
            // Step 6: Connect to SQLite Database
            Connection conn = DriverManager.getConnection("jdbc:sqlite:your_database.db");

            // Step 7: Get all tables as CSV Map
            Map<String, Map<String, String>> csvData = getAllTablesAsCSVMap(conn);
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

