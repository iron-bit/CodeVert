module es.codevert {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.github.ironbit;
    requires java.sql;
    requires mysql.connector.j;

    opens es.codevert to javafx.fxml;
    exports es.codevert;
}
