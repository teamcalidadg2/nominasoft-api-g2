package nomina.soft.backend.servicios.implementacion;

import static nomina.soft.backend.servicios.Utility.TIME_ZONE;
import static nomina.soft.backend.statics.PeriodoNominaImplConstant.PERIODOS_NO_ENCONTRADOS;
import static nomina.soft.backend.statics.PeriodoNominaImplConstant.PERIODO_NOMINA_NO_ENCONTRADO_POR_ID;
import static nomina.soft.backend.statics.PeriodoNominaImplConstant.RANGO_DE_FECHAS_NO_VALIDO;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dao.ContratoDao;
import nomina.soft.backend.dao.EmpleadoDao;
import nomina.soft.backend.dao.IncidenciaLaboralDao;
import nomina.soft.backend.dao.PeriodoNominaDao;
import nomina.soft.backend.dto.PeriodoNominaDto;
import nomina.soft.backend.entidades.Contrato;
import nomina.soft.backend.entidades.Empleado;
import nomina.soft.backend.entidades.IncidenciaLaboral;
import nomina.soft.backend.entidades.PeriodoNomina;
import nomina.soft.backend.excepciones.clases.PeriodoNominaExistsException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotValidException;
import nomina.soft.backend.servicios.declaracion.ServicioPeriodoNomina;

@Service
@Transactional
public class ImplServicioPeriodoNomina implements ServicioPeriodoNomina {

	private PeriodoNominaDao repositorioPeriodoNomina;
	private EmpleadoDao repositorioEmpleado;
	private ContratoDao repositorioContrato;
	private IncidenciaLaboralDao repositorioIncidenciaLaboral;

	@Autowired
	public ImplServicioPeriodoNomina(PeriodoNominaDao repositorioPeriodoNomina, EmpleadoDao repositorioEmpleado,
			ContratoDao repositorioContrato, IncidenciaLaboralDao repositorioIncidenciaLaboral) {
		super();
		this.repositorioPeriodoNomina = repositorioPeriodoNomina;
		this.repositorioEmpleado = repositorioEmpleado;
		this.repositorioContrato = repositorioContrato;
		this.repositorioIncidenciaLaboral = repositorioIncidenciaLaboral;
	}

	@Override
	public List<PeriodoNomina> obtenerTodosLosPeriodos() throws PeriodoNominaNotFoundException {
		List<PeriodoNomina> listaPeriodos = this.repositorioPeriodoNomina.findAll();
		if (listaPeriodos.isEmpty())
			throw new PeriodoNominaNotFoundException(PERIODOS_NO_ENCONTRADOS);
		return listaPeriodos;
	}

	@Override
	public PeriodoNomina guardarNuevoPeriodoNomina(PeriodoNominaDto periodoNominaDto)
			throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException,
			ParseException {
		PeriodoNomina nuevoPeriodoNomina = new PeriodoNomina();
		boolean existeOverlapingConOtroPeriodo = validarOverlaping(periodoNominaDto.getFechaInicio(),
				periodoNominaDto.getFechaFin());
		if (!existeOverlapingConOtroPeriodo) {
			nuevoPeriodoNomina
					.setFechaInicio(periodoNominaDto.getFechaInicio());
			nuevoPeriodoNomina.setFechaFin(periodoNominaDto.getFechaFin());
			if (nuevoPeriodoNomina.fechasValidas(nuevoPeriodoNomina.getFechaInicio(),
					nuevoPeriodoNomina.getFechaFin())) {
				nuevoPeriodoNomina.setIncidenciasLaborales(new ArrayList<>());
				nuevoPeriodoNomina.setDescripcion(periodoNominaDto.getDescripcion());
				nuevoPeriodoNomina.setFechaInicio(periodoNominaDto.getFechaInicio());
				nuevoPeriodoNomina.setFechaFin(periodoNominaDto.getFechaFin());
				generarCamposIncidenciaLaboral(nuevoPeriodoNomina);
				this.repositorioPeriodoNomina.save(nuevoPeriodoNomina);
				return nuevoPeriodoNomina;
			}
		} else
			throw new PeriodoNominaExistsException(RANGO_DE_FECHAS_NO_VALIDO);
		return null;
	}

	private boolean validarOverlaping(Date fechaInicio, Date fechaFin) {
		List<PeriodoNomina> listaPeriodosNomina = this.repositorioPeriodoNomina.findAll();
		for (PeriodoNomina periodoNomina : listaPeriodosNomina) {
			if (periodoNomina.getFechaInicio().after(fechaInicio)
					|| periodoNomina.getFechaInicio().equals(fechaInicio)) {
				DateTime fechaInicioExistente = new DateTime(periodoNomina.getFechaInicio());
				DateTime fechaFinExistente = new DateTime(periodoNomina.getFechaFin());
				Interval rangoExistente = new Interval(fechaInicioExistente, fechaFinExistente);

				DateTime fechaInicioNuevo = new DateTime(fechaInicio);
				DateTime fechaFinNuevo = new DateTime(fechaFin);
				Interval rangoNuevo = new Interval(fechaInicioNuevo, fechaFinNuevo);

				if (rangoExistente.overlaps(rangoNuevo))
					return true;
			}
		}
		return false;
	}

	private void generarCamposIncidenciaLaboral(PeriodoNomina periodoNomina) {
		List<Empleado> listaEmpleados = this.repositorioEmpleado.findAll();
		for (Empleado empleado : listaEmpleados) {
			if (Boolean.TRUE.equals(empleado.getEstaActivo())) {
				List<Contrato> listaContratos = this.repositorioContrato.findAllByEmpleado(empleado);
				if (!listaContratos.isEmpty()) {
					Contrato contratoMasReciente = listaContratos.get(listaContratos.size() - 1);
					if (validarContratoConPeriodoNomina(contratoMasReciente, periodoNomina)) {
						IncidenciaLaboral nuevaIncidenciaLaboral = new IncidenciaLaboral();
						nuevaIncidenciaLaboral.setTotalHorasDeFalta(0);
						nuevaIncidenciaLaboral.setTotalHorasExtras(0);
						nuevaIncidenciaLaboral.setPeriodoNomina(periodoNomina);
						boolean contratoTerminaAntesQuePeriodo = verificarSiContratoTeminaAntesQuePeriodoNomina(
								contratoMasReciente, nuevaIncidenciaLaboral);
						if (contratoTerminaAntesQuePeriodo)
							asignarHorasFaltantes(contratoMasReciente, nuevaIncidenciaLaboral);
						nuevaIncidenciaLaboral.setContrato(contratoMasReciente);
						this.repositorioIncidenciaLaboral.save(nuevaIncidenciaLaboral);
						contratoMasReciente.addIncidenciaLaboral(nuevaIncidenciaLaboral);
						this.repositorioContrato.save(contratoMasReciente);
						periodoNomina.addIncidenciaLaboral(nuevaIncidenciaLaboral);
					}
				}
			}
		}
	}

	private boolean verificarSiContratoTeminaAntesQuePeriodoNomina(Contrato contratoMasReciente,
			IncidenciaLaboral nuevaIncidenciaLaboral) {
		PeriodoNomina periodoNomina = nuevaIncidenciaLaboral.getPeriodoNomina();
		return contratoMasReciente.getFechaFin().before(periodoNomina.getFechaFin());
	}

	private boolean validarContratoConPeriodoNomina(Contrato contratoMasReciente, PeriodoNomina periodo) {
		return Boolean.FALSE.equals(contratoMasReciente.getEstaCancelado())
				&& contratoMasReciente.getFechaFin().after(periodo.getFechaInicio());
	}

	private void asignarHorasFaltantes(Contrato contratoMasReciente, IncidenciaLaboral nuevaIncidenciaLaboral) {
		PeriodoNomina periodoNomina = nuevaIncidenciaLaboral.getPeriodoNomina();
		int totalDiasPeriodoNomina = Period
				.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.of(TIME_ZONE)),
						LocalDate.ofInstant(periodoNomina.getFechaFin().toInstant(), ZoneId.of(TIME_ZONE)))
				.getDays();
		int semanasPeriodoNomina = totalDiasPeriodoNomina / 7;
		int totalDiasLaboralesPeriodoNomina = totalDiasPeriodoNomina - (2 * semanasPeriodoNomina);
		int totalDiasLaburoEmpleado = Period
				.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.of(TIME_ZONE)),
						LocalDate.ofInstant(contratoMasReciente.getFechaFin().toInstant(), ZoneId.of(TIME_ZONE)))
				.getDays();
		int semanasLaburo = totalDiasLaburoEmpleado / 7;
		int totalDiasLaburoEmpleadoReales = totalDiasLaburoEmpleado - (2 * semanasLaburo);

		int cantidadDiasFaltantes = totalDiasLaboralesPeriodoNomina - totalDiasLaburoEmpleadoReales;
		int cantidadHorasPorDia = Integer.parseInt(contratoMasReciente.getHorasPorSemana()) / 5;
		int totalHorasDeFalta = cantidadHorasPorDia * cantidadDiasFaltantes;
		nuevaIncidenciaLaboral.addTotalHorasDeFalta(totalHorasDeFalta);
	}

	@Override
	public PeriodoNomina buscarPeriodoNominaPorId(Long idPeriodoNomina) throws PeriodoNominaNotFoundException {
		PeriodoNomina periodoNomina = this.repositorioPeriodoNomina.findByIdPeriodoNomina(idPeriodoNomina);
		if (periodoNomina == null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOMINA_NO_ENCONTRADO_POR_ID);
		}
		return periodoNomina;
	}

	@Override
	public void eliminarPeriodoNomina(Long idPeriodoNomina) throws PeriodoNominaNotFoundException {
		PeriodoNomina periodoNomina = this.repositorioPeriodoNomina.findByIdPeriodoNomina(idPeriodoNomina);
		if (periodoNomina == null)
			throw new PeriodoNominaNotFoundException(PERIODO_NOMINA_NO_ENCONTRADO_POR_ID);
		else
			this.repositorioPeriodoNomina.delete(periodoNomina);
	}

}
