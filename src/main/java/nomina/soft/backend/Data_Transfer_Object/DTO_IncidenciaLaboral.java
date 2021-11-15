package nomina.soft.backend.Data_Transfer_Object;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class DTO_IncidenciaLaboral {
    @Getter @Setter private Long idIncidenciaLaboral;
    @Getter @Setter private int totalHorasDeFalta;
    @Getter @Setter private int totalHorasExtras;
    @Getter @Setter private Long idContrato;
    @Getter @Setter private Long idPeriodoNomina;
}
