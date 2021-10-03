package nomina.soft.backend.models;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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
    

    public float calcularTotalIngresos(ContratoModel contrato, NominaModel nomina, BoletaDePagoModel boletaDePago) {
		int totalDeHoras = calcularTotalDeHoras(nomina.getPeriodoNomina().getFechaInicio(),
                                                nomina.getPeriodoNomina().getFechaFin(),
                                                Integer.parseInt(contrato.getHorasPorSemana()));
		int sueldoBasicoTMP = calcularSueldoBasico(totalDeHoras, Integer.parseInt(contrato.getPagoPorHora()));
		float montoPorAsignacionFamiliarTMP = calcularMontoPorAsignacionFamiliar(contrato.getTieneAsignacionFamiliar(),
                                                                                sueldoBasicoTMP);
		int montoPorHorasExtrasTMP = obtenerMontoPorHorasExtras(contrato,nomina.getPeriodoNomina());
		float reintegrosTMP = 0f;
		float movilidadTMP = 0f;
		float otrosIngresosTMP = 0f;
		boletaDePago.setSueldoBasico(sueldoBasico);
		boletaDePago.setAsignacionFamiliar(montoPorAsignacionFamiliarTMP);
		boletaDePago.setMontoPorHorasExtra(montoPorHorasExtrasTMP);
		boletaDePago.setReintegros(reintegrosTMP);
		boletaDePago.setMovilidad(movilidadTMP);
		boletaDePago.setOtrosIngresos(otrosIngresosTMP);
		return sueldoBasico + montoPorAsignacionFamiliarTMP + montoPorHorasExtrasTMP + reintegrosTMP + movilidadTMP + otrosIngresosTMP;
	}

    public float calcularTotalRetenciones(ContratoModel contrato, NominaModel nomina, BoletaDePagoModel boletaDePago) {
		int totalDeHoras = calcularTotalDeHoras(nomina.getPeriodoNomina().getFechaInicio(),
                                                nomina.getPeriodoNomina().getFechaFin(),
                                                Integer.parseInt(contrato.getHorasPorSemana()));
        int sueldoBasicoTMP = calcularSueldoBasico(totalDeHoras, Integer.parseInt(contrato.getPagoPorHora()));
		float regimenPensionarioTMP = calcularRegimenPensionario(sueldoBasicoTMP,contrato.getAfp().getPorcentajeDescuento());
		int montoPorHorasDeFaltaTMP = obtenerMontoPorHorasDeFalta(contrato,nomina.getPeriodoNomina());
		float adelantosTMP = 0f;
		float otrosDescuentosTMP = 0f;
		boletaDePago.setRegimenPensionario(regimenPensionario);
		boletaDePago.setMontoPorHorasDeFalta(montoPorHorasDeFalta);
		boletaDePago.setAdelantos(adelantos);
		boletaDePago.setOtrosDescuentos(otrosDescuentos);
		return 	regimenPensionarioTMP + montoPorHorasDeFaltaTMP + adelantosTMP + otrosDescuentosTMP;
	}

	

	public int calcularMontoPorHorasDeFalta(int totalHorasDeFaltaIncidenciaLaboral, int pagoPorHoraContrato) {
		return totalHorasDeFaltaIncidenciaLaboral * pagoPorHoraContrato;
	}

    private int obtenerMontoPorHorasDeFalta(ContratoModel contrato, PeriodoNominaModel periodoNomina){ //REGLA 08.2
		List<IncidenciaLaboralModel> incidenciasLaborales = contrato.getIncidenciasLaborales();
		IncidenciaLaboralModel incidenciaLaboralVigente = null;
		for(IncidenciaLaboralModel incidenciaLaboral: incidenciasLaborales){
			if(incidenciaLaboral.getPeriodoNomina() == periodoNomina){
				incidenciaLaboralVigente = incidenciaLaboral;
			}
		}
        int totalHorasDeFalta = incidenciaLaboralVigente.getTotalHorasDeFalta();
		int pagoPorHora = Integer.parseInt(contrato.getPagoPorHora());
		return calcularMontoPorHorasDeFalta(totalHorasDeFalta, pagoPorHora);
	}

    public float calcularRegimenPensionario(int sueldoBasico, int porcentajeAFP) {
		return sueldoBasico * porcentajeAFP * 0.01f;
	}


    public int calcularMontoPorHorasExtras(int totalHorasExtrasIncidenciaLaboral, int pagoPorHoraContrato) {
		return totalHorasExtrasIncidenciaLaboral * pagoPorHoraContrato;
	}

    private int obtenerMontoPorHorasExtras(ContratoModel contrato, PeriodoNominaModel periodoNomina){ //REGLA 08.2
		List<IncidenciaLaboralModel> incidenciasLaborales = contrato.getIncidenciasLaborales();
		IncidenciaLaboralModel incidenciaLaboralVigente = null;
		for(IncidenciaLaboralModel incidenciaLaboral: incidenciasLaborales){
			if(incidenciaLaboral.getPeriodoNomina() == periodoNomina){
				incidenciaLaboralVigente = incidenciaLaboral;
			}
		}
        int totalHorasExtras = incidenciaLaboralVigente.getTotalHorasExtras();
		int pagoPorHora = Integer.parseInt(contrato.getPagoPorHora());
		return calcularMontoPorHorasExtras(totalHorasExtras, pagoPorHora);
	}

    public float calcularMontoPorAsignacionFamiliar(Boolean tieneAsignacionFamiliarContrato, int sueldoBasico) { //REGLA 08.1
		float montoPorAsignacionFamiliar = 0f;
		if(tieneAsignacionFamiliarContrato){
			montoPorAsignacionFamiliar = sueldoBasico * 0.1f;
		}
		return montoPorAsignacionFamiliar;
	}

    public int calcularSueldoBasico(int totalDeHoras, int pagoPorHoraContrato) { //REGLA 07
		return totalDeHoras * pagoPorHoraContrato; 
	}


    public int calcularTotalDeHoras(Date fechaInicioPeriodoNomina, Date fechaFinPeriodoNomina, int horasPorSemanaContrato) { //REGLA 06.2
		int totalSemanasPeriodoNomina = Period.between(LocalDate.ofInstant(fechaInicioPeriodoNomina.toInstant(), ZoneId.systemDefault()),
                                        LocalDate.ofInstant(fechaFinPeriodoNomina.toInstant(), ZoneId.systemDefault()))
                                        .getDays()/7;
		return totalSemanasPeriodoNomina * horasPorSemanaContrato;
	}

    public float calcularNetoAPagar(float totalIngresos, float totalRetenciones) {
        return totalIngresos - totalRetenciones;
    }




}
