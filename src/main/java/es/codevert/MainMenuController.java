package es.codevert;

import java.io.IOException;

import javafx.scene.input.MouseEvent;

public class MainMenuController {

    // Method to open the file menu
    public void openFileMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("file_app");
    }

    // Method to open the database menu
    public void openDBMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("db_app");
    }

    // Method to open the API menu
    public void openApiMenu(MouseEvent mouseEvent) throws IOException {
        App.setRoot("api_app");
    }
}
