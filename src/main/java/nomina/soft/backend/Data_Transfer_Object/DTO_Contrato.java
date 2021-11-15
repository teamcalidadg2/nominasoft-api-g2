package nomina.soft.backend.Data_Transfer_Object;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nomina.soft.backend.Entidades.IncidenciaLaboral;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DTO_Contrato {
    @Getter @Setter private Long idContrato;
    @Getter @Setter private Date fechaInicio;
    @Getter @Setter private Date fechaFin; 
    @Getter @Setter private Boolean tieneAsignacionFamiliar;
    @Getter @Setter private String horasPorSemana;
    @Getter @Setter private String pagoPorHora;
    @Getter @Setter private String puesto;
    @Getter @Setter private Boolean estaCancelado;
    @Getter @Setter private String idEmpleado;
    @Getter @Setter private String idAfp;
	@Getter @Setter private List<IncidenciaLaboral> incidenciasLaborales;


}
