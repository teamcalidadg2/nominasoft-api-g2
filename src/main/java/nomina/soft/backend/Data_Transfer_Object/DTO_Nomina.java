package nomina.soft.backend.Data_Transfer_Object;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class DTO_Nomina {
    @Getter @Setter private Long idNomina;
    @Getter @Setter private String descripcion;
    @Getter @Setter private Date fecha;
    @Getter @Setter private Boolean estaCerrada;
    @Getter @Setter private String idPeriodoNomina;

}
