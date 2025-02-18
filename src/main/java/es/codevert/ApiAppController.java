package es.codevert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ApiAppController {

    public TextField urlLabel;
    public HBox elQueDesaparece;
    public GridPane gridTocho;
    public GridPane rightGrid;
    public ImageView myGifPane;
    public Label labelDBName;
    public ComboBox<String> comboBox;

    public void requestApiButton(ActionEvent actionEvent) throws IOException {
        if (checkCredentials()) {
            handleContainerTransition();
        } else {
            changeErrorBorder();
        }
    }

    private boolean checkCredentials() throws IOException {
        // TODO Complete combobox
        String[] test = {"All", "1", "2", "3"};
        for (String t : test) {
            comboBox.getItems().add(t);
        }

        String url = this.urlLabel.getText();
        fetchAndSaveJson(url);

        if (url.isBlank()) {
            return true;
        }

        return false;
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

    private void handleContainerTransition() {
        Timeline delay;

        delay = new Timeline(new KeyFrame(Duration.seconds(2.5), event -> {
            vanishMainLayout(160, 5);

            elQueDesaparece.setDisable(true);
            gridTocho.setDisable(false);

            makesConverterAppear(160, 10);
        }));

        delay.play();

        moveDownGif(100, 15);
    }

    private void vanishMainLayout(int stepsOff, int durationOff) {
        Timeline vanishTimeline = new Timeline();

        for (int i = 0; i <= stepsOff; i++) {
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * durationOff),
                    event -> {
                        double newOpacity = this.elQueDesaparece.getOpacity() - 1.0 / stepsOff;
                        changePaneOpacity(this.elQueDesaparece, newOpacity);
                    }

            );
            vanishTimeline.getKeyFrames().add(keyFrame);
        }
        vanishTimeline.play();
    }

    private void makesConverterAppear(int stepsOn, int durationOn) {
        Timeline appearTimeline = new Timeline();

        for (int i = 0; i <= stepsOn; i++) {
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * durationOn),
                    event -> {
                        double newOpacity = this.gridTocho.getOpacity() + 1.0 / stepsOn;
                        changePaneOpacity(this.gridTocho, newOpacity);
                    }
            );
            appearTimeline.getKeyFrames().add(keyFrame);
        }
        appearTimeline.play();
    }

    private void changePaneOpacity(Pane pane, double value) {
        pane.setOpacity(value);
    }

    private void moveDownGif(int stepsDown, int durationDown) {
        Timeline moveDownTimeline = new Timeline();

        for (int i = 0; i <= stepsDown; i++) {
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * durationDown),
                    event -> {
                        double newPosition = this.myGifPane.getTranslateY() + 1;
                        myGifPane.setTranslateY(newPosition);
                    }
            );
            moveDownTimeline.getKeyFrames().add(keyFrame);
        }
        moveDownTimeline.play();
    }

    public void changeErrorBorder() {
        this.urlLabel.getStyleClass().add("error");
    }

    public void returnOriginalBorder(KeyEvent keyEvent) {
        this.urlLabel.getStyleClass().remove("error");
    }
}