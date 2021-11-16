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
public class NominaDto {
    @Getter @Setter private Long idNomina;
    @Getter @Setter private String descripcion;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone=TIME_ZONE)
    @Getter @Setter private Date fecha;
    @Getter @Setter private Boolean estaCerrada;
    @Getter @Setter private String idPeriodoNomina;

}
