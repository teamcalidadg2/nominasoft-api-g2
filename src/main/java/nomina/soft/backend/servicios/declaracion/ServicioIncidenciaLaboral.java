package nomina.soft.backend.servicios.declaracion;
import java.util.List;

import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.models.IncidenciaLaboral;


public interface ServicioIncidenciaLaboral {
    
	
	List<IncidenciaLaboral> buscarIncidenciasPorDni(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	IncidenciaLaboral reportarHoraFaltante(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	IncidenciaLaboral reportarHoraExtra(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	
}
