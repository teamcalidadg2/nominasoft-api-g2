package nomina.soft.backend.services.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.IncidenciaLaboralModel;
import nomina.soft.backend.models.PeriodoNominaModel;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.repositories.IncidenciaLaboralRepository;
import nomina.soft.backend.services.IncidenciaLaboralService;

@Service
@Transactional
public class IncidenciaLaboralServiceImpl implements IncidenciaLaboralService{

	private IncidenciaLaboralRepository incidenciaLaboralRepository;
	private ContratoServiceImpl contratoService;

	@Autowired
	public IncidenciaLaboralServiceImpl(IncidenciaLaboralRepository incidenciaLaboralRepository,
										ContratoServiceImpl contratoService) {
		super();
		this.incidenciaLaboralRepository = incidenciaLaboralRepository;
		this.contratoService = contratoService;
	}

	@Override
	public List<IncidenciaLaboralModel> buscarIncidenciasPorDni(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException {
		ContratoModel contratoVigente = this.contratoService.buscarContratoPorDni(dni);
		List<IncidenciaLaboralModel> lista = null;
		if(contratoVigente!= null) {
			lista = contratoVigente.getIncidenciasLaborales();
		}
		return lista;
	}

	@Override
	public IncidenciaLaboralModel reportarHoraFaltante(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException {
		ContratoModel contratoVigente = this.contratoService.buscarContratoPorDni(dni);
		IncidenciaLaboralModel incidenciaLaboralVigente = new IncidenciaLaboralModel();
		List<IncidenciaLaboralModel> lista = null;
		if(contratoVigente!= null) {
			incidenciaLaboralVigente = obtenerIncidenciaLaboralVigente(contratoVigente);
			incidenciaLaboralVigente.reportarHoraFaltante();
			this.incidenciaLaboralRepository.save(incidenciaLaboralVigente);
		}
		return incidenciaLaboralVigente;
	}

	private IncidenciaLaboralModel obtenerIncidenciaLaboralVigente(ContratoModel contrato) {
		List<IncidenciaLaboralModel> incidenciasLaborales = contrato.getIncidenciasLaborales();
		IncidenciaLaboralModel incidenciaLaboralVigente = null;
		for(IncidenciaLaboralModel incidenciaLaboral: incidenciasLaborales){
			if(validarPeriodoNomina(incidenciaLaboral.getPeriodoNomina())){
				incidenciaLaboralVigente = incidenciaLaboral;
			}
		}
		return incidenciaLaboralVigente;
	}

	private boolean validarPeriodoNomina(PeriodoNominaModel periodoNomina) {
		boolean periodoNominaValido = true;
		if(periodoNomina != this.contratoService.obtenerPeriodoNominaVigente()){
			periodoNominaValido = false;
		}
		return periodoNominaValido;
	}

	@Override
	public IncidenciaLaboralModel reportarHoraExtra(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException {
		ContratoModel contratoVigente = this.contratoService.buscarContratoPorDni(dni);
		IncidenciaLaboralModel incidenciaLaboralVigente = null;
		List<IncidenciaLaboralModel> lista = null;
		if(contratoVigente!= null) {
			incidenciaLaboralVigente = obtenerIncidenciaLaboralVigente(contratoVigente);
			incidenciaLaboralVigente.reportarHoraExtra();
			this.incidenciaLaboralRepository.save(incidenciaLaboralVigente);
		}
		return incidenciaLaboralVigente;
	}
	
	
	
}
