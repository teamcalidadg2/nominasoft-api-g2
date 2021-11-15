package nomina.soft.backend.Servicios.Implementacion;

import static nomina.soft.backend.Constantes.PeriodoNominaImplConstant.PERIODOS_NO_ENCONTRADOS;
import static nomina.soft.backend.Constantes.PeriodoNominaImplConstant.PERIODO_NOMINA_NO_ENCONTRADO_POR_ID;
import static nomina.soft.backend.Constantes.PeriodoNominaImplConstant.RANGO_DE_FECHAS_NO_VALIDO;

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

import nomina.soft.backend.Data_Access_Object.DAO_Contrato;
import nomina.soft.backend.Data_Access_Object.DAO_Empleado;
import nomina.soft.backend.Data_Access_Object.DAO_IncidenciaLaboral;
import nomina.soft.backend.Data_Access_Object.DAO_PeriodoNomina;
import nomina.soft.backend.Data_Transfer_Object.DTO_PeriodoNomina;
import nomina.soft.backend.Entidades.Contrato;
import nomina.soft.backend.Entidades.Empleado;
import nomina.soft.backend.Entidades.IncidenciaLaboral;
import nomina.soft.backend.Entidades.PeriodoNomina;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaExistsException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotValidException;
import nomina.soft.backend.Servicios.Declaracion.Servicio_PeriodoNomina;

@Service
@Transactional
public class Impl_Servicio_PeriodoNomina implements Servicio_PeriodoNomina {

	private DAO_PeriodoNomina repositorioPeriodoNomina;
	private DAO_Empleado repositorioEmpleado;
	private DAO_Contrato repositorioContrato;
	private DAO_IncidenciaLaboral repositorioIncidenciaLaboral;
	public static final String TIMEZONE = "America/Lima";

	@Autowired
	public Impl_Servicio_PeriodoNomina(DAO_PeriodoNomina repositorioPeriodoNomina, DAO_Empleado repositorioEmpleado,
			DAO_Contrato repositorioContrato, DAO_IncidenciaLaboral repositorioIncidenciaLaboral) {
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
	public PeriodoNomina guardarNuevoPeriodoNomina(DTO_PeriodoNomina periodoNominaDto)
			throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException {
		PeriodoNomina nuevoPeriodoNomina = new PeriodoNomina();
		if (validateOverlapingFechas(periodoNominaDto.getFechaInicio(), periodoNominaDto.getFechaFin())) {
			nuevoPeriodoNomina.setFechaInicio(arreglarZonaHorariaFechaInicio(periodoNominaDto.getFechaInicio()));
			nuevoPeriodoNomina.setFechaFin(arreglarZonaHorariaFechaFin(periodoNominaDto.getFechaFin()));
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
		}
		return null;
	}

	public Date arreglarZonaHorariaFechaInicio(Date fechaInicio) {
		fechaInicio.setMinutes(fechaInicio.getMinutes() + fechaInicio.getTimezoneOffset());
		return fechaInicio;
	}

	public Date arreglarZonaHorariaFechaFin(Date fechaFin) {
		fechaFin.setMinutes(fechaFin.getMinutes() + fechaFin.getTimezoneOffset());
		return fechaFin;
	}

	private boolean validateOverlapingFechas(Date fechaInicio, Date fechaFin) throws PeriodoNominaExistsException {
		boolean rangoFechasValido = true;
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
					rangoFechasValido = false;
			}
		}

		if (!rangoFechasValido) {
			throw new PeriodoNominaExistsException(RANGO_DE_FECHAS_NO_VALIDO);
		}
		return rangoFechasValido;
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
				.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.of(TIMEZONE)),
						LocalDate.ofInstant(periodoNomina.getFechaFin().toInstant(), ZoneId.of(TIMEZONE)))
				.getDays();
		int semanasPeriodoNomina = totalDiasPeriodoNomina / 7;
		int totalDiasLaboralesPeriodoNomina = totalDiasPeriodoNomina - (2 * semanasPeriodoNomina);
		int totalDiasLaburoEmpleado = Period
				.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.of(TIMEZONE)),
						LocalDate.ofInstant(contratoMasReciente.getFechaFin().toInstant(), ZoneId.of(TIMEZONE)))
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
