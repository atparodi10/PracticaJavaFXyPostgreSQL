package ni.edu.uam.practicajavafxypostresql;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ni.edu.uam.practicajavafxypostresql.controller.EmpleadoController;

import java.io.IOException;

public class RegistroEmpleadoApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RegistroEmpleadoApplication.class.getResource("views/empleado-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Registro de Empleados");
        stage.setScene(scene);
        stage.show();
    }
}
