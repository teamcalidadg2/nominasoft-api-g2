package nomina.soft.backend.models;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "boleta_de_pago")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoletaDePagoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idBoleta;

    @Getter @Setter private float sueldoBasico;
    @Getter @Setter private float asignacionFamiliar;
    @Getter @Setter private int montoPorHorasExtra;
    @Getter @Setter private float reintegros;
    @Getter @Setter private float movilidad;
    @Getter @Setter private float otrosIngresos;
    @Getter @Setter private float regimenPensionario;
    @Getter @Setter private float montoPorHorasDeFalta;
    @Getter @Setter private float adelantos;
    @Getter @Setter private float otrosDescuentos;
    @Getter @Setter private float netoPorPagar;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nomina")
    @JsonIgnore
    @Getter @Setter private NominaModel nomina;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato")
    @JsonIgnore
    @Getter @Setter private ContratoModel contrato;






}
