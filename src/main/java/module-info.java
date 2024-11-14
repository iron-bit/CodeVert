module es.codevert {
    requires javafx.controls;
    requires javafx.fxml;

    opens es.codevert to javafx.fxml;
    exports es.codevert;
}
