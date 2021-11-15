package nomina.soft.backend.servicios.declaracion;
import java.util.List;

import nomina.soft.backend.dto.PeriodoNominaDto;
import nomina.soft.backend.excepciones.clases.PeriodoNominaExistsException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotValidException;
import nomina.soft.backend.models.PeriodoNomina;


public interface ServicioPeriodoNomina {
    
	public List<PeriodoNomina> obtenerTodosLosPeriodos() throws PeriodoNominaNotFoundException;
	
	public PeriodoNomina buscarPeriodoNominaPorId(Long idPeriodoNomina) throws PeriodoNominaNotFoundException;
	
	public PeriodoNomina guardarNuevoPeriodoNomina(PeriodoNominaDto dtoPeriodoNomina) throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException;
	
	public void eliminarPeriodoNomina(Long idPeriodoNomina) throws PeriodoNominaNotFoundException;
	
}
