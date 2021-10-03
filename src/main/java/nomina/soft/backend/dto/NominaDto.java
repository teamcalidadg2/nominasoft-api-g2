package nomina.soft.backend.dto;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nomina.soft.backend.models.BoletaDePagoModel;

@AllArgsConstructor
@NoArgsConstructor
public class NominaDto {
    @Getter @Setter private Long idNomina;
    @Getter @Setter private String descripcion;
    @Getter @Setter private Date fecha;
    @Getter @Setter private Boolean estaCerrada;
    @Getter @Setter private Long idPeriodoNomina;
	@Getter @Setter private List<BoletaDePagoModel> boletasDePago;
}
