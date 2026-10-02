module ni.edu.uam.practicajavafxypostresql {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires java.desktop;


    opens ni.edu.uam.practicajavafxypostresql to javafx.fxml;
    exports ni.edu.uam.practicajavafxypostresql;
}