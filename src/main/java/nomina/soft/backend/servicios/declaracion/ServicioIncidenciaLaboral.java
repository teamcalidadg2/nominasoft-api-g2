package nomina.soft.backend.servicios.declaracion;
import java.text.ParseException;
import java.util.List;

import nomina.soft.backend.entidades.IncidenciaLaboral;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;


public interface ServicioIncidenciaLaboral {
    
	
	List<IncidenciaLaboral> buscarIncidenciasPorDni(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException, ParseException;
	IncidenciaLaboral reportarHoraFaltante(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException, ParseException;
	IncidenciaLaboral reportarHoraExtra(String dni) throws EmpleadoNotFoundException, ContratoNotFoundException, EmpleadoNotValidException, ParseException;
	
}
