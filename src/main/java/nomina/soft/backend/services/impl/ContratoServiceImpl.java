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
	public ContratoServiceImpl(ContratoRepository contratoRepository,
			EmpleadoRepository	empleadoRepository,
			AfpRepository afpRepository,
			IncidenciaLaboralRepository incidenciaLaboralRepository,
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
		if(empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + dniEmpleado);
		}
				
		return this.contratoRepository.findAllByEmpleado(empleado);
	}

	@Override
	public ContratoModel guardarContrato(ContratoDto contratoDto) throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException {
		ContratoModel nuevoContrato = null;
		AfpModel afpEncontrado = afpRepository.findByIdAfp(contratoDto.getIdAfp());
		if(afpEncontrado==null) {
			throw new AfpNotFoundException(NO_AFP_FOUND);
		}
		EmpleadoModel empleadoEncontrado = this.empleadoRepository.findByIdEmpleado(contratoDto.getIdEmpleado());
		if(empleadoEncontrado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND);
		}
		if(obtenerContratoVigente(empleadoEncontrado)==null) {
			if(validarContrato(contratoDto)) {
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
		}else {
			throw new ContratoExistsException(CONTRATO_ALREADY_EXISTS);
		}
		return nuevoContrato;
	}
	
	private void evaluarIncidenciasLaborales(ContratoModel nuevoContrato) throws ContratoNotValidException {
		EmpleadoModel empleado = this.empleadoRepository.findByDni(nuevoContrato.getEmpleado().getDni());
		List<ContratoModel> listaContratos = this.contratoRepository.findAllByEmpleado(empleado);
		IncidenciaLaboralModel nuevaIncidenciaLaboral =  new IncidenciaLaboralModel();
		PeriodoNominaModel periodoNominaVigente = obtenerPeriodoNominaVigente();
		if(listaContratos.size()>1){
			ContratoModel contratoMasReciente = listaContratos.get(listaContratos.size() - 1);
			IncidenciaLaboralModel incidenciaLaboralMasReciente = obtenerIncidenciaLaboralMasReciente(contratoMasReciente);
			if(incidenciaLaboralMasReciente!=null){
				PeriodoNominaModel periodoNominaEncontrado = incidenciaLaboralMasReciente.getPeriodoNomina();
				if(periodoNominaVigente!=null && (periodoNominaEncontrado == periodoNominaVigente)){
					insertarHorasDeFaltaPasadas(nuevaIncidenciaLaboral, incidenciaLaboralMasReciente, periodoNominaVigente, contratoMasReciente);
					nuevaIncidenciaLaboral.setTotalHorasExtras(incidenciaLaboralMasReciente.getTotalHorasExtras());
					nuevoContrato.addIncidenciaLaboral(nuevaIncidenciaLaboral);
					nuevaIncidenciaLaboral.setContrato(nuevoContrato);
					nuevaIncidenciaLaboral.setPeriodoNomina(periodoNominaVigente);
					this.incidenciaLaboralRepository.save(nuevaIncidenciaLaboral);
					reemplazarIncidenciaLaboralDePeriodo(periodoNominaVigente, incidenciaLaboralMasReciente, nuevaIncidenciaLaboral);
				}
			}
			else{
				evaluarPosiblesHorasDeFalta(nuevaIncidenciaLaboral,periodoNominaVigente,nuevoContrato);
			}
		}else{
			evaluarPosiblesHorasDeFalta(nuevaIncidenciaLaboral,periodoNominaVigente,nuevoContrato);
		}
	}
	private void evaluarPosiblesHorasDeFalta(IncidenciaLaboralModel nuevaIncidenciaLaboral,
			PeriodoNominaModel periodoNominaVigente, ContratoModel nuevoContrato) {
		if(periodoNominaVigente!=null){
			insertarPosiblesHorasDeFalta(nuevaIncidenciaLaboral, periodoNominaVigente,nuevoContrato);
			nuevaIncidenciaLaboral.setTotalHorasExtras(0);
		}
	}

	private void insertarPosiblesHorasDeFalta(IncidenciaLaboralModel nuevaIncidenciaLaboral,
			PeriodoNominaModel periodoNominaVigente, ContratoModel nuevoContrato) {
		if(nuevoContrato.getFechaInicio().after(periodoNominaVigente.getFechaInicio()) &&
			nuevoContrato.getFechaInicio().before(periodoNominaVigente.getFechaFin())){
			int diasPasadosDeNomina = Period.between(LocalDate.ofInstant(periodoNominaVigente.getFechaInicio().toInstant(), ZoneId.systemDefault()),
													LocalDate.ofInstant(nuevoContrato.getFechaInicio().toInstant(), ZoneId.systemDefault()))
													.getDays();
			int horasContratadasPorSemana = Integer.parseInt(nuevoContrato.getHorasPorSemana());
			int horasDeFalta = (diasPasadosDeNomina/7) * horasContratadasPorSemana;
			nuevaIncidenciaLaboral.setTotalHorasDeFalta(horasDeFalta);
			nuevaIncidenciaLaboral.setContrato(nuevoContrato);
			nuevaIncidenciaLaboral.setPeriodoNomina(periodoNominaVigente);
			this.incidenciaLaboralRepository.save(nuevaIncidenciaLaboral);
		}
	}

	private void insertarHorasDeFaltaPasadas(IncidenciaLaboralModel nuevaIncidenciaLaboral, 
												IncidenciaLaboralModel incidenciaLaboralMasReciente,
												PeriodoNominaModel periodoNomina, 
												ContratoModel contratoMasReciente) {
		int totalHorasDeFaltaPasadas = incidenciaLaboralMasReciente.getTotalHorasDeFalta();
		int totalHorasDeFaltaPasadasPorContratoCaduco = obtenerTotalHorasDeFaltaPasadasPorContratoCaduco(periodoNomina, contratoMasReciente);
		int totalHorasDeFaltaPorIncidenciaLaboral = totalHorasDeFaltaPasadas - totalHorasDeFaltaPasadasPorContratoCaduco;
		if(totalHorasDeFaltaPorIncidenciaLaboral>=0){
			nuevaIncidenciaLaboral.setTotalHorasDeFalta(totalHorasDeFaltaPorIncidenciaLaboral);
		}
	}

	private int obtenerTotalHorasDeFaltaPasadasPorContratoCaduco(PeriodoNominaModel periodoNomina, ContratoModel contratoMasReciente) {
		Date fechaInicio = periodoNomina.getFechaInicio();
		Date fechaFin = periodoNomina.getFechaFin();
		int duracionPeriodoNomina = Period.between(LocalDate.ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault()),
													LocalDate.ofInstant(fechaFin.toInstant(), ZoneId.systemDefault()))
													.getDays();
		int duracionLaburoDurantePeriodo = Period.between(LocalDate.ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault()),
															LocalDate.ofInstant(contratoMasReciente.getFechaFin().toInstant(), ZoneId.systemDefault()))
															.getDays();
		int cantidadDiasFaltantes = duracionPeriodoNomina - duracionLaburoDurantePeriodo;
		int totalHorasDeFalta = (cantidadDiasFaltantes / 7) * Integer.parseInt(contratoMasReciente.getHorasPorSemana());
		return totalHorasDeFalta;
	}

	private void reemplazarIncidenciaLaboralDePeriodo(PeriodoNominaModel periodoNominaVigente,
			IncidenciaLaboralModel incidenciaLaboralMasReciente, IncidenciaLaboralModel nuevaIncidenciaLaboral) {
		List<IncidenciaLaboralModel> listaIncidencias = periodoNominaVigente.getIncidenciasLaborales();
		for(IncidenciaLaboralModel incidenciaLaboral: listaIncidencias ){
			if(incidenciaLaboral == incidenciaLaboralMasReciente){
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

	private PeriodoNominaModel obtenerPeriodoNominaVigente() {
		List<PeriodoNominaModel> periodosNomina = this.periodoNominaRepository.findAll();
		PeriodoNominaModel periodoNominaVigente = null;
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		for(PeriodoNominaModel periodoNomina:periodosNomina){
			if(periodoNomina.getFechaInicio().before(tiempoActual) && periodoNomina.getFechaFin().after(tiempoActual)){
				periodoNominaVigente = periodoNomina;
			}
		}
		return periodoNominaVigente;
	}

	private IncidenciaLaboralModel obtenerIncidenciaLaboralMasReciente(ContratoModel contratoMasReciente) {
		List<IncidenciaLaboralModel> listaIncidencias = this.incidenciaLaboralRepository.findAllByContrato(contratoMasReciente);
		IncidenciaLaboralModel incidenciaLaboralMasReciente = null;
		if(!listaIncidencias.isEmpty()){
			incidenciaLaboralMasReciente  = listaIncidencias.get(listaIncidencias.size() - 1);
		}
		return incidenciaLaboralMasReciente;
	}


	private boolean validarContrato(ContratoDto contratoDto) throws ContratoNotValidException {
		boolean contratoValido = true;
		if(!(ValidarFechas(contratoDto.getFechaInicio(), contratoDto.getFechaFin()) &&
				ValidarHorasContratadas(contratoDto.getHorasPorSemana()) &&	
				ValidarPagoPorHora(contratoDto.getPagoPorHora()))) {
			contratoValido = false;
		}
		return contratoValido;
	}
	
	private boolean ValidarFechas(Date fechaInicio, Date fechaFin) throws ContratoNotValidException {
		boolean fechasValidas = true;
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		if(!(fechaInicio.equals(tiempoActual) ||	//REGLA02
				fechaInicio.after(tiempoActual))) {
			fechasValidas = false;
			throw new ContratoNotValidException(FECHA_INICIO_NOT_VALID);
		}
		
		if((fechaFin.after(fechaInicio))) {			//REGLA03
			int mesesDeDiferencia = Period.between(LocalDate.ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault()),
													LocalDate.ofInstant(fechaFin.toInstant(), ZoneId.systemDefault()))
													.getMonths();
			if(!(mesesDeDiferencia>=3 && mesesDeDiferencia<=12)) {
				fechasValidas = false;
				throw new ContratoNotValidException(FECHA_FIN_NOT_VALID);
			}
		}else {
			fechasValidas = false;
			throw new ContratoNotValidException(FECHAS_NOT_VALID);
		}
		return fechasValidas;
	}
	
	private boolean ValidarHorasContratadas(String horasContratadasCad) throws ContratoNotValidException {	//REGLA 04
		int horasContratadas = 0;
		boolean horasContratadasValidas = true;
		try {
			horasContratadas = Integer.parseInt(horasContratadasCad);
		} catch (NumberFormatException nfe){
			horasContratadasValidas = false;
			throw new ContratoNotValidException(HORAS_CONTRATADAS_NOT_INTEGER);
		}
		if(horasContratadas>=8 && horasContratadas<=40) {
			if(!(horasContratadas%4==0)) {
				horasContratadasValidas = false;
				throw new ContratoNotValidException(HORAS_CONTRATADAS_NOT_VALID);
			}
		}else {
			horasContratadasValidas = false;
			throw new ContratoNotValidException(HORAS_CONTRATADAS_RANGO_NOT_VALID);
		}
		return horasContratadasValidas;
	}
	
	private boolean ValidarPagoPorHora(String pagoPorHoraCad) throws ContratoNotValidException { //REGLA05
		int pagoPorHora = 0;
		boolean pagoPorHoraValido = true;
		try {
			pagoPorHora = Integer.parseInt(pagoPorHoraCad);
		} catch (NumberFormatException nfe){
			pagoPorHoraValido = false;
			throw new ContratoNotValidException(PAGO_POR_HORA_NOT_INTEGER);
		}
		if(!(pagoPorHora>=10 && pagoPorHora<=60)) {
			pagoPorHoraValido = false;
			throw new ContratoNotValidException(PAGO_POR_HORA_RANGO_NOT_VALID);
		}
		return pagoPorHoraValido;
	}
	
	
	@Override
	public ContratoModel buscarContratoPorDni(String dniEmpleado) throws ContratoNotFoundException, EmpleadoNotFoundException {
		EmpleadoModel empleado = this.empleadoRepository.findByDni(dniEmpleado);
		if(empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + dniEmpleado);
		}
		ContratoModel contratoVigente = obtenerContratoVigente(empleado);
		if(contratoVigente==null) {
			throw new ContratoNotFoundException(NO_CONTRATO_FOUND_BY_EMPLEADO + empleado.getNombres() + " " + empleado.getApellidos());
		}
		return contratoVigente;
	}
	
	private ContratoModel obtenerContratoVigente(EmpleadoModel empleado) {
		List<ContratoModel> contratosDeEmpleado = this.contratoRepository.findAllByEmpleado(empleado);
		ContratoModel contratoVigente = null;
		for (ContratoModel contrato: contratosDeEmpleado) {
		      if(validarVigencia(contrato)) {
		    	  contratoVigente = contrato;
		      }		      
		}
		return contratoVigente;
	}
	
	public boolean validarVigencia(ContratoModel contratoModel) {				//REGLA01
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		boolean vigenciaValida = true;
		if(!((contratoModel.getFechaFin().after(tiempoActual) || 
				contratoModel.getFechaFin().equals(tiempoActual)) &&
				!contratoModel.getEstaCancelado())) {
			vigenciaValida = false;
		}
		return vigenciaValida;
	}

	@Override
	public ContratoModel updateContrato(Long idContrato, String puesto, String horasPorSemana, Long idAfp,
			Boolean tieneAsignacionFamiliar, String pagoPorHora) throws ContratoNotValidException, AfpNotFoundException, ContratoNotFoundException {
		ContratoModel contratoAEditar = this.contratoRepository.getById(idContrato);
		AfpModel afpEncontrado = this.afpRepository.findByIdAfp(idAfp);
		if(contratoAEditar!=null){
			if(afpEncontrado!=null){
				if(ValidarHorasContratadas(contratoAEditar.getHorasPorSemana()) &&	
				ValidarPagoPorHora(contratoAEditar.getPagoPorHora())){
					contratoAEditar.setPuesto(puesto);
					contratoAEditar.setHorasPorSemana(horasPorSemana);
					contratoAEditar.setAfp(afpEncontrado);
					contratoAEditar.setTieneAsignacionFamiliar(tieneAsignacionFamiliar);
					contratoAEditar.setPagoPorHora(pagoPorHora);
					this.contratoRepository.save(contratoAEditar);
				}
			}else{
				throw new AfpNotFoundException(NO_AFP_FOUND);
			}

		}else{
			throw new ContratoNotFoundException(CONTRATO_NOT_FOUND + idContrato.toString());
		}

		return contratoAEditar;
	}

	@Override
	public ContratoModel cancelarContrato(Long idContrato) throws ContratoNotFoundException {
		ContratoModel contratoAEditar = this.contratoRepository.getById(idContrato);
		if(contratoAEditar!=null){
				contratoAEditar.setEstaCancelado(true);
				this.contratoRepository.save(contratoAEditar);
		}else{
			throw new ContratoNotFoundException(CONTRATO_NOT_FOUND + idContrato.toString());
		}

		return contratoAEditar;
	}
	
	
	
	
}
