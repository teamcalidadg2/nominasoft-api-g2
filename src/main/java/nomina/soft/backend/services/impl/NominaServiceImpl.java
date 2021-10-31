package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.ContratoImplConstant.CONTRATO_CANCELADO;
import static nomina.soft.backend.constant.ContratoImplConstant.CONTRATO_FECHA_FIN_NOT_VALID;
import static nomina.soft.backend.constant.NominaImplConstant.*;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import nomina.soft.backend.exception.domain.NominaNotFoundException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotFoundException;
import nomina.soft.backend.models.BoletaDePagoModel;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.models.NominaModel;
import nomina.soft.backend.models.PeriodoNominaModel;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.repositories.NominaRepository;
import nomina.soft.backend.repositories.PeriodoNominaRepository;
import nomina.soft.backend.services.NominaService;

@Service
@Transactional
public class NominaServiceImpl implements NominaService{

	private NominaRepository nominaRepository;
	private PeriodoNominaRepository periodoNominaRepository;
	private EmpleadoRepository empleadoRepository;
	private ContratoServiceImpl contratoService;
	private BoletaDePagoServiceImpl boletaDePagoService;

	@Autowired
	public NominaServiceImpl(NominaRepository nominaRepository, 
							PeriodoNominaRepository periodoNominaRepository, 
							EmpleadoRepository empleadoRepository,
							ContratoServiceImpl contratoService,
							BoletaDePagoServiceImpl boletaDePagoService) {
		super();
		this.nominaRepository = nominaRepository;
		this.periodoNominaRepository = periodoNominaRepository;
		this.empleadoRepository = empleadoRepository;
		this.contratoService = contratoService;
		this.boletaDePagoService = boletaDePagoService;
	}

	@Override
	public ArrayList<NominaModel> getAll() {
		return (ArrayList<NominaModel>)nominaRepository.findAll();
	}
	
	@Override
	public List<NominaModel> getAllByDescripcion(String descripcion) throws NominaNotFoundException {
		List<NominaModel> lista = this.nominaRepository.findByDescripcionContains(descripcion);
		if(lista==null) {
			throw new NominaNotFoundException(NO_NOMINAS_FOUND_BY_DESCRIPCION + descripcion);
		}
		return lista;
	}

	@Override
	public NominaModel buscarPorId(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException {
		NominaModel nomina = new NominaModel();
		if(nomina.identificadorValido(idNomina)) nomina = this.nominaRepository.findByIdNomina(Long.parseLong(idNomina));
		if(nomina == null) throw new NominaNotFoundException(NO_NOMINA_FOUND_BY_ID);
		return nomina;
	}
	
	@Override
	public List<BoletaDePagoModel> guardarNomina (NominaDto nominaDto) throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException{
		NominaModel nuevaNomina = new NominaModel();
		PeriodoNominaModel periodoNomina = new PeriodoNominaModel();
		if(nuevaNomina.periodoDeNominaValido(nominaDto.getIdPeriodoNomina())) periodoNomina = this.periodoNominaRepository.findByIdPeriodoNomina(Long.parseLong(nominaDto.getIdPeriodoNomina()));
		List<BoletaDePagoModel> listaBoletas = null;
		if(periodoNomina==null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID);
		}
		else {
			List<NominaModel> nominasDePeriodo = periodoNomina.getNominas();
			if(!nuevaNomina.esPeriodoCerrado(nominasDePeriodo)&&
			nuevaNomina.descripcionValida(nominaDto.getDescripcion())){
				nominaDto.corregirFechaZonaHoraria(nominaDto.getFecha());
				nuevaNomina.setPeriodoNomina(periodoNomina);
				nuevaNomina.setFecha(nominaDto.getFecha());
				nuevaNomina.setDescripcion(nominaDto.getDescripcion());
				// this.nominaRepository.save(nuevaNomina);
				listaBoletas = GuardarBoletasDePago(nuevaNomina);
				nuevaNomina.setEstaCerrada(false);
				nuevaNomina.setBoletasDePago(listaBoletas);
				this.nominaRepository.save(nuevaNomina);
			}else throw new PeriodoNominaNotFoundException(PERIODO_CERRADO);
		}
		return nuevaNomina.getBoletasDePago();
	}

	@Override
	public List<BoletaDePagoModel> guardarNomina(Date fecha, String descripcion, String idPeriodoNomina) throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException {
		NominaModel nuevaNomina = new NominaModel();
		PeriodoNominaModel periodoNomina = new PeriodoNominaModel();
		NominaDto nominaDto = new NominaDto();
		nominaDto.setFecha(fecha);
		nominaDto.setDescripcion(descripcion);
		nominaDto.setIdPeriodoNomina(idPeriodoNomina);
		if(nuevaNomina.periodoDeNominaValido(nominaDto.getIdPeriodoNomina())) periodoNomina = this.periodoNominaRepository.findByIdPeriodoNomina(Long.parseLong(nominaDto.getIdPeriodoNomina()));
		List<BoletaDePagoModel> listaBoletas = null;
		if(periodoNomina==null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID);
		}
		else {
			List<NominaModel> nominasDePeriodo = periodoNomina.getNominas();
			if(!nuevaNomina.esPeriodoCerrado(nominasDePeriodo)&&
			nuevaNomina.descripcionValida(nominaDto.getDescripcion())){
				nominaDto.corregirFechaZonaHoraria(nominaDto.getFecha());
				nuevaNomina.setPeriodoNomina(periodoNomina);
				nuevaNomina.setFecha(nominaDto.getFecha());
				nuevaNomina.setDescripcion(nominaDto.getDescripcion());
				// this.nominaRepository.save(nuevaNomina);
				listaBoletas = GuardarBoletasDePago(nuevaNomina);
				nuevaNomina.setEstaCerrada(false);
				nuevaNomina.setBoletasDePago(listaBoletas);
				this.nominaRepository.save(nuevaNomina);
			}else throw new PeriodoNominaNotFoundException(PERIODO_CERRADO);
		}
		return nuevaNomina.getBoletasDePago();
	}
	
	private List<BoletaDePagoModel> GuardarBoletasDePago(NominaModel nuevaNomina) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException {
		List<EmpleadoModel> listaEmpleados = this.empleadoRepository.findAll();
		List<BoletaDePagoModel> boletasDePago = new ArrayList<BoletaDePagoModel>();
		NominaModel nominaTemporal = new NominaModel();
		for (EmpleadoModel empleado: listaEmpleados) {
			ContratoModel contratoVigente = this.contratoService.buscarContratoPorDni(empleado.getDni());
			if(nominaTemporal.validarContratoConNomina(contratoVigente, nuevaNomina)){
				boletasDePago.add(this.boletaDePagoService.guardarBoletaDePago(contratoVigente,nuevaNomina));
			}
	  	}
		return boletasDePago;
	}

	


	@Override
	public List<BoletaDePagoModel> generarNomina(NominaDto nominaDto) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, PeriodoNominaNotFoundException, EmpleadoNotValidException {
		NominaModel nuevaNomina = new NominaModel();
		PeriodoNominaModel periodoNomina = new PeriodoNominaModel();
		if(nuevaNomina.periodoDeNominaValido(nominaDto.getIdPeriodoNomina())) periodoNomina = this.periodoNominaRepository.findByIdPeriodoNomina(Long.parseLong(nominaDto.getIdPeriodoNomina()));
		List<BoletaDePagoModel> listaBoletas = null;
		if(periodoNomina==null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID);
		}
		else {
			List<NominaModel> nominasDePeriodo = periodoNomina.getNominas();
			if(!nuevaNomina.esPeriodoCerrado(nominasDePeriodo) &&
				nuevaNomina.descripcionValida(nominaDto.getDescripcion())){
				nominaDto.corregirFechaZonaHoraria(nominaDto.getFecha());
				nuevaNomina.setPeriodoNomina(periodoNomina);
				nuevaNomina.setFecha(nominaDto.getFecha());
				nuevaNomina.setDescripcion(nominaDto.getDescripcion());
				listaBoletas = GenerarBoletasDePago(nuevaNomina);
				nuevaNomina.setEstaCerrada(false);
				nuevaNomina.setBoletasDePago(listaBoletas);
			}else throw new PeriodoNominaNotFoundException(PERIODO_CERRADO);
		}
		return nuevaNomina.getBoletasDePago();
	}

	@Override
	public List<BoletaDePagoModel> generarNomina(Date fecha, String descripcion, String idPeriodoNomina) throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException {
		NominaModel nuevaNomina = new NominaModel();
		PeriodoNominaModel periodoNomina = new PeriodoNominaModel();
		NominaDto nominaDto = new NominaDto();
		nominaDto.setFecha(fecha);
		nominaDto.setDescripcion(descripcion);
		nominaDto.setIdPeriodoNomina(idPeriodoNomina);
		if(nuevaNomina.periodoDeNominaValido(nominaDto.getIdPeriodoNomina())) periodoNomina = this.periodoNominaRepository.findByIdPeriodoNomina(Long.parseLong(nominaDto.getIdPeriodoNomina()));
		List<BoletaDePagoModel> listaBoletas = null;
		if(periodoNomina==null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID);
		}
		else {
			List<NominaModel> nominasDePeriodo = periodoNomina.getNominas();
			if(!nuevaNomina.esPeriodoCerrado(nominasDePeriodo) &&
				nuevaNomina.descripcionValida(nominaDto.getDescripcion())){
				nominaDto.corregirFechaZonaHoraria(nominaDto.getFecha());
				nuevaNomina.setPeriodoNomina(periodoNomina);
				nuevaNomina.setFecha(nominaDto.getFecha());
				nuevaNomina.setDescripcion(nominaDto.getDescripcion());
				listaBoletas = GenerarBoletasDePago(nuevaNomina);
				nuevaNomina.setEstaCerrada(false);
				nuevaNomina.setBoletasDePago(listaBoletas);
			}else throw new PeriodoNominaNotFoundException(PERIODO_CERRADO);
		}
		return nuevaNomina.getBoletasDePago();
	}


	private List<BoletaDePagoModel> GenerarBoletasDePago(NominaModel nuevaNomina) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException {
		List<EmpleadoModel> listaEmpleados = this.empleadoRepository.findAll();
		List<BoletaDePagoModel> boletasDePago = new ArrayList<BoletaDePagoModel>();
		NominaModel nominaTemporal = new NominaModel();
		for (EmpleadoModel empleado: listaEmpleados) {
			ContratoModel contratoVigente = this.contratoService.buscarContratoPorDni(empleado.getDni());
			if(nominaTemporal.validarContratoConNomina(contratoVigente, nuevaNomina)){
				boletasDePago.add(this.boletaDePagoService.generarBoletaDePago(contratoVigente,nuevaNomina));
			}
	  	}
		return boletasDePago;
	}


	@Override
	public NominaModel cerrarNomina(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException {
		NominaModel nominaAEditar = new NominaModel();
		if(nominaAEditar.identificadorValido(idNomina)) nominaAEditar = this.nominaRepository.findByIdNomina(Long.parseLong(idNomina));
		if(nominaAEditar!=null){
				nominaAEditar.setEstaCerrada(true);
				this.nominaRepository.save(nominaAEditar);
		}else{
			throw new NominaNotFoundException(NO_NOMINA_FOUND_BY_ID);
		}
		return nominaAEditar;
	}


	@Override
	public void eliminarNomina(String idNomina) throws NominaNotFoundException, NumberFormatException, NominaNotValidException {
		NominaModel nominaAEliminar = new NominaModel();
		if(nominaAEliminar.identificadorValido(idNomina)) nominaAEliminar = this.nominaRepository.findByIdNomina(Long.parseLong(idNomina));
		if(nominaAEliminar!=null){
				this.nominaRepository.delete(nominaAEliminar);
		}else{
			throw new NominaNotFoundException(NO_NOMINA_FOUND_BY_ID);
		}		
	}








}
