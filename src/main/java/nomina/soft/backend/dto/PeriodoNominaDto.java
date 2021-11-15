package nomina.soft.backend.dto;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class PeriodoNominaDto {
    @Getter @Setter private Long idPeriodoNomina;
    @Getter @Setter private String descripcion;
    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin;
    
}
