package nomina.soft.backend.Data_Transfer_Object;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class DTO_PeriodoNomina {
    @Getter @Setter private Long idPeriodoNomina;
    @Getter @Setter private String descripcion;
    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin;
    
}
