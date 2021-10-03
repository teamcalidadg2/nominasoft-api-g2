package nomina.soft.backend.services;
import java.text.ParseException;
import java.util.List;

import nomina.soft.backend.dto.PeriodoNominaDto;
import nomina.soft.backend.exception.domain.PeriodoNominaExistsException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotFoundException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotValidException;
import nomina.soft.backend.models.PeriodoNominaModel;


public interface PeriodoNominaService {
    
	public List<PeriodoNominaModel> getAll() throws PeriodoNominaNotFoundException;
	
	public PeriodoNominaModel buscarPeriodoNominaPorId(Long idPeriodoNomina) throws PeriodoNominaNotFoundException;
	
	public PeriodoNominaModel guardarPeriodoNomina(PeriodoNominaDto periodoNominaDto) throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException;
	
	public void deletePeriodoNomina(Long idPeriodoNomina) throws PeriodoNominaNotFoundException;
	
}
