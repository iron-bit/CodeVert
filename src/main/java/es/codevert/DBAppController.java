package es.codevert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

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

public class DBAppController {

    public TextField urlLabel, userLabel, passwordLabel;
    public HBox elQueDesaparece;
    public GridPane gridTocho;
    public GridPane rightGrid;
    public ImageView myGifPane;
    public ComboBox<String> comboBox;
    public Label labelDBName, labelDBType, labelDBAlgo1, labelDBAlgo2;

    private Connection connection = null;

    private final FileConverter CONVERTER = new FileConverter();
    private Map<String, Map<String, String>> dbFile;

    private String selectedFilter = null;
    private String selectAllLabel = "Seleccionar Todo";


    public void connectToDBButton(ActionEvent actionEvent) {
        try (Connection c = checkCredentials();){
            if (c != null) {
                this.connection = c;
                handleContainerTransition();
                // TODO
            } else {
                changeErrorBorder();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Connection checkCredentials() {
        String url = this.urlLabel.getText();
        if (url.isBlank()) return null;
        String user = this.userLabel.getText();
        String password = this.passwordLabel.getText();

        if (url.contains("mysql")) {
            return connectToMySQLDB(url, user, password);
        } else if (url.contains("sqlite")) {
            return connectToSQLite(url);
        }
        return null;
    }

    private Connection connectToMySQLDB(String url, String user, String password) {
        try {
            Connection c = DriverManager.getConnection(url, user, password);
            System.out.println("Connection Established successfully");
            setLabelTexts(c);
            return c;
        } catch (Exception e) {
            System.err.println("Connection denied. Check your credentials, the driver connectors and the database status.");
        }
        return null;
    }

    private Connection connectToSQLite(String url) {
        try {
            Connection c = DriverManager.getConnection(url);
            System.out.println("Connection Established successfully");
            setLabelTexts(c);
            return c;
        } catch (Exception e) {
            System.err.println("Connection denied. Check your credentials, the driver connectors and the database status.");
        }
        return null;
    }

    private void setLabelTexts(Connection c) throws SQLException {
        labelDBName.setText(c.getCatalog());
        labelDBType.setText("Provider: " + c.getMetaData().getDatabaseProductName());
        labelDBAlgo1.setText("Driver name: " + c.getMetaData().getDriverName());
        labelDBAlgo2.setText("Driver version: " + c.getMetaData().getDriverVersion());
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

        //
        try {
            this.dbFile = PreFormatDB.getAllTablesAsCSVMap(this.connection);
            ArrayList<String> filtersList = new ArrayList<>(dbFile.keySet());

            this.comboBox.getItems().add(this.selectAllLabel);
            filtersList.forEach(filter -> {
                this.comboBox.getItems().add(filter);
            });
            this.comboBox.getSelectionModel().select(0);

        } catch (SQLException e) {
            System.out.println("Can't get tables from database: " + e.getMessage() );
        }


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

    /*public void changeErrorBorder() {
        this.urlLabel.setStyle("-fx-border-color: red");
        this.userLabel.setStyle("-fx-border-color: red");
        this.passwordLabel.setStyle("-fx-border-color: red");
    }*/

    public void changeErrorBorder() {
        this.urlLabel.getStyleClass().add("error");
        this.userLabel.getStyleClass().add("error");
        this.passwordLabel.getStyleClass().add("error");
    }

    public void returnOriginalBorder(KeyEvent keyEvent) {
        this.urlLabel.getStyleClass().remove("error");
        this.userLabel.getStyleClass().remove("error");
        this.passwordLabel.getStyleClass().remove("error");
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
        String convertedFileRoute = CONVERTER.convertMap(this.dbFile, FileExtension.TXT, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }

    public void convertToCSV(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convertMap(this.dbFile, FileExtension.CSV, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }

    public void convertToJSON(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convertMap(this.dbFile, FileExtension.JSON, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }

    public void convertToXML(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convertMap(this.dbFile, FileExtension.XML, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }
}