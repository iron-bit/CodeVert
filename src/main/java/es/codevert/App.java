package es.codevert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.scene.image.Image;

import java.io.IOException;
import java.util.Objects;



public class App extends Application {

    private static Scene scene;

    // Configure and show the main window
    @Override
    public void start(Stage stage) throws IOException {
        String homeFXML = "main_menu";
        scene = new Scene(loadFXML(homeFXML));
        configureHomeWindow(stage);
        stage.show();
    }

    static void configureHomeWindow(Stage stage){
        // Set the application icon
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/favicon.png"))));
        stage.setScene(scene);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        // Set the window to full screen
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        // Make the window non-resizable
        stage.setResizable(false);
        stage.setTitle("CodeVert");
    }

    // Change the root of the scene
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Load the FXML file
    private static Parent loadFXML(String fxml) throws IOException {
        String prefix = "fxml/";
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(prefix + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // Launch the application
    public static void main(String[] args) {
        launch();
    }

}