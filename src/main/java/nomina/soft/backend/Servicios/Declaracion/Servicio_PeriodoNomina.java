package nomina.soft.backend.Servicios.Declaracion;
import java.util.List;

import nomina.soft.backend.Data_Transfer_Object.DTO_PeriodoNomina;
import nomina.soft.backend.Entidades.PeriodoNomina;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaExistsException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotValidException;


public interface Servicio_PeriodoNomina {
    
	public List<PeriodoNomina> obtenerTodosLosPeriodos() throws PeriodoNominaNotFoundException;
	
	public PeriodoNomina buscarPeriodoNominaPorId(Long idPeriodoNomina) throws PeriodoNominaNotFoundException;
	
	public PeriodoNomina guardarNuevoPeriodoNomina(DTO_PeriodoNomina dtoPeriodoNomina) throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException;
	
	public void eliminarPeriodoNomina(Long idPeriodoNomina) throws PeriodoNominaNotFoundException;
	
}
