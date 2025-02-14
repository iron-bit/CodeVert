package es.codevert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.stage.Stage;

public class HomeController {

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

    private final static FileConverter converter = new FileConverter();
    private CodeVertFile myCVF;

    public void chooseFile(MouseEvent mouseEvent) {
        String[] test = {"All", "1", "2", "3"};
        for (String t : test) {
            comboBox.getItems().add(t);
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        try {
            this.myCVF = converter.prepareFile(selectedFile);
            Image gif = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/check.gif")));
            dragSquare.setImage(gif);
            Image check = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/check.png")));
            waitImage(check, 1800);
            labelName.setStyle("-fx-text-fill: black;");
            labelName.setText(myCVF.getFileName());

            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2.5), ae -> {
                vanishMainLayout(160, 5);
                elQueDesaparece.setDisable(true);
                gridTocho.setDisable(false);
                disableExtensionButton(selectedFile);

                makesConverterAppear(160, 10);

                labelFile.setText(myCVF.getFileName());
                labelFormat.setText("Extensión: " + myCVF.getFileExtension());
                Path path = Paths.get(selectedFile.getPath());
                try {
                    labelWeight.setText("Tamaño: " + Files.size(path) + " bytes");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Random rnd = new Random();
                int eta = 100 + rnd.nextInt(200);
                labelEta.setText("Tiempo estimado: " + eta + "ms");
            }));
            delay.play();

            moveDownGif(100, 15);

        } catch (Exception e) {
            Image errorGif = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/error.gif")));
            dragSquare.setImage(errorGif);
            Image error = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/error.png")));
            waitImage(error, 1800);
            labelName.setStyle("-fx-text-fill: red;");
            labelName.setText("Archivo con extensión no compatible");
        }
    }

    @FXML
    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleDrop(DragEvent dragEvent) {
        List<File> files = dragEvent.getDragboard().getFiles();
        File file = files.get(0);
        try {
            this.myCVF = converter.prepareFile(file);
/*
                Set<FileExtension> fileExtensions = converter.getCompatibleExtensions(myCVF);
*/
            Image gif = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/check.gif")));
            dragSquare.setImage(gif);
            Image check = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/check.png")));
            waitImage(check, 1800);
            labelName.setStyle("-fx-text-fill: black;");
            labelName.setText(myCVF.getFileName());

            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2.5), ae -> {
                vanishMainLayout(160, 5);
                elQueDesaparece.setDisable(true);
                gridTocho.setDisable(false);
                disableExtensionButton(file);

                makesConverterAppear(160, 10);

                labelFile.setText(myCVF.getFileName());
                labelFormat.setText("Extensión: " + myCVF.getFileExtension());
                Path path = Paths.get(file.getPath());
                try {
                    labelWeight.setText("Tamaño: " + Files.size(path) + " bytes");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Random rnd = new Random();
                int eta = 5 + rnd.nextInt(10);
                labelEta.setText("Tiempo estimado: " + eta + "ms");
            }));
            delay.play();

            moveDownGif(100, 15);

        } catch (Exception e) {
            Image errorGif = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/error.gif")));
            dragSquare.setImage(errorGif);
            Image error = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/error.png")));
            waitImage(error, 1800);
            labelName.setStyle("-fx-text-fill: red;");
            labelName.setText("Archivo con extensión no compatible");
        }
    }

    private void disableExtensionButton(File file) {
        ObservableList<Node> children = rightGrid.getChildren();
        for (Node node : children) {
            if (node instanceof Button) {
                Button button = (Button) node;
                String buttonText = button.getText().toLowerCase();
                String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1).toLowerCase();
                if (buttonText.equals(fileExtension)) {
                    button.setDisable(true);
                    break;
                }
            }
        }
    }

    private void makesConverterAppear(int stepsOn, int durationOn) {
        Timeline appear = new Timeline();
        for (int i = 0; i <= stepsOn; i++) {
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * durationOn),
                    ae -> gridTocho.setOpacity(gridTocho.getOpacity() + 1.0 / stepsOn)
            );
            appear.getKeyFrames().add(keyFrame);
        }
        appear.play();
    }

    private void vanishMainLayout(int stepsOff, int durationOff) {
        Timeline vanish = new Timeline();
        for (int i = 0; i <= stepsOff; i++) {
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * durationOff),
                    ae -> elQueDesaparece.setOpacity(elQueDesaparece.getOpacity() - 1.0 / stepsOff)
            );
            vanish.getKeyFrames().add(keyFrame);
        }
        vanish.play();
    }

    private void moveDownGif(int stepsDown, int durationDown) {
        Timeline moveDown = new Timeline();
        for (int i = 0; i <= stepsDown; i++) {
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * durationDown),
                    ae -> myGifPane.setTranslateY(myGifPane.getTranslateY() + 1)
            );
            moveDown.getKeyFrames().add(keyFrame);
        }
        moveDown.play();
    }

    private void waitImage(Image image, int millis) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(millis),
                ae -> dragSquare.setImage(image)
        ));
        timeline.play();
    }

    public void convertToXML(ActionEvent actionEvent) {
        System.out.println(actionEvent.getEventType());
        converter.convert(myCVF, FileExtension.XML, null);
    }

    public void convertToJSON(ActionEvent actionEvent) {
        converter.convert(myCVF, FileExtension.JSON, null);
    }

    public void convertToCSV(ActionEvent actionEvent) {
        converter.convert(myCVF, FileExtension.CSV, null);
    }

    public void convertToTXT(ActionEvent actionEvent) {
        converter.convert(myCVF, FileExtension.TXT, null);
    }

}
