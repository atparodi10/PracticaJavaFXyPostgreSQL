package ni.edu.uam.practicajavafxypostresql.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import ni.edu.uam.practicajavafxypostresql.database.DataBaseConnection;
import ni.edu.uam.practicajavafxypostresql.model.Empleado;

import java.sql.*;
import java.time.LocalDate;


public class EmpleadoController {
    @FXML
    private TextField txtNombres;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtCedula;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCargo;

    @FXML
    private ComboBox<String> cmbDepartamento;

    @FXML
    private TextField txtSalario;

    @FXML
    private DatePicker dpFechaContratacion;

    @FXML
    private ComboBox<String> cmbEstado;

    @FXML
    private TableView<Empleado> tblEmpleado;

    @FXML
    private TableColumn<Empleado, Integer> colId;

    @FXML
    private TableColumn<Empleado, String> colNombres;

    @FXML
    private TableColumn<Empleado, String> colApellidos;

    @FXML
    private TableColumn<Empleado, String> colCedula;

    @FXML
    private TableColumn<Empleado, String> colCorreo;

    @FXML
    private TableColumn<Empleado, String> colTelefono;

    @FXML
    private TableColumn<Empleado, String> colCargo;

    @FXML
    private TableColumn<Empleado, Double> colSalario;

    @FXML
    private TableColumn<Empleado, LocalDate> colFechaContratacion;

    @FXML
    private TableColumn<Empleado, String> colEstado;

    @FXML
    private ComboBox<String> cmbConsultas;

    private final ObservableList<Empleado> empleados = FXCollections.observableArrayList();

    @FXML
    private void cargarEmpleados(){
        empleados.clear();

        String sql = "SELECT * FROM empleado";

        try{
            Connection connection = DataBaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Empleado empleado = new Empleado();
                empleado.setId(resultSet.getInt("id"));
                empleado.setNombres(resultSet.getString("nombres"));
                empleado.setApellidos(resultSet.getString("apellidos"));
                empleado.setCedula(resultSet.getString("cedula"));
                empleado.setCorreo(resultSet.getString("correo"));
                empleado.setTelefono(resultSet.getString("telefono"));
                empleado.setCargo(resultSet.getString("cargo"));
                empleado.setDepartamento(resultSet.getString("departamento"));
                empleado.setSalario(resultSet.getDouble("salario"));

                empleado.setFechaContracion(Date.valueOf(resultSet.getObject("fecha_contratacion", LocalDate.class)));

                empleado.setEstado(resultSet.getString("estado"));

                empleados.add(empleado);
            }
            tblEmpleado.setItems(empleados);

        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void GuardarEmpleado(){
        if(!validarCampos()){
            return;
        }


        String sql = "INSERT INTO empleado(nombres, apellidos, cedula, correo, telefono, cargo, departamento, salario, fecha_contratacion, estado) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, txtNombres.getText());
            statement.setString(2, txtApellidos.getText());
            statement.setString(3, txtCedula.getText());
            statement.setString(4, txtCorreo.getText());
            statement.setString(5, txtTelefono.getText());
            statement.setString(6, txtCargo.getText());
            statement.setString(7, cmbDepartamento.getValue());
            statement.setDouble(8, Double.parseDouble(txtSalario.getText()));
            statement.setObject(9, dpFechaContratacion.getValue()); // Mandamos el LocalDate directo
            statement.setString(10, cmbEstado.getValue()); // Mandamos el String seleccionado

            statement.execute();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro Almacenado",
                    "Empleado Almacenado",
                    "El empleado se ha almacenado correctamente."
            );

            cargarEmpleados();
            limpiarCampos();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Error al guardar", ex.getMessage());
        }
    }

    @FXML
    private void ejecutarConsulta() {
        String seleccion = cmbConsultas.getValue();

        if (seleccion == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Selección requerida", "Por favor seleccione una consulta del menú.");
            return;
        }

        switch (seleccion) {
            case "Mostrar todos los empleados":
                cargarEmpleadosGenerico("SELECT * FROM empleado");
                break;
            case "Mostrar nombres, apellidos y cargo":
                mostrarNombresApellidosCargo();
                break;
            case "Mostrar empleados de Administración":
                cargarEmpleadosGenerico("SELECT * FROM empleado WHERE departamento = 'Administración'");
                break;
            case "Mostrar empleados con salario mayor a 20000":
                cargarEmpleadosGenerico("SELECT * FROM empleado WHERE salario > 20000");
                break;
            case "Ordenar empleados por salario de mayor a menor":
                cargarEmpleadosGenerico("SELECT * FROM empleado ORDER BY salario DESC");
                break;
            case "Contar cantidad total de empleados":
                mostrarResultadoEscalar("SELECT COUNT(*) AS resultado FROM empleado", "Cantidad total de empleados registrados:");
                break;
            case "Obtener salario promedio":
                mostrarResultadoEscalar("SELECT AVG(salario) AS resultado FROM empleado", "El salario promedio es: C$ ");
                break;
            case "Obtener suma de los salarios":
                mostrarResultadoEscalar("SELECT SUM(salario) AS resultado FROM empleado", "La suma de todos los salarios es: C$ ");
                break;
            case "Mostrar empleados activos":
                cargarEmpleadosGenerico("SELECT * FROM empleado WHERE estado = 'Activo'");
                break;
            case "Agrupar empleados por departamento":
                mostrarAgrupacionDepartamentos();
                break;
            default:
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Consulta no reconocida", "La consulta seleccionada no existe.");
                break;
        }
    }

    // --- MÉTODOS AUXILIARES PARA LAS CONSULTAS ---

    private void cargarEmpleadosGenerico(String sql) {
        empleados.clear();
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()){
                Empleado empleado = new Empleado();
                empleado.setId(resultSet.getInt("id"));
                empleado.setNombres(resultSet.getString("nombres"));
                empleado.setApellidos(resultSet.getString("apellidos"));
                empleado.setCedula(resultSet.getString("cedula"));
                empleado.setCorreo(resultSet.getString("correo"));
                empleado.setTelefono(resultSet.getString("telefono"));
                empleado.setCargo(resultSet.getString("cargo"));
                empleado.setDepartamento(resultSet.getString("departamento"));
                empleado.setSalario(resultSet.getDouble("salario"));
                empleado.setFechaContracion(Date.valueOf(resultSet.getObject("fecha_contratacion", LocalDate.class)));
                empleado.setEstado(resultSet.getString("estado"));
                empleados.add(empleado);
            }
            tblEmpleado.setItems(empleados);

        } catch (SQLException ex){
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "Fallo al ejecutar consulta", ex.getMessage());
        }
    }

    private void mostrarNombresApellidosCargo() {
        empleados.clear();
        String sql = "SELECT nombres, apellidos, cargo FROM empleado";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()){
                Empleado empleado = new Empleado();
                empleado.setNombres(resultSet.getString("nombres"));
                empleado.setApellidos(resultSet.getString("apellidos"));
                empleado.setCargo(resultSet.getString("cargo"));
                // Solo llenamos esos 3. El resto aparecerá nulo/vacío en tu TableView, lo cual es correcto.
                empleados.add(empleado);
            }
            tblEmpleado.setItems(empleados);

        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    private void mostrarResultadoEscalar(String sql, String mensajeTexto) {
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                // Obtenemos la columna calculada, la traemos como String para más facilidad (así abarca int o double)
                String resultado = resultSet.getString("resultado");

                // Formateamos si es decimal para no ver números demasiado largos
                if (resultado != null && resultado.contains(".")) {
                    double num = Double.parseDouble(resultado);
                    resultado = String.format("%.2f", num);
                }

                mostrarAlerta(Alert.AlertType.INFORMATION, "Resultado Matemático", "Cálculo Exitoso", mensajeTexto + " " + resultado);
            }

        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    private void mostrarAgrupacionDepartamentos() {
        String sql = "SELECT departamento, COUNT(*) AS cantidad_empleados FROM empleado GROUP BY departamento ORDER BY departamento";
        StringBuilder textoAgrupado = new StringBuilder("Empleados por Departamento:\n\n");

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()){
                String depto = resultSet.getString("departamento");
                int cantidad = resultSet.getInt("cantidad_empleados");
                textoAgrupado.append("• ").append(depto).append(": ").append(cantidad).append(" empleado(s)\n");
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Reporte por Departamento", "Agrupación Exitosa", textoAgrupado.toString());

        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtNombres.clear();
        txtApellidos.clear();
        txtCedula.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtCargo.clear();
        txtSalario.clear();
        cmbDepartamento.setValue(null);
        cmbEstado.setValue(null);
        dpFechaContratacion.setValue(null);
    }

    private void mostrarAlerta(Alert.AlertType tipoAlerta, String titulo, String header, String mensaje) {
        Alert alert = new Alert(tipoAlerta);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean validarCampos() {
        if (txtNombres.getText() == null || txtNombres.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Campo vacío", "Los nombres son obligatorios.");
            return false;
        } else if (!txtNombres.getText().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Formato incorrecto", "Los nombres solo pueden contener letras.");
            return false;
        }

        if (txtApellidos.getText() == null || txtApellidos.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Campo vacío", "Los apellidos son obligatorios.");
            return false;
        } else if (!txtApellidos.getText().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Formato incorrecto", "Los apellidos solo pueden contener letras.");
            return false;
        }

        if (txtCedula.getText() == null || txtCedula.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Campo vacío", "La cédula es obligatoria.");
            return false;
        } else if (!txtCedula.getText().matches("^\\d{3}-?\\d{6}-?\\d{4}[A-Za-z]$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Formato incorrecto", "La cédula debe tener 13 dígitos y una letra al final.");
            return false;
        }

        if (txtCorreo.getText() == null || txtCorreo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Campo vacío", "El correo es obligatorio.");
            return false;
        } else if (!txtCorreo.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Formato incorrecto", "Ingrese un correo electrónico válido.");
            return false;
        }

        if (txtTelefono.getText() == null || txtTelefono.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Campo vacío", "El teléfono es obligatorio.");
            return false;
        } else if (!txtTelefono.getText().matches("^\\d{4}-?\\d{4}$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Formato incorrecto", "El teléfono debe contener exactamente 8 dígitos.");
            return false;
        }

        if (txtCargo.getText() == null || txtCargo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Campo vacío", "El cargo es obligatorio.");
            return false;
        } else if (!txtCargo.getText().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Formato incorrecto", "El cargo solo puede contener letras.");
            return false;
        }

        if (cmbDepartamento.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Selección requerida", "Por favor seleccione un departamento.");
            return false;
        }

        if (txtSalario.getText() == null || txtSalario.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Campo vacío", "El salario es obligatorio.");
            return false;
        }
        try {
            double salario = Double.parseDouble(txtSalario.getText());
            if (salario < 6188.00) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Salario inferior al mínimo", "El salario no puede ser menor a C$ 6,188.00.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Validación", "Formato incorrecto", "El salario debe ser un número válido (Ejemplo: 7500.50).");
            return false;
        }

        if (dpFechaContratacion.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Campo vacío", "La fecha de contratación es obligatoria.");
            return false;
        } else {
            LocalDate fechaSeleccionada = dpFechaContratacion.getValue();
            LocalDate fechaInicioEmpresa = LocalDate.of(2015, 10, 1);

            if (fechaSeleccionada.isAfter(LocalDate.now())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Fecha inválida", "La fecha de contratación no puede estar en el futuro.");
                return false;
            }
            if (fechaSeleccionada.isBefore(fechaInicioEmpresa)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Fecha inválida", "La fecha de contratación no puede ser anterior a octubre de 2015.");
                return false;
            }
        }

        String estado = cmbEstado.getValue();
        if (estado == null || estado.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Selección requerida", "Por favor seleccione un estado.");
            return false;
        } else if (!estado.equals("Activo") && !estado.equals("Inactivo")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Estado inválido", "El estado solo puede ser 'Activo' o 'Inactivo'.");
            return false;
        }

        return true;
    }



}
