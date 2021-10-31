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
				this.empleadoRepository.save(empleadoEncontrado);
				this.contratoRepository.save(nuevoContrato);
			}
		} else {
			throw new ContratoExistsException(CONTRATO_ALREADY_EXISTS);
		}
		return nuevoContrato;
	}

	private void evaluarIncidenciasLaborales(ContratoModel nuevoContrato) throws ContratoNotValidException {
		EmpleadoModel empleado = this.empleadoRepository.findByDni(nuevoContrato.getEmpleado().getDni());
		List<ContratoModel> listaContratos = this.contratoRepository.findAllByEmpleado(empleado);
		IncidenciaLaboralModel nuevaIncidenciaLaboral = new IncidenciaLaboralModel();
		PeriodoNominaModel periodoNominaVigente = obtenerPeriodoNominaVigente();
		if (listaContratos.size() > 1) {
			ContratoModel contratoMasReciente = listaContratos.get(listaContratos.size() - 1);
			IncidenciaLaboralModel incidenciaLaboralMasReciente = obtenerIncidenciaLaboralMasReciente(
					contratoMasReciente);
			if (incidenciaLaboralMasReciente != null) {
				PeriodoNominaModel periodoNominaEncontrado = incidenciaLaboralMasReciente.getPeriodoNomina();
				if (periodoNominaVigente != null && (periodoNominaEncontrado == periodoNominaVigente)) {
					insertarHorasDeFaltaPasadas(nuevaIncidenciaLaboral, incidenciaLaboralMasReciente,
							periodoNominaVigente, contratoMasReciente);
					nuevaIncidenciaLaboral.setTotalHorasExtras(incidenciaLaboralMasReciente.getTotalHorasExtras());
					nuevoContrato.addIncidenciaLaboral(nuevaIncidenciaLaboral);
					nuevaIncidenciaLaboral.setContrato(nuevoContrato);
					nuevaIncidenciaLaboral.setPeriodoNomina(periodoNominaVigente);
					this.incidenciaLaboralRepository.save(nuevaIncidenciaLaboral);
					reemplazarIncidenciaLaboralDePeriodo(periodoNominaVigente, incidenciaLaboralMasReciente,
							nuevaIncidenciaLaboral);
				}
			} else {
				evaluarPosiblesHorasDeFalta(nuevaIncidenciaLaboral, periodoNominaVigente, nuevoContrato);
			}
		} else {
			evaluarPosiblesHorasDeFalta(nuevaIncidenciaLaboral, periodoNominaVigente, nuevoContrato);
		}
	}

	private void evaluarPosiblesHorasDeFalta(IncidenciaLaboralModel nuevaIncidenciaLaboral,
			PeriodoNominaModel periodoNominaVigente, ContratoModel nuevoContrato) {
		if (periodoNominaVigente != null) {
			insertarPosiblesHorasDeFalta(nuevaIncidenciaLaboral, periodoNominaVigente, nuevoContrato);
			nuevaIncidenciaLaboral.setTotalHorasExtras(0);
		}
	}

	private void insertarPosiblesHorasDeFalta(IncidenciaLaboralModel nuevaIncidenciaLaboral,
			PeriodoNominaModel periodoNominaVigente, ContratoModel nuevoContrato) {
		if (nuevoContrato.getFechaInicio().after(periodoNominaVigente.getFechaInicio())
				&& nuevoContrato.getFechaInicio().before(periodoNominaVigente.getFechaFin())) {
			int diasPasadosDeNomina = Period
					.between(
							LocalDate.ofInstant(periodoNominaVigente.getFechaInicio().toInstant(),
									ZoneId.systemDefault()),
							LocalDate.ofInstant(nuevoContrato.getFechaInicio().toInstant(), ZoneId.systemDefault()))
					.getDays();
			int horasContratadasPorSemana = Integer.parseInt(nuevoContrato.getHorasPorSemana());
			int horasDeFalta = (diasPasadosDeNomina / 7) * horasContratadasPorSemana;
			nuevaIncidenciaLaboral.setTotalHorasDeFalta(horasDeFalta);
			nuevaIncidenciaLaboral.setContrato(nuevoContrato);
			nuevaIncidenciaLaboral.setPeriodoNomina(periodoNominaVigente);
			this.incidenciaLaboralRepository.save(nuevaIncidenciaLaboral);
		}
	}

	private void insertarHorasDeFaltaPasadas(IncidenciaLaboralModel nuevaIncidenciaLaboral,
			IncidenciaLaboralModel incidenciaLaboralMasReciente, PeriodoNominaModel periodoNomina,
			ContratoModel contratoMasReciente) {
		int totalHorasDeFaltaPasadas = incidenciaLaboralMasReciente.getTotalHorasDeFalta();
		int totalHorasDeFaltaPasadasPorContratoCaduco = obtenerTotalHorasDeFaltaPasadasPorContratoCaduco(periodoNomina,
				contratoMasReciente);
		int totalHorasDeFaltaPorIncidenciaLaboral = totalHorasDeFaltaPasadas
				- totalHorasDeFaltaPasadasPorContratoCaduco;
		if (totalHorasDeFaltaPorIncidenciaLaboral >= 0) {
			nuevaIncidenciaLaboral.setTotalHorasDeFalta(totalHorasDeFaltaPorIncidenciaLaboral);
		}
	}

	private int obtenerTotalHorasDeFaltaPasadasPorContratoCaduco(PeriodoNominaModel periodoNomina,
			ContratoModel contratoMasReciente) {
		Date fechaInicio = periodoNomina.getFechaInicio();
		Date fechaFin = periodoNomina.getFechaFin();
		int duracionPeriodoNomina = Period.between(LocalDate.ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault()),
				LocalDate.ofInstant(fechaFin.toInstant(), ZoneId.systemDefault())).getDays();
		int duracionLaburoDurantePeriodo = Period
				.between(LocalDate.ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault()),
						LocalDate.ofInstant(contratoMasReciente.getFechaFin().toInstant(), ZoneId.systemDefault()))
				.getDays();
		int cantidadDiasFaltantes = duracionPeriodoNomina - duracionLaburoDurantePeriodo;
		int totalHorasDeFalta = (cantidadDiasFaltantes / 7) * Integer.parseInt(contratoMasReciente.getHorasPorSemana());
		return totalHorasDeFalta;
	}

	private void reemplazarIncidenciaLaboralDePeriodo(PeriodoNominaModel periodoNominaVigente,
			IncidenciaLaboralModel incidenciaLaboralMasReciente, IncidenciaLaboralModel nuevaIncidenciaLaboral) {
		List<IncidenciaLaboralModel> listaIncidencias = periodoNominaVigente.getIncidenciasLaborales();
		for (IncidenciaLaboralModel incidenciaLaboral : listaIncidencias) {
			if (incidenciaLaboral == incidenciaLaboralMasReciente) {
				incidenciaLaboral.setContrato(nuevaIncidenciaLaboral.getContrato());
				incidenciaLaboral.setIdIncidenciaLaboral(nuevaIncidenciaLaboral.getIdIncidenciaLaboral());
				incidenciaLaboral.setTotalHorasDeFalta(nuevaIncidenciaLaboral.getTotalHorasDeFalta());
				incidenciaLaboral.setTotalHorasExtras(nuevaIncidenciaLaboral.getTotalHorasExtras());
				this.incidenciaLaboralRepository.save(incidenciaLaboral);
			}
		}
		periodoNominaVigente.setIncidenciasLaborales(listaIncidencias);
		this.periodoNominaRepository.save(periodoNominaVigente);
	}

	public PeriodoNominaModel obtenerPeriodoNominaVigente() {
		List<PeriodoNominaModel> periodosNomina = this.periodoNominaRepository.findAll();
		PeriodoNominaModel periodoNominaVigente = null;
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		for (PeriodoNominaModel periodoNomina : periodosNomina) {
			if (periodoNomina.getFechaInicio().before(tiempoActual)
					&& periodoNomina.getFechaFin().after(tiempoActual)) {
				periodoNominaVigente = periodoNomina;
			}
		}
		return periodoNominaVigente;
	}

	private IncidenciaLaboralModel obtenerIncidenciaLaboralMasReciente(ContratoModel contratoMasReciente) {
		List<IncidenciaLaboralModel> listaIncidencias = this.incidenciaLaboralRepository
				.findAllByContrato(contratoMasReciente);
		IncidenciaLaboralModel incidenciaLaboralMasReciente = null;
		if (!listaIncidencias.isEmpty()) {
			incidenciaLaboralMasReciente = listaIncidencias.get(listaIncidencias.size() - 1);
		}
		return incidenciaLaboralMasReciente;
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

	private ContratoModel obtenerContratoVigente(EmpleadoModel empleado) {
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
				this.contratoRepository.save(contratoAEditar);
			} else
				throw new ContratoNotValidException(CONTRATO_CERRADO);
		} else {
			throw new ContratoNotFoundException(CONTRATO_NOT_FOUND);
		}
		return contratoAEditar;
	}

	@Override
	public ContratoModel guardarContrato(Date fechaInicio, Date fechaFin, String idEmpleado, String puesto,
			String horasPorSemana, String idAfp, Boolean tieneAsignacionFamiliar, String pagoPorHora) throws NumberFormatException, ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException {
		ContratoModel nuevoContrato = new ContratoModel();
		AfpModel afpEncontrado = new AfpModel();
		EmpleadoModel empleadoEncontrado = new EmpleadoModel();
		ContratoDto contratoDto = new ContratoDto();
		contratoDto.setFechaInicio(fechaInicio);
		if (nuevoContrato.afpValido(idAfp))
			afpEncontrado = afpRepository.findByIdAfp(Long.parseLong(idAfp));
		if (afpEncontrado == null)
			throw new AfpNotFoundException(NO_AFP_FOUND);
		if (nuevoContrato.empleadoValido(idEmpleado))
			empleadoEncontrado = this.empleadoRepository.findByIdEmpleado(Long.parseLong(idEmpleado));
		if (empleadoEncontrado == null)
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND);
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
				this.empleadoRepository.save(empleadoEncontrado);
				this.contratoRepository.save(nuevoContrato);
			}
		} else {
			throw new ContratoExistsException(CONTRATO_ALREADY_EXISTS);
		}
		return nuevoContrato;
	}

}
