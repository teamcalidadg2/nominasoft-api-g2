package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.AfpImplConstant.NO_AFP_FOUND;
import static nomina.soft.backend.constant.ContratoImplConstant.*;
import static nomina.soft.backend.constant.EmpleadoImplConstant.NO_EMPLEADO_FOUND;
import static nomina.soft.backend.constant.EmpleadoImplConstant.NO_EMPLEADO_FOUND_BY_DNI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.exception.domain.AfpNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.AfpModel;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.repositories.AfpRepository;
import nomina.soft.backend.repositories.ContratoRepository;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.services.ContratoService;

@Service
@Transactional
public class ContratoServiceImpl implements ContratoService {

	private ContratoRepository contratoRepository;
	private EmpleadoRepository empleadoRepository;
	private AfpRepository afpRepository;

	private EmpleadoRepository empleadoRepository;
	private AfpRepository afpRepository;

	@Autowired
	public ContratoServiceImpl(ContratoRepository contratoRepository,
			EmpleadoRepository	empleadoRepository,
			AfpRepository afpRepository) {
		super();
		this.contratoRepository = contratoRepository;
		this.empleadoRepository = empleadoRepository;
		this.afpRepository = afpRepository;
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
	public ContratoModel guardarContrato(ContratoDto contratoDto) throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException {
		ContratoModel nuevoContrato = null;
		AfpModel afpEncontrado = afpRepository.findById(contratoDto.getAfp_id());
		if(afpEncontrado==null) {
			throw new AfpNotFoundException(NO_AFP_FOUND);
		}
		
		EmpleadoModel empleadoEncontrado = this.empleadoRepository.findById(contratoDto.getEmpleado_id());
		if(empleadoEncontrado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND);
		}
		if(obtenerContratoVigente(empleadoEncontrado)==null) {
			if(validarContrato(contratoDto)) {
				nuevoContrato = new ContratoModel();
				nuevoContrato.setEmpleado(empleadoEncontrado);
				nuevoContrato.setAfp(afpEncontrado);
				nuevoContrato.setFechaInicio(contratoDto.getFechaInicio());
				nuevoContrato.setFechaFin(contratoDto.getFechaFin());
				nuevoContrato.setTieneAsignacionFamiliar(contratoDto.getTieneAsignacionFamiliar());
				nuevoContrato.setHorasPorSemana(contratoDto.getHorasPorSemana());
				nuevoContrato.setPagoPorHora(contratoDto.getPagoPorHora());
				nuevoContrato.setPuesto(contratoDto.getPuesto());
				nuevoContrato.setCancelado(false);
				this.contratoRepository.save(nuevoContrato);
			}
		}else {
			throw new EmpleadoNotFoundException(CONTRATO_ALREADY_EXISTS);
		}
		return nuevoContrato;
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
		if(!(fechaInicio.equals(tiempoActual) ||
				fechaInicio.after(tiempoActual))) {
			fechasValidas = false;
			throw new ContratoNotValidException(FECHA_INICIO_NOT_VALID);
		}
		
		if((fechaFin.after(fechaInicio))) {
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
	
	private boolean ValidarHorasContratadas(String horasContratadasCad) throws ContratoNotValidException {
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
	
	private boolean ValidarPagoPorHora(String pagoPorHoraCad) throws ContratoNotValidException {
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
			throw new ContratoNotFoundException(NO_CONTRATO_FOUND_BY_DNI + dniEmpleado);
		}
		return contratoVigente;
	}
	
	private ContratoModel obtenerContratoVigente(EmpleadoModel empleado) {
		List<ContratoModel> contratosDeEmpleado = this.contratoRepository.findAllByEmpleado(empleado);
		ContratoModel contratoVigente = null;
		for (ContratoModel contrato: contratosDeEmpleado) {
		      if(ValidarVigencia(contrato)) {
		    	  contratoVigente = contrato;
		      }		      
		}
		return contratoVigente;
	}
	
	private boolean ValidarVigencia(ContratoModel contratoModel) {
		Date tiempoActual = java.sql.Timestamp.valueOf(LocalDateTime.now());
		boolean vigenciaValida = true;
		if(!((contratoModel.getFechaFin().after(tiempoActual) || 
				contratoModel.getFechaFin().equals(tiempoActual)) &&
				!contratoModel.getCancelado())) {
			vigenciaValida = false;
		}
		return vigenciaValida;
	}
	
	
	
	
}
