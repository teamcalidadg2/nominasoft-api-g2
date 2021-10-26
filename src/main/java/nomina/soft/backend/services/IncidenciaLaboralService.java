package nomina.soft.backend.services;
import java.util.List;

import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import nomina.soft.backend.models.IncidenciaLaboralModel;


public interface IncidenciaLaboralService {
    
	
	List<IncidenciaLaboralModel> buscarIncidenciasPorDni(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	IncidenciaLaboralModel reportarHoraFaltante(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	IncidenciaLaboralModel reportarHoraExtra(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	
}
