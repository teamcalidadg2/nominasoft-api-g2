package nomina.soft.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class IncidenciaLaboralDto {
    @Getter @Setter
    private int incidencia_laboral_id;
    @Getter @Setter
    private int totalHorasDeFalta;
    @Getter @Setter
    private int totalHorasExtras;
    @Getter @Setter
    private int contrato_id;
    @Getter @Setter
    private int periodo_nomina_id;
}
