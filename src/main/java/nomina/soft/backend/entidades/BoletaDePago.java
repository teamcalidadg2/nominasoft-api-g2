package nomina.soft.backend.entidades;
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "boleta_de_pago")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoletaDePago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    @Getter @Setter private Long idBoleta;

    @Getter @Setter private float sueldoBasico;
    @Getter @Setter private float asignacionFamiliar;
    @Getter @Setter private float montoPorHorasExtra;
    @Getter @Setter private float reintegros;
    @Getter @Setter private float movilidad;
    @Getter @Setter private float otrosIngresos;
    @Getter @Setter private float totalIngresos;
    @Getter @Setter private float regimenPensionario;
    @Getter @Setter private float montoPorHorasDeFalta;
    @Getter @Setter private float adelantos;
    @Getter @Setter private float otrosDescuentos;
    @Getter @Setter private float totalRetenciones;
    @Getter @Setter private float sueldoNeto;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nomina")
    @JsonIgnore
    @Getter @Setter private Nomina nomina;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato")
    @Getter @Setter private Contrato contrato;
    

    public float calcularTotalIngresos(Contrato contrato, Nomina nomina, BoletaDePago boletaDePago) {
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
		boletaDePago.setSueldoBasico(sueldoBasicoTMP);
		boletaDePago.setAsignacionFamiliar(montoPorAsignacionFamiliarTMP);
		boletaDePago.setMontoPorHorasExtra(montoPorHorasExtrasTMP);
		boletaDePago.setReintegros(reintegrosTMP);
		boletaDePago.setMovilidad(movilidadTMP);
		boletaDePago.setOtrosIngresos(otrosIngresosTMP);
		return sumaTotalIngresos(sueldoBasicoTMP , montoPorAsignacionFamiliarTMP , montoPorHorasExtrasTMP , reintegrosTMP , movilidadTMP , otrosIngresosTMP);
	}

	public float sumaTotalIngresos(int sueldoBasico , float montoPorAsignacionFamiliar , int montoPorHorasExtras , float reintegros , float movilidad , float otrosIngresos){
		return sueldoBasico + montoPorAsignacionFamiliar + montoPorHorasExtras + reintegros + movilidad + otrosIngresos;
	}

    public float calcularTotalRetenciones(Contrato contrato, Nomina nomina, BoletaDePago boletaDePago) {
		int totalDeHoras = calcularTotalDeHoras(nomina.getPeriodoNomina().getFechaInicio(),
                                                nomina.getPeriodoNomina().getFechaFin(),
                                                Integer.parseInt(contrato.getHorasPorSemana()));
        int sueldoBasicoTMP = calcularSueldoBasico(totalDeHoras, Integer.parseInt(contrato.getPagoPorHora()));
		float regimenPensionarioTMP = calcularRegimenPensionario(sueldoBasicoTMP,contrato.getAfp().getPorcentajeDescuento());
		int montoPorHorasDeFaltaTMP = obtenerMontoPorHorasDeFalta(contrato,nomina.getPeriodoNomina());
		float adelantosTMP = 0f;
		float otrosDescuentosTMP = 0f;
		boletaDePago.setRegimenPensionario(regimenPensionarioTMP);
		boletaDePago.setMontoPorHorasDeFalta(montoPorHorasDeFaltaTMP);
		boletaDePago.setAdelantos(adelantosTMP);
		boletaDePago.setOtrosDescuentos(otrosDescuentosTMP);
		return 	sumaTotalDeRetenciones(regimenPensionarioTMP,montoPorHorasDeFaltaTMP,adelantosTMP,otrosDescuentosTMP);
	}

	public float sumaTotalDeRetenciones(float regimenPensionarioTMP,int montoPorHorasDeFaltaTMP,float adelantosTMP, float otrosDescuentosTMP){
		return regimenPensionarioTMP + montoPorHorasDeFaltaTMP + adelantosTMP + otrosDescuentosTMP;
	}
	

	public int calcularMontoPorHorasDeFalta(int totalHorasDeFaltaIncidenciaLaboral, int pagoPorHoraContrato) {
		return totalHorasDeFaltaIncidenciaLaboral * pagoPorHoraContrato;
	}

    private int obtenerMontoPorHorasDeFalta(Contrato contrato, PeriodoNomina periodoNomina){ //REGLA 08.2
		int totalHorasDeFalta = 0;
		int pagoPorHora = 0;
		List<IncidenciaLaboral> incidenciasLaborales = contrato.getIncidenciasLaborales();
		IncidenciaLaboral incidenciaLaboralVigente = null;
		for(IncidenciaLaboral incidenciaLaboral: incidenciasLaborales){
			if(incidenciaLaboral.getPeriodoNomina() == periodoNomina){
				incidenciaLaboralVigente = incidenciaLaboral;
			}
		}
		if(incidenciaLaboralVigente!=null){
			totalHorasDeFalta = incidenciaLaboralVigente.getTotalHorasDeFalta();
			pagoPorHora = Integer.parseInt(contrato.getPagoPorHora());
		}
		return calcularMontoPorHorasDeFalta(totalHorasDeFalta, pagoPorHora);
	}

    public float calcularRegimenPensionario(int sueldoBasico, float porcentajeAFP) {
		return sueldoBasico * porcentajeAFP * 0.01f;
	}


    public int calcularMontoPorHorasExtras(int totalHorasExtrasIncidenciaLaboral, int pagoPorHoraContrato) {
		return totalHorasExtrasIncidenciaLaboral * pagoPorHoraContrato;
	}

    private int obtenerMontoPorHorasExtras(Contrato contrato, PeriodoNomina periodoNomina){ //REGLA 08.2
		int totalHorasExtras = 0;
		int pagoPorHora = 0;
		List<IncidenciaLaboral> incidenciasLaborales = contrato.getIncidenciasLaborales();
		IncidenciaLaboral incidenciaLaboralVigente = null;
		for(IncidenciaLaboral incidenciaLaboral: incidenciasLaborales){
			if(incidenciaLaboral.getPeriodoNomina() == periodoNomina){
				incidenciaLaboralVigente = incidenciaLaboral;
			}
		}
		if(incidenciaLaboralVigente!=null){
			totalHorasExtras = incidenciaLaboralVigente.getTotalHorasExtras();
			pagoPorHora = Integer.parseInt(contrato.getPagoPorHora());
		}
		return calcularMontoPorHorasExtras(totalHorasExtras, pagoPorHora);
	}

    public float calcularMontoPorAsignacionFamiliar(Boolean tieneAsignacionFamiliarContrato, int sueldoBasico) { //REGLA 08.1
		float montoPorAsignacionFamiliar = 0f;
		if(Boolean.TRUE.equals(tieneAsignacionFamiliarContrato)){
			montoPorAsignacionFamiliar = sueldoBasico * 0.1f;
		}
		return montoPorAsignacionFamiliar;
	}

    public int calcularSueldoBasico(int totalDeHoras, int pagoPorHoraContrato) { //REGLA 07
		return totalDeHoras * pagoPorHoraContrato; 
	}


    public int calcularTotalDeHoras(Date fechaInicioPeriodoNomina, Date fechaFinPeriodoNomina, int horasPorSemanaContrato) { //REGLA 06.2
		int totalSemanasPeriodoNomina = Period.between(LocalDate.ofInstant(fechaInicioPeriodoNomina.toInstant(), ZoneId.of("America/Lima")),
                                        LocalDate.ofInstant(fechaFinPeriodoNomina.toInstant(), ZoneId.of("America/Lima")))
                                        .getDays()/7;
		return totalSemanasPeriodoNomina * horasPorSemanaContrato;
	}

    public float calcularNetoAPagar(float totalIngresos, float totalRetenciones) {
        return totalIngresos - totalRetenciones;
    }




}
