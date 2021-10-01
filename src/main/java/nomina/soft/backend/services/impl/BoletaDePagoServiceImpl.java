package nomina.soft.backend.services.impl;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.AfpModel;
import nomina.soft.backend.models.BoletaDePagoModel;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.models.IncidenciaLaboralModel;
import nomina.soft.backend.models.NominaModel;
import nomina.soft.backend.models.PeriodoNominaModel;
import nomina.soft.backend.repositories.BoletaDePagoRepository;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.repositories.IncidenciaLaboralRepository;
import nomina.soft.backend.services.BoletaDePagoService;


@Service
@Transactional
public class BoletaDePagoServiceImpl implements BoletaDePagoService{

	private BoletaDePagoRepository boletaDePagoRepository;
	private ContratoServiceImpl contratoService;
	private IncidenciaLaboralRepository incidenciaLaboralRepository;
	private EmpleadoRepository empleadoRepository;

	@Autowired
	public BoletaDePagoServiceImpl(BoletaDePagoRepository boletaDePagoRepository,
									ContratoServiceImpl contratoService,
									IncidenciaLaboralRepository incidenciaLaboralRepository,
									EmpleadoRepository empleadoRepository) {
		super();
		this.boletaDePagoRepository = boletaDePagoRepository;
		this.contratoService = contratoService;
		this.incidenciaLaboralRepository = incidenciaLaboralRepository;
		this.empleadoRepository = empleadoRepository;
	}

	public BoletaDePagoModel GenerarBoletaDePago(ContratoModel contrato, NominaModel nomina) throws ContratoNotFoundException, EmpleadoNotFoundException{
		BoletaDePagoModel boletaDePago = new BoletaDePagoModel();
		float totalIngresos = calcularTotalIngresos(contrato,nomina,boletaDePago);
		float totalRetenciones = calcularTotalRetenciones(contrato,nomina,boletaDePago);		
		float netoAPagar = totalIngresos - totalRetenciones;
		boletaDePago.setNetoAPagar(netoAPagar);
		boletaDePago.setContrato(contrato);
		boletaDePago.setNomina(nomina);
		return boletaDePago;
	}



	private float calcularTotalRetenciones(ContratoModel contrato, NominaModel nomina, BoletaDePagoModel boletaDePago) {
		int totalDeHoras = calcularTotalDeHoras(nomina,contrato);
		int sueldoBasico = totalDeHoras * Integer.parseInt(contrato.getPagoPorHora());
		float regimenPensionario = calcularRegimenPensionario(sueldoBasico,contrato);
		int montoPorHorasDeFalta = calcularMontoPorHorasDeFalta(contrato,nomina);
		float adelantos = 0f;
		float otrosDescuentos = 0f;
		boletaDePago.setRegimenPensionario(regimenPensionario);
		boletaDePago.setMontoPorHorasDeFalta(montoPorHorasDeFalta);
		boletaDePago.setAdelantos(adelantos);
		boletaDePago.setOtrosDescuentos(otrosDescuentos);
		return 	regimenPensionario + montoPorHorasDeFalta + adelantos + otrosDescuentos;
	}

	private float calcularTotalIngresos(ContratoModel contrato, NominaModel nomina, BoletaDePagoModel boletaDePago) {
		int totalDeHoras = calcularTotalDeHoras(nomina,contrato);
		int sueldoBasico = totalDeHoras * Integer.parseInt(contrato.getPagoPorHora());
		float montoPorAsignacionFamiliar = calcularMontoPorAsignacionFamiliar(contrato, sueldoBasico);
		int montoPorHorasExtras = calcularMontoPorHorasExtras(contrato,nomina);
		float reintegros = 0f;
		float movilidad = 0f;
		float otrosIngresos = 0f;
		boletaDePago.setSueldoBasico(sueldoBasico);
		boletaDePago.setAsignacionFamiliar(montoPorAsignacionFamiliar);
		boletaDePago.setMontoPorHorasExtra(montoPorHorasExtras);
		boletaDePago.setReintegros(reintegros);
		boletaDePago.setMovilidad(movilidad);
		boletaDePago.setOtrosIngresos(otrosIngresos);
		return sueldoBasico + montoPorAsignacionFamiliar + montoPorHorasExtras + reintegros + movilidad + otrosIngresos;
	}

	private int calcularMontoPorHorasDeFalta(ContratoModel contrato, NominaModel nomina) {
		IncidenciaLaboralModel incidenciaLaboralEncontrada = buscarIncidenciaLaboralDelPeriodo(contrato, nomina);
		int totalHorasDeFalta = incidenciaLaboralEncontrada.getTotalHorasDeFalta();
		int pagoPorHora = Integer.parseInt(contrato.getPagoPorHora());
		return totalHorasDeFalta * pagoPorHora;
	}

	private float calcularRegimenPensionario(int sueldoBasico, ContratoModel contrato) {
		AfpModel afpDeContrato = contrato.getAfp();
		float porcentajeAFP = afpDeContrato.getPorcentajeDescuento();
		return sueldoBasico * porcentajeAFP;
	}

	private int calcularMontoPorHorasExtras(ContratoModel contrato, NominaModel nomina) {
		IncidenciaLaboralModel incidenciaLaboralEncontrada = buscarIncidenciaLaboralDelPeriodo(contrato, nomina);
		int totalHorasExtras = incidenciaLaboralEncontrada.getTotalHorasExtras();
		int pagoPorHora = Integer.parseInt(contrato.getPagoPorHora());
		return totalHorasExtras * pagoPorHora;
	}

	private IncidenciaLaboralModel buscarIncidenciaLaboralDelPeriodo(ContratoModel contrato, NominaModel nomina){
		PeriodoNominaModel periodoNomina = nomina.getPeriodoNomina();
		List<IncidenciaLaboralModel> incidenciasLaborales = this.incidenciaLaboralRepository.findAllByContrato(contrato);
		IncidenciaLaboralModel incidenciaLaboralCorrecta = null;
		for(IncidenciaLaboralModel incidenciaLaboral: incidenciasLaborales){
			if(incidenciaLaboral.getPeriodoNomina() == periodoNomina){
				incidenciaLaboralCorrecta = incidenciaLaboral;
			}
		}
		return incidenciaLaboralCorrecta;
	}

	private float calcularMontoPorAsignacionFamiliar(ContratoModel contrato, int sueldoMinimo) {
		float montoPorAsignacionFamiliar = 0f;
		if(contrato.getTieneAsignacionFamiliar()){
			montoPorAsignacionFamiliar = sueldoMinimo * 0.1f;
		}
		return montoPorAsignacionFamiliar;
	}

	private int calcularTotalDeHoras(NominaModel nomina, ContratoModel contrato) {
		int totalSemanasPeriodoNomina = calcularTotalSemanasPeriodoNomina(nomina.getPeriodoNomina());
		int totalHorasContratadasPorSemana = Integer.parseInt(contrato.getHorasPorSemana());

		return totalSemanasPeriodoNomina * totalHorasContratadasPorSemana;
	}

	private int calcularTotalSemanasPeriodoNomina(PeriodoNominaModel periodoNomina){
		return Period.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.systemDefault()),
							LocalDate.ofInstant(periodoNomina.getFechaFin().toInstant(), ZoneId.systemDefault()))
							.getDays()/7;
	}
	
	
	
}
