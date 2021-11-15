package nomina.soft.backend.dto;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoDto {
    @Getter @Setter private Long idEmpleado;
    @Getter @Setter private String nombres;
    @Getter @Setter private String apellidos;
    @Getter @Setter private String dni;
    @Getter @Setter private Date fechaNacimiento;
    @Getter @Setter private String telefono;
    @Getter @Setter private String correo;
    @Getter @Setter private String direccion;
    @Getter @Setter private Boolean estaActivo;

}
