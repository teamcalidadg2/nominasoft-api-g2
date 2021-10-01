package nomina.soft.backend.dto;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.models.ContratoModel;

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
    @Getter @Setter private List<ContratoModel> contratos;
}
