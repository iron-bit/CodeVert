package es.codevert;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.ArrayList;

import com.github.ironbit.CodeVertFile;
import com.github.ironbit.FileConverter;
import com.github.ironbit.FileExtension;
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


    private final FileConverter CONVERTER = new FileConverter();
    private String pathTempJSON = null;
    private File tempJSON = null;
    private CodeVertFile codevertFile;

    private String selectAllLabel = "Seleccionar Todo";
    private String selectedFilter = null;


    public void requestApiButton(ActionEvent actionEvent) throws IOException {
        if (checkCredentials()) {
            handleContainerTransition();

            //
            this.tempJSON = new File(this.pathTempJSON);
            this.codevertFile = CONVERTER.prepareFile(this.tempJSON);

            this.comboBox.getItems().add(this.selectAllLabel);
            ArrayList<String> filters = this.codevertFile.getKeys();
            System.out.println(filters);
            filters.forEach(filter -> {
                this.comboBox.getItems().add(filter);
                System.out.println(filter);
            });
            this.comboBox.getSelectionModel().select(0);

        } else {
            changeErrorBorder();
        }
    }

    private boolean checkCredentials() throws IOException {
        String url = this.urlLabel.getText();
        this.pathTempJSON = fetchAndSaveJson(url);

        return !url.isBlank() && this.pathTempJSON != null;
    }

    public static String fetchAndSaveJson(String urlString) {
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
            return tempFile.toAbsolutePath().toString();
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

        return null;
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

    private String getSelectedFilter() {
        this.selectedFilter = this.comboBox.getValue();

        if (this.selectedFilter == null || this.selectedFilter.equals(this.selectAllLabel)) {
            return null;
        }

        return this.selectedFilter;
    }

    public void convertToTXT(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convert(this.codevertFile, FileExtension.TXT, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }

    public void convertToCSV(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convert(this.codevertFile, FileExtension.CSV, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }

    public void convertToJSON(ActionEvent actionEvent) {
        // TODO
    }

    public void convertToXML(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convert(this.codevertFile, FileExtension.XML, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }
}