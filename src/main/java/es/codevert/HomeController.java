package es.codevert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.github.ironbit.FileConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

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
    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleDrop(DragEvent dragEvent) {
        List<File> files = dragEvent.getDragboard().getFiles();
        if (!files.isEmpty()) {
            File file = files.get(0);
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".txt") || fileName.endsWith(".csv") || fileName.endsWith(".json") || fileName.endsWith(".xml")) {
                Image gif = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/check.gif")));
                dragSquare.setImage(gif);
                Image check = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/check.png")));
                waitImage(check, 1800);
                labelName.setStyle("-fx-text-fill: black;");
                labelName.setText(fileName);

                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2.5), ae -> {
                    vanishMainLayout(160, 5);
                    elQueDesaparece.setDisable(true);
                    gridTocho.setDisable(false);
                    disableExtensionButton(file);

                    makesConverterAppear(160, 10);

                    labelFile.setText(fileName.substring(0, fileName.indexOf('.')));
                    labelFormat.setText("Extensión: " + fileName.toLowerCase().substring(fileName.lastIndexOf('.')));
                    Path path = Paths.get(file.getPath());
                    try {
                        labelWeight.setText("Tamaño: " + Files.size(path) + " bytes");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Random rnd = new Random();
                    int eta = 5 + rnd.nextInt(10);
                    labelEta.setText("Tiempo estimado: " + eta + "s");
                }));
                delay.play();

                moveDownGif(100, 15);

                FileConverter converter = new FileConverter();
            } else {
                Image errorGif = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/error.gif")));
                dragSquare.setImage(errorGif);
                Image error = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/es/codevert/images/error.png")));
                waitImage(error, 1800);
                labelName.setStyle("-fx-text-fill: red;");
                labelName.setText("Archivo con extensión " + fileName.substring(fileName.lastIndexOf('.')) + " no compatible");
            }
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

    public void convertButtonAction(ActionEvent actionEvent) {
        System.out.println("Code the api logic");
    }
}
