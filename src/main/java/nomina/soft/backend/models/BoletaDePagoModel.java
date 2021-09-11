package nomina.soft.backend.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "boleta_de_pago")
@AllArgsConstructor
@NoArgsConstructor
public class BoletaDePagoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nomina_id")
    @Getter @Setter
    private NominaModel nomina;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contrato_id")
    @Getter @Setter
    private ContratoModel contrato;






}
