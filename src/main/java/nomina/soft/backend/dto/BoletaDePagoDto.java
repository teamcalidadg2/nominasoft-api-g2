package nomina.soft.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class BoletaDePagoDto {
    @Getter @Setter
    private int boleta_id;
    @Getter @Setter
    private double sueldoBasico;
    @Getter @Setter
    private double asignacionFamiliar;
    @Getter @Setter
    private double montoPorHorasExtra;
    @Getter @Setter
    private double reintegros;
    @Getter @Setter
    private double movilidad;
    @Getter @Setter
    private double otrosIngresos;
    @Getter @Setter
    private double regimenPensionario;
    @Getter @Setter
    private double montoPorHorasDeFalta;
    @Getter @Setter
    private double adelantos;
    @Getter @Setter
    private double otrosDescuentos;
    @Getter @Setter
    private int nomina_id;
    @Getter @Setter
    private int contrato_id;
}
