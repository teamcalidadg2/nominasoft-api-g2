package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.PeriodoNominaImplConstant.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.PeriodoNominaDto;
import nomina.soft.backend.exception.domain.PeriodoNominaExistsException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotFoundException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotValidException;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.models.IncidenciaLaboralModel;
import nomina.soft.backend.models.NominaModel;
import nomina.soft.backend.models.PeriodoNominaModel;
import nomina.soft.backend.repositories.ContratoRepository;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.repositories.IncidenciaLaboralRepository;
import nomina.soft.backend.repositories.PeriodoNominaRepository;
import nomina.soft.backend.services.PeriodoNominaService;

@Service
@Transactional
public class PeriodoNominaServiceImpl implements PeriodoNominaService {

	private PeriodoNominaRepository periodoNominaRepository;
	private EmpleadoRepository empleadoRepository;
	private ContratoRepository contratoRepository;
	private ContratoServiceImpl contratoService;
	private IncidenciaLaboralRepository incidenciaLaboralRepository;

	@Autowired
	public PeriodoNominaServiceImpl(PeriodoNominaRepository periodoNominaRepository,
			EmpleadoRepository empleadoRepository, ContratoRepository contratoRepository,
			ContratoServiceImpl contratoService, IncidenciaLaboralRepository incidenciaLaboralRepository) {
		super();
		this.periodoNominaRepository = periodoNominaRepository;
		this.empleadoRepository = empleadoRepository;
		this.contratoRepository = contratoRepository;
		this.contratoService = contratoService;
		this.incidenciaLaboralRepository = incidenciaLaboralRepository;
	}

	@Override
	public List<PeriodoNominaModel> getAll() throws PeriodoNominaNotFoundException {
		List<PeriodoNominaModel> lista = this.periodoNominaRepository.findAll();
		List<PeriodoNominaModel> listaFinal = new ArrayList<PeriodoNominaModel>();
		if (lista == null) {
			throw new PeriodoNominaNotFoundException(NO_PERIODOS_FOUND);
		} else {
			for (PeriodoNominaModel periodoNomina : lista) {
				List<NominaModel> listaNominas = periodoNomina.getNominas();
				boolean periodoDisponible = true;
				for (NominaModel nomina : listaNominas) {
					if (nomina.getEstaCerrada())
						periodoDisponible = false;
				}
				if (periodoDisponible)
					listaFinal.add(periodoNomina);
			}
		}
		return listaFinal;
	}

	@Override
	public PeriodoNominaModel guardarPeriodoNomina(PeriodoNominaDto periodoNominaDto)
			throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException {
		PeriodoNominaModel periodoNomina = new PeriodoNominaModel();
		validateNewFechaInicio(null, periodoNominaDto.getFechaInicio());
		validateNewFechaFin(null, periodoNominaDto.getFechaFin());
		if (periodoNomina.fechasValidas(periodoNominaDto.getFechaInicio(), periodoNominaDto.getFechaFin())
				&& validateOverlapingFechas(periodoNominaDto.getFechaInicio(), periodoNominaDto.getFechaFin())) {
			periodoNominaDto.corregirFechasZonaHoraria(periodoNominaDto.getFechaInicio(),
					periodoNominaDto.getFechaFin());
			periodoNomina.setIncidenciasLaborales(new ArrayList<IncidenciaLaboralModel>());
			periodoNomina.setDescripcion(periodoNominaDto.getDescripcion());
			periodoNomina.setFechaInicio(periodoNominaDto.getFechaInicio());
			periodoNomina.setFechaFin(periodoNominaDto.getFechaFin());
			generarCamposIncidenciaLaboral(periodoNomina);
			this.periodoNominaRepository.save(periodoNomina);
		}
		return periodoNomina;
	}

	private boolean validateOverlapingFechas(Date fechaInicio, Date fechaFin) throws PeriodoNominaExistsException {
		boolean rangoFechasValido = true;
		List<PeriodoNominaModel> listaPeriodosNomina = this.periodoNominaRepository.findAll();
		for (PeriodoNominaModel periodoNomina : listaPeriodosNomina) {
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
			throw new PeriodoNominaExistsException(RANGO_NOT_VALID);
		}
		return rangoFechasValido;
	}

	private void generarCamposIncidenciaLaboral(PeriodoNominaModel periodoNomina) {
		List<EmpleadoModel> listaEmpleados = this.empleadoRepository.findAll();
		for (EmpleadoModel empleado : listaEmpleados) {
			if (Boolean.TRUE.equals(empleado.getEstaActivo())) {
				List<ContratoModel> listaContratos = this.contratoRepository.findAllByEmpleado(empleado);
				if (!listaContratos.isEmpty()) {
					ContratoModel contratoMasReciente = listaContratos.get(listaContratos.size() - 1);
					if (validarContratoConPeriodoNomina(contratoMasReciente, periodoNomina)) {
						IncidenciaLaboralModel nuevaIncidenciaLaboral = new IncidenciaLaboralModel();
						nuevaIncidenciaLaboral.setTotalHorasDeFalta(0);
						nuevaIncidenciaLaboral.setTotalHorasExtras(0);
						nuevaIncidenciaLaboral.setPeriodoNomina(periodoNomina);
						if (contratoTeminaAntesQuePeriodoNomina(contratoMasReciente, nuevaIncidenciaLaboral))
							asignarHorasFaltantes(contratoMasReciente, nuevaIncidenciaLaboral);
						nuevaIncidenciaLaboral.setContrato(contratoMasReciente);
						this.incidenciaLaboralRepository.save(nuevaIncidenciaLaboral);
						contratoMasReciente.addIncidenciaLaboral(nuevaIncidenciaLaboral);
						this.contratoRepository.save(contratoMasReciente);
						periodoNomina.addIncidenciaLaboral(nuevaIncidenciaLaboral);
					}
				}
			}
		}
	}

	private boolean contratoTeminaAntesQuePeriodoNomina(ContratoModel contratoMasReciente,
			IncidenciaLaboralModel nuevaIncidenciaLaboral) {
		PeriodoNominaModel periodoNomina = nuevaIncidenciaLaboral.getPeriodoNomina();
		return contratoMasReciente.getFechaFin().before(periodoNomina.getFechaFin());
	}

	private boolean validarContratoConPeriodoNomina(ContratoModel contratoMasReciente, PeriodoNominaModel periodo) {
		if (Boolean.TRUE.equals(contratoMasReciente.getEstaCancelado())
				|| contratoMasReciente.getFechaFin().before(periodo.getFechaInicio()))
			return false;
		return true;
	}

	private void asignarHorasFaltantes(ContratoModel contratoMasReciente,
			IncidenciaLaboralModel nuevaIncidenciaLaboral) {
		PeriodoNominaModel periodoNomina = nuevaIncidenciaLaboral.getPeriodoNomina();
		int totalDiasPeriodoNomina = Period
				.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.of("America/Lima")),
						LocalDate.ofInstant(periodoNomina.getFechaFin().toInstant(), ZoneId.of("America/Lima")))
				.getDays();
		int semanasPeriodoNomina = totalDiasPeriodoNomina / 7;
		int totalDiasLaboralesPeriodoNomina = totalDiasPeriodoNomina - (2 * semanasPeriodoNomina);
		int totalDiasLaburoEmpleado = Period
				.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.of("America/Lima")),
						LocalDate.ofInstant(contratoMasReciente.getFechaFin().toInstant(), ZoneId.of("America/Lima")))
				.getDays();
		int semanasLaburo = totalDiasLaburoEmpleado / 7;
		int totalDiasLaburoEmpleadoReales = totalDiasLaburoEmpleado - (2 * semanasLaburo);

		int cantidadDiasFaltantes = totalDiasLaboralesPeriodoNomina - totalDiasLaburoEmpleadoReales;
		int cantidadHorasPorDia = Integer.parseInt(contratoMasReciente.getHorasPorSemana()) / 5;
		int totalHorasDeFalta = cantidadHorasPorDia * cantidadDiasFaltantes;
		nuevaIncidenciaLaboral.addTotalHorasDeFalta(totalHorasDeFalta);
	}

	@Override
	public PeriodoNominaModel buscarPeriodoNominaPorId(Long idPeriodoNomina) throws PeriodoNominaNotFoundException {
		PeriodoNominaModel periodoNomina = this.periodoNominaRepository.findByIdPeriodoNomina(idPeriodoNomina);
		if (periodoNomina == null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID);
		}
		return periodoNomina;
	}

	@Override
	public void deletePeriodoNomina(Long idPeriodoNomina) throws PeriodoNominaNotFoundException {
		PeriodoNominaModel periodoNomina = this.periodoNominaRepository.findByIdPeriodoNomina(idPeriodoNomina);
		if (periodoNomina == null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID);
		}
		this.periodoNominaRepository.delete(periodoNomina);
	}

	private PeriodoNominaModel validateNewFechaInicio(Date actualFechaInicio, Date nuevaFechaInicio)
			throws PeriodoNominaNotFoundException, PeriodoNominaExistsException {
		PeriodoNominaModel periodoNominaConNuevaFechaInicio = this.periodoNominaRepository
				.findByFechaInicio(nuevaFechaInicio);
		if (actualFechaInicio != null) {
			PeriodoNominaModel actualPeriodoNomina = this.periodoNominaRepository.findByFechaInicio(actualFechaInicio);
			if (actualPeriodoNomina == null) {
				throw new PeriodoNominaNotFoundException(
						NO_NOMINA_FOUND_BY_FECHA_INICIO + actualFechaInicio.toString());
			}
			if (periodoNominaConNuevaFechaInicio != null && (actualPeriodoNomina
					.getIdPeriodoNomina() != periodoNominaConNuevaFechaInicio.getIdPeriodoNomina())) {
				throw new PeriodoNominaExistsException(FECHA_INICIO_ALREADY_EXISTS);
			}
			return actualPeriodoNomina;
		} else {
			if (periodoNominaConNuevaFechaInicio != null) {
				throw new PeriodoNominaExistsException(FECHA_INICIO_ALREADY_EXISTS);
			}
			return null;
		}
	}

	private PeriodoNominaModel validateNewFechaFin(Date actualFechaFin, Date nuevaFechaFin)
			throws PeriodoNominaNotFoundException, PeriodoNominaExistsException {
		PeriodoNominaModel periodoNominaConNuevaFechaFin = this.periodoNominaRepository.findByFechaFin(nuevaFechaFin);
		if (actualFechaFin != null) {
			PeriodoNominaModel actualPeriodoNomina = this.periodoNominaRepository.findByFechaInicio(actualFechaFin);
			if (actualPeriodoNomina == null) {
				throw new PeriodoNominaNotFoundException(NO_NOMINA_FOUND_BY_FECHA_FIN + actualFechaFin.toString());
			}
			if (periodoNominaConNuevaFechaFin != null && (actualPeriodoNomina
					.getIdPeriodoNomina() != periodoNominaConNuevaFechaFin.getIdPeriodoNomina())) {
				throw new PeriodoNominaExistsException(FECHA_FIN_ALREADY_EXISTS);
			}
			return actualPeriodoNomina;
		} else {
			if (periodoNominaConNuevaFechaFin != null) {
				throw new PeriodoNominaExistsException(FECHA_FIN_ALREADY_EXISTS);
			}
			return null;
		}
	}

}
