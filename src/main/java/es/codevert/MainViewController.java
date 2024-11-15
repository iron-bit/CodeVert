package es.codevert;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainViewController {
    public Label labelText;

    @FXML
    public void changeText(ActionEvent actionEvent) {
    labelText.setText("Victor Gay");
    }
}
