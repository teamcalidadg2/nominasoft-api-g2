package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.ContratoImplConstant.CONTRATO_CANCELADO;
import static nomina.soft.backend.constant.ContratoImplConstant.CONTRATO_FECHA_FIN_NOT_VALID;
import static nomina.soft.backend.constant.NominaImplConstant.*;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.PERIODO_NOT_FOUND_BY_ID;

import java.time.LocalDateTime;
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
		List<NominaModel> lista = this.nominaRepository.findAllByDescripcion(descripcion);
		if(lista==null) {
			throw new NominaNotFoundException(NO_NOMINAS_FOUND_BY_DESCRIPCION + descripcion);
		}
		return lista;
	}

	@Override
	public NominaModel buscarPorId(Long idNomina) throws NominaNotFoundException {
		NominaModel nomina = this.nominaRepository.findByIdNomina(idNomina);
		if(nomina == null){
			throw new NominaNotFoundException(NO_NOMINA_FOUND_BY_ID + idNomina);
		}
		return nomina;
	}
	
	@Override
	public List<BoletaDePagoModel> guardarNomina (NominaDto nominaDto) throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException{
		NominaModel nuevaNomina = new NominaModel();
		PeriodoNominaModel periodoNomina = this.periodoNominaRepository.getById(nominaDto.getIdPeriodoNomina());
		List<BoletaDePagoModel> listaBoletas = null;
		if(periodoNomina==null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID + nominaDto.getIdPeriodoNomina());
		}
		else {
			nuevaNomina.setPeriodoNomina(periodoNomina);
			nuevaNomina.setFecha(nominaDto.getFecha());
			nuevaNomina.setDescripcion(nominaDto.getDescripcion());
			this.nominaRepository.save(nuevaNomina);
			listaBoletas = GuardarBoletasDePago(nuevaNomina);
			nuevaNomina.setEstaCerrada(false);
			nuevaNomina.setBoletasDePago(listaBoletas);
			this.nominaRepository.save(nuevaNomina);
		}
		return nuevaNomina.getBoletasDePago();
	}
	
	private List<BoletaDePagoModel> GuardarBoletasDePago(NominaModel nuevaNomina) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException {
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
	public List<BoletaDePagoModel> generarNomina(NominaDto nominaDto) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException, PeriodoNominaNotFoundException {
		NominaModel nuevaNomina = new NominaModel();
		PeriodoNominaModel periodoNomina = this.periodoNominaRepository.getById(nominaDto.getIdPeriodoNomina());
		List<BoletaDePagoModel> listaBoletas = null;
		if(periodoNomina==null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID + nominaDto.getIdPeriodoNomina());
		}
		else {
			nuevaNomina.setPeriodoNomina(periodoNomina);
			nuevaNomina.setFecha(nominaDto.getFecha());
			nuevaNomina.setDescripcion(nominaDto.getDescripcion());
			listaBoletas = GenerarBoletasDePago(nuevaNomina);
			nuevaNomina.setEstaCerrada(false);
			nuevaNomina.setBoletasDePago(listaBoletas);
		}
		return nuevaNomina.getBoletasDePago();
	}

	private List<BoletaDePagoModel> GenerarBoletasDePago(NominaModel nuevaNomina) throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, NominaNotValidException {
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
	public NominaModel cerrarNomina(Long idNomina) throws NominaNotFoundException {
		NominaModel nominaAEditar = this.nominaRepository.getById(idNomina);
		if(nominaAEditar!=null){
				nominaAEditar.setEstaCerrada(true);
				this.nominaRepository.save(nominaAEditar);
		}else{
			throw new NominaNotFoundException(NO_NOMINA_FOUND_BY_ID + idNomina.toString());
		}
		return nominaAEditar;
	}


	@Override
	public void eliminarNomina(Long idNomina) throws NominaNotFoundException {
		NominaModel nominaAEliminar = this.nominaRepository.getById(idNomina);
		if(nominaAEliminar!=null){
				this.nominaRepository.delete(nominaAEliminar);
		}else{
			throw new NominaNotFoundException(NO_NOMINA_FOUND_BY_ID + idNomina.toString());
		}		
	}





}
