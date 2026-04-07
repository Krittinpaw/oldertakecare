module com.elderlycare {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.elderlycare to javafx.fxml;
    exports com.elderlycare;
    exports com.elderlycare.ui;
}
