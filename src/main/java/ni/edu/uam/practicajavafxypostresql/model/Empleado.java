package ni.edu.uam.practicajavafxypostresql.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Empleado {
    private int id;
    private String nombres;
    private String apellidos;
    private String cedula;
    private String correo;
    private String telefono;
    private String cargo;
    private String departamento;
    private double salario;
    private Date fechaContracion;
    private String estado;


}
