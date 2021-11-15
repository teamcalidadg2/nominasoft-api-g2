package nomina.soft.backend.Servicios.Declaracion;
import java.util.List;

import nomina.soft.backend.Entidades.IncidenciaLaboral;
import nomina.soft.backend.Excepciones.Clases.ContratoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;


public interface Servicio_IncidenciaLaboral {
    
	
	List<IncidenciaLaboral> buscarIncidenciasPorDni(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	IncidenciaLaboral reportarHoraFaltante(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	IncidenciaLaboral reportarHoraExtra(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException;
	
}
