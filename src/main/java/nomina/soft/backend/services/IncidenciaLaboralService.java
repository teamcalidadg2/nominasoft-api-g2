package nomina.soft.backend.services;
import java.util.List;

import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.IncidenciaLaboralModel;


public interface IncidenciaLaboralService {
    
	
	List<IncidenciaLaboralModel> buscarIncidenciasPorDni(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException;
	IncidenciaLaboralModel reportarHoraFaltante(String dni) throws EmpleadoNotFoundException;
	IncidenciaLaboralModel reportarHoraExtra(String dni) throws EmpleadoNotFoundException;
	
}
