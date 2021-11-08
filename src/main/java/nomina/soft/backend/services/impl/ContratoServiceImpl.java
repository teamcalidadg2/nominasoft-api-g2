package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.AfpImplConstant.NO_AFP_FOUND;
import static nomina.soft.backend.constant.ContratoImplConstant.*;
import static nomina.soft.backend.constant.EmpleadoImplConstant.NO_EMPLEADO_FOUND;
import static nomina.soft.backend.constant.EmpleadoImplConstant.NO_EMPLEADO_FOUND_BY_DNI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.exception.domain.AfpNotFoundException;
import nomina.soft.backend.exception.domain.ContratoExistsException;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import nomina.soft.backend.models.AfpModel;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.models.IncidenciaLaboralModel;
import nomina.soft.backend.models.NominaModel;
import nomina.soft.backend.models.PeriodoNominaModel;
import nomina.soft.backend.repositories.AfpRepository;
import nomina.soft.backend.repositories.ContratoRepository;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.repositories.IncidenciaLaboralRepository;
import nomina.soft.backend.repositories.PeriodoNominaRepository;
import nomina.soft.backend.services.ContratoService;

@Service
@Transactional
public class ContratoServiceImpl implements ContratoService {

	private ContratoRepository contratoRepository;
	private EmpleadoRepository empleadoRepository;
	private AfpRepository afpRepository;
	private IncidenciaLaboralRepository incidenciaLaboralRepository;
	private PeriodoNominaRepository periodoNominaRepository;

	@Autowired
	public ContratoServiceImpl(ContratoRepository contratoRepository, EmpleadoRepository empleadoRepository,
			AfpRepository afpRepository, IncidenciaLaboralRepository incidenciaLaboralRepository,
			PeriodoNominaRepository periodoNominaRepository) {
		super();
		this.contratoRepository = contratoRepository;
		this.empleadoRepository = empleadoRepository;
		this.afpRepository = afpRepository;
		this.incidenciaLaboralRepository = incidenciaLaboralRepository;
		this.periodoNominaRepository = periodoNominaRepository;
	}

	@Override
	public List<ContratoModel> getAll(String dniEmpleado) throws EmpleadoNotFoundException {

		EmpleadoModel empleado = this.empleadoRepository.findByDni(dniEmpleado);
		if (empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + dniEmpleado);
		}

		return this.contratoRepository.findAllByEmpleado(empleado);
	}

	@Override
	public ContratoModel guardarContrato(ContratoDto contratoDto)
			throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException {
		ContratoModel nuevoContrato = new ContratoModel();
		AfpModel afpEncontrado = new AfpModel();
		EmpleadoModel empleadoEncontrado = new EmpleadoModel();
		if (nuevoContrato.afpValido(contratoDto.getIdAfp()))
			afpEncontrado = afpRepository.findByIdAfp(Long.parseLong(contratoDto.getIdAfp()));
		if (afpEncontrado == null)
			throw new AfpNotFoundException(NO_AFP_FOUND);
		if (nuevoContrato.empleadoValido(contratoDto.getIdEmpleado()))
			empleadoEncontrado = this.empleadoRepository.findByIdEmpleado(Long.parseLong(contratoDto.getIdEmpleado()));
		if (empleadoEncontrado == null)
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND);
		if (obtenerContratoVigente(empleadoEncontrado) == null) {
			if (validarContrato(contratoDto)) {
				nuevoContrato = new ContratoModel();
				nuevoContrato.setIncidenciasLaborales(new ArrayList<IncidenciaLaboralModel>());
				nuevoContrato.setFechaInicio(contratoDto.getFechaInicio());
				nuevoContrato.setFechaFin(contratoDto.getFechaFin());
				nuevoContrato.setEmpleado(empleadoEncontrado);
				this.contratoRepository.save(nuevoContrato);
				nuevoContrato.setAfp(afpEncontrado);
				nuevoContrato.setTieneAsignacionFamiliar(contratoDto.getTieneAsignacionFamiliar());
				nuevoContrato.setHorasPorSemana(contratoDto.getHorasPorSemana());
				nuevoContrato.setPagoPorHora(contratoDto.getPagoPorHora());
				nuevoContrato.setPuesto(contratoDto.getPuesto());
				evaluarIncidenciasLaborales(nuevoContrato);
				nuevoContrato.setEstaCancelado(false);
				empleadoEncontrado.addContrato(nuevoContrato);
				empleadoEncontrado.setEstaActivo(true);
				this.empleadoRepository.save(empleadoEncontrado);
				this.contratoRepository.save(nuevoContrato);
			}
		} else {
			throw new ContratoExistsException(CONTRATO_ALREADY_EXISTS);
		}
		return nuevoContrato;
	}

	private void evaluarIncidenciasLaborales(ContratoModel nuevoContrato) throws ContratoNotValidException {
		PeriodoNominaModel periodoNominaVigente = obtenerPeriodoNominaVigente();
		if (periodoNominaVigente != null) {
			IncidenciaLaboralModel incidenciaLaboralNueva = new IncidenciaLaboralModel();
			int horasDeFalta = evaluarHorasDeFalta(nuevoContrato, periodoNominaVigente);
			incidenciaLaboralNueva.setTotalHorasDeFalta(horasDeFalta);
			incidenciaLaboralNueva.setContrato(nuevoContrato);
			incidenciaLaboralNueva.setPeriodoNomina(periodoNominaVigente);
			incidenciaLaboralNueva.setTotalHorasExtras(0);
			this.incidenciaLaboralRepository.save(incidenciaLaboralNueva);
			periodoNominaVigente.addIncidenciaLaboral(incidenciaLaboralNueva);
			this.periodoNominaRepository.save(periodoNominaVigente);
		}
	}

	private int evaluarHorasDeFalta(ContratoModel nuevoContrato, PeriodoNominaModel periodoNominaVigente) {
		int totalDiasNoLaburoEmpleado = Period
				.between(
						LocalDate.ofInstant(periodoNominaVigente.getFechaInicio().toInstant(),
								ZoneId.of("America/Lima")),
						LocalDate.ofInstant(nuevoContrato.getFechaInicio().toInstant(), ZoneId.of("America/Lima")))
				.getDays();
		int semanasNoLaburo = totalDiasNoLaburoEmpleado / 7;
		int totalDiasNoLaburoEmpleadoReales = totalDiasNoLaburoEmpleado - (2 * semanasNoLaburo);

		int cantidadHorasPorDia = Integer.parseInt(nuevoContrato.getHorasPorSemana()) / 5;
		return cantidadHorasPorDia * totalDiasNoLaburoEmpleadoReales;
	}

	public PeriodoNominaModel obtenerPeriodoNominaVigente() {
		List<PeriodoNominaModel> periodosNomina = this.periodoNominaRepository.findAll();
		PeriodoNominaModel periodoNominaVigente = null;
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		for (PeriodoNominaModel periodoNomina : periodosNomina) {
			if (periodoNomina.getFechaInicio().before(tiempoActual) && (periodoNomina.getFechaFin().after(tiempoActual)
					|| periodoNomina.getFechaFin().equals(tiempoActual))) {
				periodoNominaVigente = periodoNomina;
			}
		}
		return periodoNominaVigente;
	}

	private boolean validarContrato(ContratoDto contratoDto) throws ContratoNotValidException {
		boolean contratoValido = true;
		ContratoModel contratoTemporal = new ContratoModel();
		contratoDto.arreglarZonaHorariaFechas();
		if (!(contratoTemporal.fechasValidas(contratoDto.getFechaInicio(), contratoDto.getFechaFin())
				&& contratoTemporal.horasContratadasValidas(contratoDto.getHorasPorSemana())
				&& contratoTemporal.pagoPorHoraValido(contratoDto.getPagoPorHora())
				&& contratoTemporal.puestoValido(contratoDto.getPuesto())
				&& contratoTemporal.asignacionFamiliarValido(contratoDto.getTieneAsignacionFamiliar()))) {
			contratoValido = false;
		}
		return contratoValido;
	}

	@Override
	public ContratoModel buscarContratoPorDni(String dniEmpleado)
			throws ContratoNotFoundException, EmpleadoNotFoundException, EmpleadoNotValidException {
		EmpleadoModel empleado = new EmpleadoModel();
		if (empleado.validarDni(dniEmpleado)) {
			empleado = this.empleadoRepository.findByDni(dniEmpleado);
		}
		if (empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI);
		}
		ContratoModel contratoVigente = obtenerContratoVigente(empleado);
		if (contratoVigente == null) {
			throw new ContratoNotFoundException(
					NO_CONTRATO_FOUND_BY_EMPLEADO + empleado.getNombres() + " " + empleado.getApellidos());
		}
		return contratoVigente;
	}

	@Override
	public ContratoModel buscarContratoPorId(String idContrato)
			throws NumberFormatException, ContratoNotValidException, ContratoNotFoundException {
		ContratoModel contrato = new ContratoModel();
		if (contrato.validarIdentificador(idContrato)) {
			contrato = this.contratoRepository.findByIdContrato(Long.parseLong(idContrato));
		}
		if (contrato == null) {
			throw new ContratoNotFoundException(CONTRATO_NOT_FOUND);
		}
		return contrato;
	}

	public ContratoModel obtenerContratoVigente(EmpleadoModel empleado) {
		List<ContratoModel> contratosDeEmpleado = this.contratoRepository.findAllByEmpleado(empleado);
		ContratoModel contratoVigente = null, contratoTemporal = new ContratoModel();
		for (ContratoModel contrato : contratosDeEmpleado) {
			if (contratoTemporal.vigenciaValida(contrato))
				contratoVigente = contrato;
		}
		return contratoVigente;
	}

	@Override
	public ContratoModel updateContrato(String idContrato, String puesto, String horasPorSemana, String idAfp,
			Boolean tieneAsignacionFamiliar, String pagoPorHora)
			throws ContratoNotValidException, AfpNotFoundException, ContratoNotFoundException {
		ContratoModel contratoAEditar = null, contratoTemporal = new ContratoModel();
		AfpModel afpEncontrado = new AfpModel();
		if (contratoTemporal.identificadorValido(idContrato))
			contratoAEditar = this.contratoRepository.findByIdContrato(Long.parseLong(idContrato));
		if (contratoAEditar == null)
			throw new ContratoNotFoundException(CONTRATO_NOT_FOUND);
		if (contratoTemporal.afpValido(idAfp))
			afpEncontrado = this.afpRepository.findByIdAfp(Long.parseLong(idAfp));
		if (afpEncontrado == null)
			throw new ContratoNotFoundException(NO_AFP_FOUND);
		if (contratoAEditar.vigenciaValida(contratoAEditar)) {
			if (contratoAEditar.puestoValido(puesto) && contratoAEditar.horasContratadasValidas(horasPorSemana)
					&& contratoAEditar.asignacionFamiliarValido(tieneAsignacionFamiliar)
					&& contratoAEditar.pagoPorHoraValido(pagoPorHora)) {
				contratoAEditar.setPuesto(puesto);
				contratoAEditar.setHorasPorSemana(horasPorSemana);
				contratoAEditar.setAfp(afpEncontrado);
				contratoAEditar.setTieneAsignacionFamiliar(tieneAsignacionFamiliar);
				contratoAEditar.setPagoPorHora(pagoPorHora);
				this.contratoRepository.save(contratoAEditar);
			}
		} else
			throw new ContratoNotValidException(CONTRATO_NOT_VIGENTE);
		return contratoAEditar;
	}

	@Override
	public ContratoModel cancelarContrato(String idContrato)
			throws ContratoNotFoundException, NumberFormatException, ContratoNotValidException {
		ContratoModel contratoAEditar = new ContratoModel();
		if (contratoAEditar.identificadorValido(idContrato))
			contratoAEditar = this.contratoRepository.findByIdContrato(Long.parseLong(idContrato));
		if (contratoAEditar != null) {
			if (contratoAEditar.vigenciaValida(contratoAEditar)) {
				contratoAEditar.setEstaCancelado(true);
				asignarPosiblesHorasDeFalta(contratoAEditar);
				this.contratoRepository.save(contratoAEditar);
				EmpleadoModel empleado = contratoAEditar.getEmpleado();
				empleado.setEstaActivo(true);
				this.empleadoRepository.save(empleado);
			} else
				throw new ContratoNotValidException(CONTRATO_CERRADO);
		} else {
			throw new ContratoNotFoundException(CONTRATO_NOT_FOUND);
		}
		return contratoAEditar;
	}

	private void asignarPosiblesHorasDeFalta(ContratoModel contratoAEditar) {
		PeriodoNominaModel periodoNominaVigente = obtenerPeriodoNominaVigente();
		if (periodoNominaVigente != null) {
			List<IncidenciaLaboralModel> incidenciasLaborales = periodoNominaVigente.getIncidenciasLaborales();
			IncidenciaLaboralModel incidenciaLaboralDePeriodo = null;
			for (IncidenciaLaboralModel incidenciaLaboral : incidenciasLaborales) {
				if (incidenciaLaboral.getPeriodoNomina() == periodoNominaVigente)
					incidenciaLaboralDePeriodo = incidenciaLaboral;
			}
			if (incidenciaLaboralDePeriodo != null) {
				int horasDeFalta = evaluarHorasDeFaltaPorCancelacion(contratoAEditar, periodoNominaVigente);
				incidenciaLaboralDePeriodo.setTotalHorasDeFalta(horasDeFalta);
				incidenciaLaboralDePeriodo.setContrato(contratoAEditar);
				incidenciaLaboralDePeriodo.setPeriodoNomina(periodoNominaVigente);
				incidenciaLaboralDePeriodo.setTotalHorasExtras(0);
				this.incidenciaLaboralRepository.save(incidenciaLaboralDePeriodo);
			}
		}
	}

	private int evaluarHorasDeFaltaPorCancelacion(ContratoModel contratoAEditar,
			PeriodoNominaModel periodoNominaVigente) {
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		int totalDiasPeriodoNomina = Period
				.between(
						LocalDate.ofInstant(periodoNominaVigente.getFechaInicio().toInstant(),
								ZoneId.of("America/Lima")),
						LocalDate.ofInstant(periodoNominaVigente.getFechaFin().toInstant(), ZoneId.of("America/Lima")))
				.getDays();
		int semanasPeriodoNomina = totalDiasPeriodoNomina / 7;
		int totalDiasLaboralesPeriodoNomina = totalDiasPeriodoNomina - (2 * semanasPeriodoNomina);

		int totalDiasLaburoEmpleado = Period
				.between(LocalDate.ofInstant(periodoNominaVigente.getFechaInicio().toInstant(), ZoneId.of("America/Lima")),
						LocalDate.ofInstant(tiempoActual.toInstant(), ZoneId.of("America/Lima")))
				.getDays();
		int semanasLaburo = totalDiasLaburoEmpleado / 7;
		int totalDiasLaburoEmpleadoReales = totalDiasLaburoEmpleado - (2 * semanasLaburo);

		int cantidadDiasFaltantes = totalDiasLaboralesPeriodoNomina - totalDiasLaburoEmpleadoReales;
		int cantidadHorasPorDia = Integer.parseInt(contratoAEditar.getHorasPorSemana()) / 5;
		return cantidadHorasPorDia * cantidadDiasFaltantes;
	}

	@Override
	public ContratoModel guardarContrato(Date fechaInicio, Date fechaFin, String idEmpleado, String puesto,
			String horasPorSemana, String idAfp, Boolean tieneAsignacionFamiliar, String pagoPorHora)
			throws NumberFormatException, ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException,
			ContratoExistsException {
		ContratoModel nuevoContrato = new ContratoModel();
		AfpModel afpEncontrado = new AfpModel();
		EmpleadoModel empleadoEncontrado = new EmpleadoModel();
		ContratoDto contratoDto = new ContratoDto();
		if (nuevoContrato.afpValido(idAfp))
			afpEncontrado = afpRepository.findByIdAfp(Long.parseLong(idAfp));
		if (afpEncontrado == null)
			throw new AfpNotFoundException(NO_AFP_FOUND);
		if (nuevoContrato.empleadoValido(idEmpleado))
			empleadoEncontrado = this.empleadoRepository.findByIdEmpleado(Long.parseLong(idEmpleado));
		if (empleadoEncontrado == null)
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND);
		if (nuevoContrato.validarFechas(fechaInicio, fechaFin)) {
			contratoDto.setFechaInicio(fechaInicio);
			contratoDto.setFechaFin(fechaFin);
			contratoDto.setIdEmpleado(idEmpleado);
			contratoDto.setPuesto(puesto);
			contratoDto.setHorasPorSemana(horasPorSemana);
			contratoDto.setIdAfp(idAfp);
			contratoDto.setTieneAsignacionFamiliar(tieneAsignacionFamiliar);
			contratoDto.setPagoPorHora(pagoPorHora);
			if (obtenerContratoVigente(empleadoEncontrado) == null) {
				if (validarContrato(contratoDto)) {
					nuevoContrato = new ContratoModel();
					nuevoContrato.setIncidenciasLaborales(new ArrayList<IncidenciaLaboralModel>());
					nuevoContrato.setFechaInicio(contratoDto.getFechaInicio());
					nuevoContrato.setFechaFin(contratoDto.getFechaFin());
					nuevoContrato.setEmpleado(empleadoEncontrado);
					this.contratoRepository.save(nuevoContrato);
					nuevoContrato.setAfp(afpEncontrado);
					nuevoContrato.setTieneAsignacionFamiliar(contratoDto.getTieneAsignacionFamiliar());
					nuevoContrato.setHorasPorSemana(contratoDto.getHorasPorSemana());
					nuevoContrato.setPagoPorHora(contratoDto.getPagoPorHora());
					nuevoContrato.setPuesto(contratoDto.getPuesto());
					evaluarIncidenciasLaborales(nuevoContrato);
					nuevoContrato.setEstaCancelado(false);
					empleadoEncontrado.addContrato(nuevoContrato);
					empleadoEncontrado.setEstaActivo(true);
					this.empleadoRepository.save(empleadoEncontrado);
					this.contratoRepository.save(nuevoContrato);
				}
			} else {
				throw new ContratoExistsException(CONTRATO_ALREADY_EXISTS);
			}
		}

		return nuevoContrato;
	}

}
