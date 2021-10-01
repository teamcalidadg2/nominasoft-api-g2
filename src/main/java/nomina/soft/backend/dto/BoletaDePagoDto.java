package nomina.soft.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class BoletaDePagoDto {
    @Getter @Setter private Long idBoleta;
    @Getter @Setter private float sueldoBasico;
    @Getter @Setter private float asignacionFamiliar;
    @Getter @Setter private float montoPorHorasExtra;
    @Getter @Setter private float reintegros;
    @Getter @Setter private float movilidad;
    @Getter @Setter private float otrosIngresos;
    @Getter @Setter private float regimenPensionario;
    @Getter @Setter private float montoPorHorasDeFalta;
    @Getter @Setter private float adelantos;
    @Getter @Setter private float otrosDescuentos;
    @Getter @Setter private float netoAPagar;
    
    @Getter @Setter private Long idNomina;
    @Getter @Setter private Long idContrato;
}
