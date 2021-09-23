package nomina.soft.backend.services.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static nomina.soft.backend.constant.NominaImplConstant.NO_NOMINA_FOUND_BY_ID;
import static nomina.soft.backend.constant.NominaImplConstant.NO_PERIODO_FOUND_BY_ID;
import static nomina.soft.backend.constant.ContratoImplConstant.CONTRATO_NOT_FOUND;
import static nomina.soft.backend.constant.ContratoImplConstant.CONTRATO_CERRADO;
import static nomina.soft.backend.constant.NominaImplConstant.FECHAS_INVALIDAS;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.exception.domain.ContratoExistsException;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.NominaExistsException;
import nomina.soft.backend.exception.domain.NominaNotFoundException;
import nomina.soft.backend.exception.domain.NominaNotValidException;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.NominaModel;
import nomina.soft.backend.models.PeriodoNominaModel;
import nomina.soft.backend.repositories.ContratoRepository;
import nomina.soft.backend.repositories.NominaRepository;
import nomina.soft.backend.repositories.PeriodoNominaRepository;
import nomina.soft.backend.services.NominaService;

@Service
@Transactional
public class NominaServiceImpl implements NominaService{

	private NominaRepository nominaRepository;
	private PeriodoNominaRepository periodoNominaRepository;
	private ContratoRepository contratoNominaRepository;

	@Autowired
	public NominaServiceImpl(NominaRepository nominaRepository, PeriodoNominaRepository periodoNominaRepository, ContratoRepository contratoNominaRepository) {
		super();
		this.nominaRepository = nominaRepository;
		this.periodoNominaRepository = periodoNominaRepository;
		this.contratoNominaRepository = contratoNominaRepository;
	}


	@Override
	public ArrayList<NominaModel> getAll() {
		return (ArrayList<NominaModel>)nominaRepository.findAll();
	}
	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NominaModel buscarPorId(int id) throws NominaNotFoundException {
		NominaModel nomina = this.nominaRepository.findById(id);
		if(nomina == null){
			throw new NominaNotFoundException(NO_NOMINA_FOUND_BY_ID + id);
		}
		return nomina;
	}
	
	@Override
	public NominaModel guardarNomina (NominaDto nominaDto, int contrato_id) throws NominaNotFoundException, NominaNotValidException, NominaExistsException, ContratoNotFoundException, ContratoNotValidException{

		NominaModel nuevaNomina = null;
		PeriodoNominaModel periodoNomina = this.periodoNominaRepository.getById(nominaDto.getPeriodo_nomina_id());
		Boolean generarNominaValido = false;


		if(periodoNomina==null) {
			throw new NominaNotFoundException(NO_PERIODO_FOUND_BY_ID);
		}

		ContratoModel contrato = this.contratoNominaRepository.getById(contrato_id);
		if(contrato==null) {
			throw new ContratoNotFoundException(CONTRATO_NOT_FOUND);
		}
		Date fechaGeneracion= java.sql.Timestamp.valueOf(LocalDateTime.now());





		// | REGLA 06
		// el contrato no debe estar cancelado | REGLA 06
		if(contrato.getCancelado()==true){
			throw new ContratoNotValidException(CONTRATO_CERRADO);
		}
		if(ValidarFechas(contrato.getFechaFin(), periodoNomina.getFechaInicio(), periodoNomina.getFechaFin(), fechaGeneracion)){
			generarNominaValido = true;
		}else{
			throw new NominaNotValidException(FECHAS_INVALIDAS);
		}

		if(generarNominaValido){
			// | REGLA 07
			int total_de_horas = CalcularTotalDeHoras(periodoNomina,contrato.getHorasPorSemana());
			// | REGLA 08
			double sueldoBasico = CalcularSueldoBasico(total_de_horas,contrato.getPagoPorHora());

			double montoPorAsignacionFamiliar = CalcularMontoPorAsignacionFamiliar(sueldoBasico,contrato);
		}

		return nuevaNomina;
	}

	// | REGLA 06
	private boolean ValidarFechas(Date fechaFinalContrato, Date fechaInicioPeriodoNomina, Date fechaFinPeriodoNomina, Date fechaGeneracion) throws ContratoNotValidException  {
		boolean fechasValidas = true;
		// la fecha final del contrato debe ser superior a la fecha inicio del periodo de Nómina | REGLA 06
		if(fechaFinalContrato.before(fechaInicioPeriodoNomina)){
			fechasValidas = false;
			throw new ContratoNotValidException("error la fecha final del contrato esta antes del inicio del periodo");
		}
		// la fecha fin del periodo de Nómina debe ser menor a la fecha de generación de la Nómina	| REGLA 06
		if(fechaFinPeriodoNomina.after(fechaGeneracion)){
			throw new ContratoNotValidException("error la fecha final del periodo es mayor a la actual ");
		}

		return fechasValidas;
	}

	// | REGLA 07
	private int CalcularTotalDeHoras(PeriodoNominaModel periodoNomina, String horasPorSemanaStr) throws ContratoNotValidException{
		int total_de_horas = 0;
		int horasPorSemana = 0;
		try {
			horasPorSemana = Integer.parseInt(horasPorSemanaStr);
		} catch (NumberFormatException nfe){
			throw new ContratoNotValidException("Error horas por semana Funcion CalcularTotalDeHoras");
		}

		int cant_semanas_periodo = (Period.between(LocalDate.ofInstant(periodoNomina.getFechaInicio().toInstant(), ZoneId.systemDefault()),
													LocalDate.ofInstant(periodoNomina.getFechaFin().toInstant(), ZoneId.systemDefault())).getDays())/7;
		total_de_horas = horasPorSemana * cant_semanas_periodo;

		return total_de_horas;
	}

	// | REGLA 08
	private double CalcularSueldoBasico(int total_de_horas, String pagoPorHoraStr) throws ContratoNotValidException{
		double sueldoBasico = 0;
		double pagoPorHora = 0;
		try {
			pagoPorHora = Integer.parseInt(pagoPorHoraStr);
		} catch (NumberFormatException nfe){
			throw new ContratoNotValidException("Error cadena de pago por horas");
		}

		sueldoBasico = pagoPorHora * total_de_horas;

		return sueldoBasico;
	}

	// | REGLA 09
	private double CalcularMontoPorAsignacionFamiliar(Double sueldoMinimo, ContratoModel contrato) throws ContratoNotValidException{
		double montoPorAsignacionFamiliar = 0;

		if(contrato.getTieneAsignacionFamiliar()){
			montoPorAsignacionFamiliar = sueldoMinimo * 0.1;
		}else{
			throw new ContratoNotValidException("No tiene asignacion familiar.");
		}

		return montoPorAsignacionFamiliar;
	}
}
