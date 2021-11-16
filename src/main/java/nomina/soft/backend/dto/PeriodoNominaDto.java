package nomina.soft.backend.dto;
import static nomina.soft.backend.servicios.Utility.TIME_ZONE;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class PeriodoNominaDto {
    @Getter @Setter private Long idPeriodoNomina;
    @Getter @Setter private String descripcion;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone=TIME_ZONE)
    @Getter @Setter private Date fechaInicio;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone=TIME_ZONE)
    @Getter @Setter private Date fechaFin;
    
}
