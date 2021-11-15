package nomina.soft.backend.servicios.implementacion;

import static nomina.soft.backend.constantes.AfpImplConstant.AFP_NO_ENCONTRADO_POR_ID;
import static nomina.soft.backend.constantes.ContratoImplConstant.CONTRATO_NO_ENCONTRADO_POR_ID;
import static nomina.soft.backend.constantes.ContratoImplConstant.CONTRATO_NO_VIGENTE;
import static nomina.soft.backend.constantes.ContratoImplConstant.CONTRATO_VIGENTE_NO_ENCONTRADO;
import static nomina.soft.backend.constantes.ContratoImplConstant.CONTRATO_VIGENTE_YA_EXISTE;
import static nomina.soft.backend.constantes.EmpleadoImplConstant.EMPLEADO_NO_ENCONTRADO_POR_DNI;
import static nomina.soft.backend.constantes.EmpleadoImplConstant.EMPLEADO_NO_ENCONTRADO_POR_ID;

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

import nomina.soft.backend.dao.AfpDao;
import nomina.soft.backend.dao.ContratoDao;
import nomina.soft.backend.dao.EmpleadoDao;
import nomina.soft.backend.dao.IncidenciaLaboralDao;
import nomina.soft.backend.dao.PeriodoNominaDao;
import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.excepciones.clases.AfpNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoExistsException;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.models.Afp;
import nomina.soft.backend.models.Contrato;
import nomina.soft.backend.models.Empleado;
import nomina.soft.backend.models.IncidenciaLaboral;
import nomina.soft.backend.models.PeriodoNomina;
import nomina.soft.backend.servicios.declaracion.ServicioContrato;

@Service
@Transactional
public class ImplServicioContrato implements ServicioContrato {

	private ContratoDao respositorioContrato;
	private EmpleadoDao repositorioEmpleado;
	private AfpDao repositorioAfp;
	private IncidenciaLaboralDao repositorioIncidenciaLaboral;
	private PeriodoNominaDao repositoPeriodoNomina;
	public static final String TIMEZONE = "America/Lima";

	@Autowired
	public ImplServicioContrato(ContratoDao respositorioContrato, EmpleadoDao repositorioEmpleado,
			AfpDao repositorioAfp, IncidenciaLaboralDao repositorioIncidenciaLaboral,
			PeriodoNominaDao repositoPeriodoNomina) {
		super();
		this.respositorioContrato = respositorioContrato;
		this.repositorioEmpleado = repositorioEmpleado;
		this.repositorioAfp = repositorioAfp;
		this.repositorioIncidenciaLaboral = repositorioIncidenciaLaboral;
		this.repositoPeriodoNomina = repositoPeriodoNomina;
	}

	@Override
	public List<Contrato> buscarListaDeContratosPorEmpleado(String dniEmpleado) throws EmpleadoNotFoundException {
		Empleado empleado = this.repositorioEmpleado.findByDni(dniEmpleado);
		if (empleado == null)
			throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_DNI);
		return this.respositorioContrato.findAllByEmpleado(empleado);
	}

	@Override
	public Contrato guardarNuevoContrato(ContratoDto dtoContrato)
			throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException,
			NumberFormatException, ContratoNotFoundException {
		Contrato nuevoContrato = new Contrato();
		Afp afpEncontrado = this.obtenerAfpValido(dtoContrato.getIdAfp());
		Empleado empleadoEncontrado = this.obtenerEmpleadoValido(dtoContrato.getIdEmpleado());
		boolean empleadoTieneContratoVigente = verificarSiEmpleadoTieneContratoVigente(empleadoEncontrado);
		if (afpEncontrado != null && empleadoEncontrado != null && !empleadoTieneContratoVigente) {
			dtoContrato.setFechaInicio(arreglarZonaHorariaFechaInicio(dtoContrato.getFechaInicio()));
			dtoContrato.setFechaFin(arreglarZonaHorariaFechaFin(dtoContrato.getFechaFin()));
			boolean esContratoValido = validarDtoContrato(dtoContrato);
			if (esContratoValido) {
				nuevoContrato.setIncidenciasLaborales(new ArrayList<>());
				nuevoContrato.setFechaInicio(dtoContrato.getFechaInicio());
				nuevoContrato.setFechaFin(dtoContrato.getFechaFin());
				nuevoContrato.setEmpleado(empleadoEncontrado);
				this.respositorioContrato.save(nuevoContrato);
				nuevoContrato.setAfp(afpEncontrado);
				nuevoContrato.setTieneAsignacionFamiliar(dtoContrato.getTieneAsignacionFamiliar());
				nuevoContrato.setHorasPorSemana(dtoContrato.getHorasPorSemana());
				nuevoContrato.setPagoPorHora(dtoContrato.getPagoPorHora());
				nuevoContrato.setPuesto(dtoContrato.getPuesto());
				evaluarIncidenciasLaboralesPorContratacionDentroDePeriodoNominaIniciado(nuevoContrato);
				nuevoContrato.setEstaCancelado(false);
				empleadoEncontrado.addContrato(nuevoContrato);
				empleadoEncontrado.setEstaActivo(true);
				this.repositorioEmpleado.save(empleadoEncontrado);
				this.respositorioContrato.save(nuevoContrato);
				return nuevoContrato;
			}
		} else
			throw new ContratoExistsException(CONTRATO_VIGENTE_YA_EXISTE);
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

	@Override
	public Contrato guardarNuevoContrato(Date fechaInicio, Date fechaFin, String idEmpleado, String puesto,
			String horasPorSemana, String idAfp, Boolean tieneAsignacionFamiliar, String pagoPorHora)
			throws NumberFormatException, ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException,
			ContratoExistsException, ContratoNotFoundException {
		Contrato nuevoContrato = new Contrato();
		Afp afpEncontrado = this.obtenerAfpValido(idAfp);
		Empleado empleadoEncontrado = this.obtenerEmpleadoValido(idEmpleado);
		boolean empleadoTieneContratoVigente = verificarSiEmpleadoTieneContratoVigente(empleadoEncontrado);
		if (afpEncontrado != null && empleadoEncontrado != null && !empleadoTieneContratoVigente) {
			nuevoContrato.setFechaInicio(arreglarZonaHorariaFechaInicio(fechaInicio));
			nuevoContrato.setFechaFin(arreglarZonaHorariaFechaFin(fechaFin));
			nuevoContrato.setTieneAsignacionFamiliar(tieneAsignacionFamiliar);
			nuevoContrato.setHorasPorSemana(horasPorSemana);
			nuevoContrato.setPagoPorHora(pagoPorHora);
			nuevoContrato.setPuesto(puesto);
			boolean esContratoValido = validarContrato(nuevoContrato);
			if (esContratoValido) {
				nuevoContrato.setAfp(afpEncontrado);
				nuevoContrato.setEmpleado(empleadoEncontrado);
				nuevoContrato.setIncidenciasLaborales(new ArrayList<>());
				this.respositorioContrato.save(nuevoContrato);
				evaluarIncidenciasLaboralesPorContratacionDentroDePeriodoNominaIniciado(nuevoContrato);
				nuevoContrato.setEstaCancelado(false);
				empleadoEncontrado.addContrato(nuevoContrato);
				empleadoEncontrado.setEstaActivo(true);
				this.repositorioEmpleado.save(empleadoEncontrado);
				this.respositorioContrato.save(nuevoContrato);
				return nuevoContrato;
			}
		} else
			throw new ContratoExistsException(CONTRATO_VIGENTE_YA_EXISTE);

		return null;
	}

	private void evaluarIncidenciasLaboralesPorContratacionDentroDePeriodoNominaIniciado(Contrato nuevoContrato) {
		PeriodoNomina periodoNominaActual = obtenerPeriodoNominaActual();
		if (periodoNominaActual != null) {
			IncidenciaLaboral incidenciaLaboralNueva = new IncidenciaLaboral();
			int horasDeFalta = obtenerHorasDeFaltaPorContratacionDentroDePeriodoNominaIniciado(nuevoContrato,
					periodoNominaActual);
			incidenciaLaboralNueva.setTotalHorasDeFalta(horasDeFalta);
			incidenciaLaboralNueva.setContrato(nuevoContrato);
			incidenciaLaboralNueva.setPeriodoNomina(periodoNominaActual);
			incidenciaLaboralNueva.setTotalHorasExtras(0);
			this.repositorioIncidenciaLaboral.save(incidenciaLaboralNueva);
			periodoNominaActual.addIncidenciaLaboral(incidenciaLaboralNueva);
			this.repositoPeriodoNomina.save(periodoNominaActual);
		}
	}

	private int obtenerHorasDeFaltaPorContratacionDentroDePeriodoNominaIniciado(Contrato nuevoContrato,
			PeriodoNomina periodoNominaActual) {
		int totalDiasNoLaburoEmpleado = Period
				.between(LocalDate.ofInstant(periodoNominaActual.getFechaInicio().toInstant(), ZoneId.of(TIMEZONE)),
						LocalDate.ofInstant(nuevoContrato.getFechaInicio().toInstant(), ZoneId.of(TIMEZONE)))
				.getDays();
		int semanasNoLaburo = totalDiasNoLaburoEmpleado / 7;
		int totalDiasNoLaburoEmpleadoReales = totalDiasNoLaburoEmpleado - (2 * semanasNoLaburo);

		int cantidadHorasPorDia = Integer.parseInt(nuevoContrato.getHorasPorSemana()) / 5;
		return cantidadHorasPorDia * totalDiasNoLaburoEmpleadoReales;
	}

	public PeriodoNomina obtenerPeriodoNominaActual() {
		List<PeriodoNomina> periodosNomina = this.repositoPeriodoNomina.findAll();
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		for (PeriodoNomina periodoNomina : periodosNomina) {
			if (periodoNomina.getFechaInicio().before(tiempoActual) && (periodoNomina.getFechaFin().after(tiempoActual)
					|| periodoNomina.getFechaFin().equals(tiempoActual))) {
				return periodoNomina;
			}
		}
		return null;
	}

	private boolean validarDtoContrato(ContratoDto dtoContrato) throws ContratoNotValidException {
		Contrato contratoTemporal = new Contrato();
		return contratoTemporal.fechasValidas(dtoContrato.getFechaInicio(), dtoContrato.getFechaFin())
				&& contratoTemporal.horasContratadasValidas(dtoContrato.getHorasPorSemana())
				&& contratoTemporal.pagoPorHoraValido(dtoContrato.getPagoPorHora())
				&& contratoTemporal.puestoValido(dtoContrato.getPuesto())
				&& contratoTemporal.asignacionFamiliarValido(dtoContrato.getTieneAsignacionFamiliar());
	}

	private boolean validarContrato(Contrato contrato) throws ContratoNotValidException {
		return contrato.fechasValidas(contrato.getFechaInicio(), contrato.getFechaFin())
				&& contrato.horasContratadasValidas(contrato.getHorasPorSemana())
				&& contrato.pagoPorHoraValido(contrato.getPagoPorHora()) && contrato.puestoValido(contrato.getPuesto())
				&& contrato.asignacionFamiliarValido(contrato.getTieneAsignacionFamiliar());
	}

	@Override
	public Contrato buscarContratoVigentePorDni(String dniEmpleado)
			throws ContratoNotFoundException, EmpleadoNotFoundException, EmpleadoNotValidException {
		Empleado empleadoEncontrado = new Empleado();
		boolean esDniValido = empleadoEncontrado.validarDni(dniEmpleado);
		if (esDniValido) {
			empleadoEncontrado = this.repositorioEmpleado.findByDni(dniEmpleado);
			if (empleadoEncontrado == null)
				throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_DNI);
			else {
				Contrato contratoVigente = obtenerContratoVigente(empleadoEncontrado);
				if (contratoVigente == null)
					throw new ContratoNotFoundException(CONTRATO_VIGENTE_NO_ENCONTRADO);
				else
					return contratoVigente;
			}
		}
		return null;
	}

	@Override
	public Contrato buscarContratoPorId(String idContrato)
			throws NumberFormatException, ContratoNotValidException, ContratoNotFoundException {
		Contrato contratoEncontrado = new Contrato();
		boolean esIdContratoValido = contratoEncontrado.validarIdentificador(idContrato);
		if (esIdContratoValido) {
			contratoEncontrado = this.respositorioContrato.findByIdContrato(Long.parseLong(idContrato));
			if (contratoEncontrado == null)
				throw new ContratoNotFoundException(CONTRATO_NO_ENCONTRADO_POR_ID);
			else
				return contratoEncontrado;
		}
		return null;
	}

	public boolean verificarSiEmpleadoTieneContratoVigente(Empleado empleado) {
		return obtenerContratoVigente(empleado) != null;
	}

	public Contrato obtenerContratoVigente(Empleado empleado) {
		List<Contrato> contratosDeEmpleado = this.respositorioContrato.findAllByEmpleado(empleado);
		for (Contrato contrato : contratosDeEmpleado) {
			if (contrato.vigenciaValida(contrato))
				return contrato;
		}
		return null;
	}

	@Override
	public Contrato actualizarContrato(String idContrato, String puesto, String horasPorSemana, String idAfp,
			Boolean tieneAsignacionFamiliar, String pagoPorHora)
			throws ContratoNotValidException, AfpNotFoundException, ContratoNotFoundException {
		Contrato contratoEncontrado = this.obtenerContratoValido(idContrato);
		Afp afpEncontrado = this.obtenerAfpValido(idAfp);
		if (contratoEncontrado != null && afpEncontrado != null) {
			boolean esContratoVigente = contratoEncontrado.vigenciaValida(contratoEncontrado);
			if (esContratoVigente) {
				if (contratoEncontrado.puestoValido(puesto)
						&& contratoEncontrado.horasContratadasValidas(horasPorSemana)
						&& contratoEncontrado.asignacionFamiliarValido(tieneAsignacionFamiliar)
						&& contratoEncontrado.pagoPorHoraValido(pagoPorHora)) {
					contratoEncontrado.setPuesto(puesto);
					contratoEncontrado.setHorasPorSemana(horasPorSemana);
					contratoEncontrado.setAfp(afpEncontrado);
					contratoEncontrado.setTieneAsignacionFamiliar(tieneAsignacionFamiliar);
					contratoEncontrado.setPagoPorHora(pagoPorHora);
					this.respositorioContrato.save(contratoEncontrado);
					return contratoEncontrado;
				}
			} else
				throw new ContratoNotValidException(CONTRATO_NO_VIGENTE);
		}
		return null;

	}

	private Contrato obtenerContratoValido(String idContrato)
			throws NumberFormatException, ContratoNotValidException, ContratoNotFoundException {
		Contrato contratoEncontrado = new Contrato();
		if (contratoEncontrado.validarIdentificador(idContrato)) {
			contratoEncontrado = this.respositorioContrato.findByIdContrato(Long.parseLong(idContrato));
			if (contratoEncontrado != null)
				return contratoEncontrado;
			else
				throw new ContratoNotFoundException(CONTRATO_NO_ENCONTRADO_POR_ID);
		}
		return null;
	}

	private Empleado obtenerEmpleadoValido(String idEmpleado)
			throws NumberFormatException, ContratoNotValidException, ContratoNotFoundException {
		Contrato contratoTemporal = new Contrato();
		if (contratoTemporal.empleadoValido(idEmpleado)) {
			Empleado empleadoEncontrado = this.repositorioEmpleado.findByIdEmpleado(Long.parseLong(idEmpleado));
			if (empleadoEncontrado != null)
				return empleadoEncontrado;
			else
				throw new ContratoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_ID);
		}
		return null;
	}

	private Afp obtenerAfpValido(String idAfp)
			throws NumberFormatException, ContratoNotValidException, ContratoNotFoundException {
		Contrato contratoTemporal = new Contrato();
		if (contratoTemporal.afpValido(idAfp)) {
			Afp afpEncontrado = this.repositorioAfp.findByIdAfp(Long.parseLong(idAfp));
			if (afpEncontrado != null)
				return afpEncontrado;
			else
				throw new ContratoNotFoundException(AFP_NO_ENCONTRADO_POR_ID);
		}
		return null;
	}

	@Override
	public void cancelarContrato(String idContrato)
			throws ContratoNotFoundException, NumberFormatException, ContratoNotValidException {
		Contrato contratoPorCancelar = this.obtenerContratoValido(idContrato);
		if (contratoPorCancelar != null) {
			if (contratoPorCancelar.vigenciaValida(contratoPorCancelar)) {
				contratoPorCancelar.setEstaCancelado(true);
				asignarHorasDeFaltaPorCancelacionDeContrato(contratoPorCancelar);
				this.respositorioContrato.save(contratoPorCancelar);
				Empleado empleado = contratoPorCancelar.getEmpleado();
				empleado.setEstaActivo(false);
				this.repositorioEmpleado.save(empleado);
			} else
				throw new ContratoNotValidException(CONTRATO_NO_VIGENTE);
		}
	}

	private void asignarHorasDeFaltaPorCancelacionDeContrato(Contrato contratoAEditar) {
		PeriodoNomina periodoNominaVigente = obtenerPeriodoNominaActual();
		if (periodoNominaVigente != null) {
			List<IncidenciaLaboral> incidenciasLaborales = periodoNominaVigente.getIncidenciasLaborales();
			IncidenciaLaboral incidenciaLaboralDePeriodo = null;
			for (IncidenciaLaboral incidenciaLaboral : incidenciasLaborales) {
				if (incidenciaLaboral.getPeriodoNomina() == periodoNominaVigente)
					incidenciaLaboralDePeriodo = incidenciaLaboral;
			}
			if (incidenciaLaboralDePeriodo != null) {
				int horasDeFalta = obtenerHorasDeFaltaPorCancelacionDeContrato(contratoAEditar, periodoNominaVigente);
				incidenciaLaboralDePeriodo.setTotalHorasDeFalta(horasDeFalta);
				incidenciaLaboralDePeriodo.setContrato(contratoAEditar);
				incidenciaLaboralDePeriodo.setPeriodoNomina(periodoNominaVigente);
				incidenciaLaboralDePeriodo.setTotalHorasExtras(0);
				this.repositorioIncidenciaLaboral.save(incidenciaLaboralDePeriodo);
			}
		}
	}

	private int obtenerHorasDeFaltaPorCancelacionDeContrato(Contrato contratoAEditar,
			PeriodoNomina periodoNominaVigente) {
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		int totalDiasPeriodoNomina = Period
				.between(LocalDate.ofInstant(periodoNominaVigente.getFechaInicio().toInstant(), ZoneId.of(TIMEZONE)),
						LocalDate.ofInstant(periodoNominaVigente.getFechaFin().toInstant(), ZoneId.of(TIMEZONE)))
				.getDays();
		int semanasPeriodoNomina = totalDiasPeriodoNomina / 7;
		int totalDiasLaboralesPeriodoNomina = totalDiasPeriodoNomina - (2 * semanasPeriodoNomina);

		int totalDiasLaburoEmpleado = Period
				.between(LocalDate.ofInstant(periodoNominaVigente.getFechaInicio().toInstant(), ZoneId.of(TIMEZONE)),
						LocalDate.ofInstant(tiempoActual.toInstant(), ZoneId.of(TIMEZONE)))
				.getDays();
		int semanasLaburo = totalDiasLaburoEmpleado / 7;
		int totalDiasLaburoEmpleadoReales = totalDiasLaburoEmpleado - (2 * semanasLaburo);

		int cantidadDiasFaltantes = totalDiasLaboralesPeriodoNomina - totalDiasLaburoEmpleadoReales;
		int cantidadHorasPorDia = Integer.parseInt(contratoAEditar.getHorasPorSemana()) / 5;
		return cantidadHorasPorDia * cantidadDiasFaltantes;
	}

}
