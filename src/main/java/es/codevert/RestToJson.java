package es.codevert;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class RestToJson {
    public static void main(String[] args) {
        String apiUrl = "https://jsonplaceholder.typicode.com/todos/1"; // Example API
        fetchAndSaveJson(apiUrl);
    }

    public static void fetchAndSaveJson(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            // Create URL object
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Read response
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line).append("\n");
            }

            // Create a temporary file
            Path tempFile = Files.createTempFile("api_response", ".json");
            Files.write(tempFile, responseContent.toString().getBytes(), StandardOpenOption.WRITE);

            System.out.println("JSON response saved to: " + tempFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
