package es.codevert;

import java.io.IOException;

import javafx.scene.input.MouseEvent;

public class MainMenuController {

    public void openFileMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("file_app");
    }

    public void openDBMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("db_app");
    }

    public void openApiMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("api_app");
    }
}
