module es.codevert {
    requires javafx.controls;
    requires javafx.fxml;
    requires CodeVert.Api.bb47f2ec5c;

    opens es.codevert to javafx.fxml;
    exports es.codevert;
}
