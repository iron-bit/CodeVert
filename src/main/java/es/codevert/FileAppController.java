package es.codevert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

import com.github.ironbit.CodeVertFile;
import com.github.ironbit.FileConverter;
import com.github.ironbit.FileExtension;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.stage.Stage;

public class FileAppController {

    // Graphics elements
    @FXML
    private Label labelName, labelFile, labelFormat, labelWeight, labelEta;

    @FXML
    private ImageView dragSquare, myGifPane;

    @FXML
    private GridPane gridTocho, rightGrid;

    @FXML
    private HBox elQueDesaparece;

    @FXML
    private ComboBox<String> comboBox;

    // Class variables
    private final FileConverter CONVERTER = new FileConverter();
    private CodeVertFile codevertFile;

    private final String IMAGES_DIR = "/es/codevert/images/";

    private String selectedFilter = null;
    private String selectAllLabel = "Seleccionar Todo";

    public void initFileChooser(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        processFile(selectedFile);
    }

    @FXML
    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleDrop(DragEvent dragEvent) {
        List<File> filesList = dragEvent.getDragboard().getFiles();
        File selectedFile = filesList.get(0);

        processFile(selectedFile);
    }

    public void processFile(File selectedFile) {
        try {
            this.codevertFile = this.CONVERTER.prepareFile(selectedFile);

            // Add filters to comboBox
            this.comboBox.getItems().add(this.selectAllLabel);
            ArrayList<String> filters = this.codevertFile.getKeys();
            filters.forEach(filter -> {
                comboBox.getItems().add(filter);
                System.out.println(filter);
            });
            this.comboBox.getSelectionModel().select(0);

            changeCheckStatusGIF();
            handleContainerTransition(selectedFile);
        } catch (Exception e) { // Should be CodeVertException
            changeErrorStatusGIF();
        }
    }

    private void handleContainerTransition(File selectedFile) {
        Timeline delay;

        delay = new Timeline(new KeyFrame(Duration.seconds(2.5), event -> {
            vanishMainLayout(160, 5);

            elQueDesaparece.setDisable(true);
            gridTocho.setDisable(false);

            disableExtensionButton();

            makesConverterAppear(160, 10);

            String fileName = this.codevertFile.getFileName();
            labelFile.setText(fileName);

            FileExtension fileExtension = this.codevertFile.getFileExtension();
            labelFormat.setText("Extension: " + fileExtension.toString());

            try {
                Path path = Paths.get(selectedFile.getPath());
                long fileWeight = Files.size(path);
                labelWeight.setText("Size: " + fileWeight + " bytes");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int eta = generateRandomETA();
            labelEta.setText("ETA: " + eta + "ms");
        }));

        delay.play();

        moveDownGif(100, 15);
    }

    private int generateRandomETA() {
        Random random = new Random();
        return random.nextInt(300);
    }

    private void changeErrorStatusGIF() {
        String errorGIF = this.IMAGES_DIR + "error.gif";
        String errorPNG = this.IMAGES_DIR + "error.png";

        Image errorGif = new Image(Objects.requireNonNull(getClass().getResourceAsStream(errorGIF)));
        dragSquare.setImage(errorGif);

        Image error = new Image(Objects.requireNonNull(getClass().getResourceAsStream(errorPNG)));
        waitImage(error, 1800);

        labelName.setStyle("-fx-text-fill: red;");
        labelName.setText("File extension not compatible");
    }

    private void changeCheckStatusGIF() {
        String checkGIF = this.IMAGES_DIR + "check.gif";
        String checkPNG = this.IMAGES_DIR + "check.png";

        Image gif = new Image(Objects.requireNonNull(getClass().getResourceAsStream(checkGIF)));
        dragSquare.setImage(gif);

        Image check = new Image(Objects.requireNonNull(getClass().getResourceAsStream(checkPNG)));
        waitImage(check, 1800);

        labelName.setStyle("-fx-text-fill: black;");
        labelName.setText(this.codevertFile.getFileName());
    }

    private void disableExtensionButton() {
        ObservableList<Node> children = rightGrid.getChildren();

        children.forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                String buttonText = getButtonText(button);
                String fileExtension = this.codevertFile.getFileExtension().toString().toLowerCase();

                if (buttonText.equals(fileExtension)) {
                    button.setDisable(true);
                }
            }
        });
    }

    private String getButtonText(Button button) {
        return button.getText().toLowerCase();
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

    private void vanishMainLayout(int stepsOff, int durationOff) {
        Timeline vanishTimeline = new Timeline();

        for (int i = 0; i <= stepsOff; i++) {
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * durationOff),
                    event -> {
                        double newOpacity = this.elQueDesaparece.getOpacity() - 1.0 /stepsOff;
                        changePaneOpacity(this.elQueDesaparece, newOpacity);
                    }

            );
            vanishTimeline.getKeyFrames().add(keyFrame);
        }
        vanishTimeline.play();
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

    private void waitImage(Image image, int millis) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(millis),
                event -> dragSquare.setImage(image)
        ));
        timeline.play();
    }

    private String getSelectedFilter() {
        this.selectedFilter = this.comboBox.getValue();

        if (this.selectedFilter == null || this.selectedFilter.equals(this.selectAllLabel)) {
           return null;
        }

        return this.selectedFilter;
    }

    public void convertToXML(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convert(codevertFile, FileExtension.XML, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }

    public void convertToJSON(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convert(codevertFile, FileExtension.JSON, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }

    public void convertToCSV(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convert(codevertFile, FileExtension.CSV, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }

    public void convertToTXT(ActionEvent actionEvent) {
        String selectedFilter = getSelectedFilter();
        String convertedFileRoute = CONVERTER.convert(codevertFile, FileExtension.TXT, selectedFilter);
        OpenFileController.openFile(convertedFileRoute);
    }
}
