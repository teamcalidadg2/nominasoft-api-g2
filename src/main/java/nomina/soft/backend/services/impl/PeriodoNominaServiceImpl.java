package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.PeriodoNominaImplConstant.FECHAS_NOT_VALID;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.FECHA_FIN_ALREADY_EXISTS;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.FECHA_INICIO_ALREADY_EXISTS;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.NO_NOMINA_FOUND_BY_FECHA_FIN;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.NO_NOMINA_FOUND_BY_FECHA_INICIO;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.NO_PERIODOS_FOUND;
import static nomina.soft.backend.constant.PeriodoNominaImplConstant.PERIODO_NOT_FOUND_BY_ID;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.PeriodoNominaDto;
import nomina.soft.backend.exception.domain.PeriodoNominaExistsException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotFoundException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotValidException;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.models.IncidenciaLaboralModel;
import nomina.soft.backend.models.PeriodoNominaModel;
import nomina.soft.backend.repositories.ContratoRepository;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.repositories.IncidenciaLaboralRepository;
import nomina.soft.backend.repositories.PeriodoNominaRepository;
import nomina.soft.backend.services.PeriodoNominaService;

@Service
@Transactional
public class PeriodoNominaServiceImpl implements PeriodoNominaService{

	private PeriodoNominaRepository periodoNominaRepository;
	private EmpleadoRepository empleadoRepository;
	private ContratoRepository contratoRepository;
	private ContratoServiceImpl contratoService;
	private IncidenciaLaboralRepository incidenciaLaboralRepository;

	
	@Autowired
	public PeriodoNominaServiceImpl(PeriodoNominaRepository periodoNominaRepository,
									EmpleadoRepository empleadoRepository,
									ContratoRepository contratoRepository,
									ContratoServiceImpl contratoService,
									IncidenciaLaboralRepository incidenciaLaboralRepository) {
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
		if(lista == null) {
			throw new PeriodoNominaNotFoundException(NO_PERIODOS_FOUND);
		}
		return lista;
	}


	@Override
	public PeriodoNominaModel guardarPeriodoNomina(PeriodoNominaDto periodoNominaDto) throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException {
		PeriodoNominaModel periodoNomina = new PeriodoNominaModel();
		validateNewFechaInicio(null,periodoNominaDto.getFechaInicio());
		validateNewFechaFin(null,periodoNominaDto.getFechaFin());
		if(validarFechas(periodoNominaDto)){
			periodoNomina.setDescripcion(periodoNominaDto.getDescripcion());
			periodoNomina.setFechaInicio(periodoNominaDto.getFechaInicio());
			periodoNomina.setFechaFin(periodoNominaDto.getFechaFin());
        	this.periodoNominaRepository.save(periodoNomina);
			PeriodoNominaModel periodoNominaNuevo = this.periodoNominaRepository.findByFechaInicio(periodoNomina.getFechaFin());
			generarCamposIncidenciaLaboral(periodoNominaNuevo);
        	this.periodoNominaRepository.save(periodoNominaNuevo);
		}
        return periodoNomina;
	}




	private void generarCamposIncidenciaLaboral(PeriodoNominaModel periodoNomina) {
		List<EmpleadoModel> listaEmpleados = this.empleadoRepository.findAll();
		for(EmpleadoModel empleado:listaEmpleados){
			List<ContratoModel> listaContratos = this.contratoRepository.findAllByEmpleado(empleado);
			if(!listaContratos.isEmpty()){
				ContratoModel contratoMasReciente = listaContratos.get(listaContratos.size() - 1);
				if(validarContratoConPeriodoNomina(contratoMasReciente,periodoNomina)){
					IncidenciaLaboralModel nuevaIncidenciaLaboral = new IncidenciaLaboralModel();
					nuevaIncidenciaLaboral.setTotalHorasDeFalta(0);
					nuevaIncidenciaLaboral.setTotalHorasExtras(0);
					nuevaIncidenciaLaboral.setContrato(contratoMasReciente);
					nuevaIncidenciaLaboral.setPeriodoNomina(periodoNomina);
					this.incidenciaLaboralRepository.save(nuevaIncidenciaLaboral);
					evaluarIncidenciasLaborales(contratoMasReciente,nuevaIncidenciaLaboral);
					this.incidenciaLaboralRepository.save(nuevaIncidenciaLaboral);
					periodoNomina.addIncidenciaLaboral(nuevaIncidenciaLaboral);
					contratoMasReciente.addIncidenciaLaboral(nuevaIncidenciaLaboral);
					this.contratoRepository.save(contratoMasReciente);
				}
			}
		}
	}

	private boolean validarContratoConPeriodoNomina(ContratoModel contratoMasReciente, PeriodoNominaModel periodo){
		boolean contratoValido = true;
		if(!(this.contratoService.validarVigencia(contratoMasReciente))) contratoValido = false;
		if(contratoMasReciente.getEstaCancelado()) contratoValido = false;
		if(contratoMasReciente.getFechaFin().before(periodo.getFechaInicio())) contratoValido = false;
		return contratoValido;
	}


	private void evaluarIncidenciasLaborales(ContratoModel contratoMasReciente, IncidenciaLaboralModel nuevaIncidenciaLaboral) {
		PeriodoNominaModel periodoNomina = nuevaIncidenciaLaboral.getPeriodoNomina();
		if(contratoMasReciente.getFechaFin().before(periodoNomina.getFechaFin())){
			int duracionPeriodoNomina = Period.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.systemDefault()),
										LocalDate.ofInstant(periodoNomina.getFechaFin().toInstant(), ZoneId.systemDefault()))
										.getDays();
			int duracionLaburoDurantePeriodo = Period.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.systemDefault()),
										LocalDate.ofInstant(contratoMasReciente.getFechaFin().toInstant(), ZoneId.systemDefault()))
										.getDays();
			int cantidadDiasFaltantes = duracionPeriodoNomina - duracionLaburoDurantePeriodo;
			int totalHorasDeFalta = cantidadDiasFaltantes / 24;
			nuevaIncidenciaLaboral.addTotalHorasDeFalta(totalHorasDeFalta);
		}
	}


	private boolean validarFechas(PeriodoNominaDto periodoNominaDto) throws PeriodoNominaNotValidException{
		boolean fechasValidas = true;
		Date fechaInicio = periodoNominaDto.getFechaInicio();
		Date fechaFin = periodoNominaDto.getFechaFin();
		int duracionDias = Period.between(LocalDate.ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault()),
										LocalDate.ofInstant(fechaFin.toInstant(), ZoneId.systemDefault()))
										.getDays();
		if(!(duracionDias>15 && duracionDias<30)){
			fechasValidas = false;
			throw new PeriodoNominaNotValidException(FECHAS_NOT_VALID);
		}
		return fechasValidas;								
	}


	@Override
	public PeriodoNominaModel buscarPeriodoNominaPorId(Long idPeriodoNomina) throws PeriodoNominaNotFoundException {
		PeriodoNominaModel periodoNomina = this.periodoNominaRepository.findByIdPeriodoNomina(idPeriodoNomina);
		if(periodoNomina == null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID);
		}
		return periodoNomina;
	}


	@Override
	public void deletePeriodoNomina(Long idPeriodoNomina) throws PeriodoNominaNotFoundException {
		PeriodoNominaModel periodoNomina = this.periodoNominaRepository.findByIdPeriodoNomina(idPeriodoNomina);
		if(periodoNomina == null) {
			throw new PeriodoNominaNotFoundException(PERIODO_NOT_FOUND_BY_ID);
		}
		this.periodoNominaRepository.delete(periodoNomina);
	}
	

	private PeriodoNominaModel validateNewFechaInicio(Date actualFechaInicio, Date nuevaFechaInicio) throws PeriodoNominaNotFoundException, PeriodoNominaExistsException{
		PeriodoNominaModel periodoNominaConNuevaFechaInicio = this.periodoNominaRepository.findByFechaInicio(nuevaFechaInicio);
        if(actualFechaInicio!=null) {
        	PeriodoNominaModel actualPeriodoNomina = this.periodoNominaRepository.findByFechaInicio(actualFechaInicio);
            if(actualPeriodoNomina == null) {
                throw new PeriodoNominaNotFoundException(NO_NOMINA_FOUND_BY_FECHA_INICIO + actualFechaInicio.toString());
            }
            if(periodoNominaConNuevaFechaInicio != null && (actualPeriodoNomina.getIdPeriodoNomina() != periodoNominaConNuevaFechaInicio.getIdPeriodoNomina ())) {
                throw new PeriodoNominaExistsException(FECHA_INICIO_ALREADY_EXISTS);
            }
            return actualPeriodoNomina;
        } else {
            if(periodoNominaConNuevaFechaInicio != null) {
                throw new PeriodoNominaExistsException(FECHA_INICIO_ALREADY_EXISTS);
            }
            return null;
        }
    }
	
	private PeriodoNominaModel validateNewFechaFin(Date actualFechaFin, Date nuevaFechaFin) throws PeriodoNominaNotFoundException, PeriodoNominaExistsException{
		PeriodoNominaModel periodoNominaConNuevaFechaFin = this.periodoNominaRepository.findByFechaFin(nuevaFechaFin);
        if(actualFechaFin!=null) {
        	PeriodoNominaModel actualPeriodoNomina = this.periodoNominaRepository.findByFechaInicio(actualFechaFin);
            if(actualPeriodoNomina == null) {
                throw new PeriodoNominaNotFoundException(NO_NOMINA_FOUND_BY_FECHA_FIN + actualFechaFin.toString());
            }
            if(periodoNominaConNuevaFechaFin != null && (actualPeriodoNomina.getIdPeriodoNomina() != periodoNominaConNuevaFechaFin.getIdPeriodoNomina())) {
                throw new PeriodoNominaExistsException(FECHA_FIN_ALREADY_EXISTS);
            }
            return actualPeriodoNomina;
        } else {
            if(periodoNominaConNuevaFechaFin != null) {
                throw new PeriodoNominaExistsException(FECHA_FIN_ALREADY_EXISTS);
            }
            return null;
        }
    }
	
	
	
}
