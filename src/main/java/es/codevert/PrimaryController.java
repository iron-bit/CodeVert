package es.codevert;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class PrimaryController {

    public void openFileMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("home");
    }

    public void openDBMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("secondary");
    }

    public void openApiMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("home");
    }
}
