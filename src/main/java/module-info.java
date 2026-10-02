module ni.edu.uam.practicajavafxypostresql {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.practicajavafxypostresql to javafx.fxml;
    exports ni.edu.uam.practicajavafxypostresql;
}