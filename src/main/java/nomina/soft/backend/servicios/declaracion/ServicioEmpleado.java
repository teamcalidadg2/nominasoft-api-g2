package nomina.soft.backend.servicios.declaracion;

import java.util.List;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.entidades.Empleado;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoExistsException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;


public interface ServicioEmpleado {
   
	public List<Empleado> obtenerTodos();
    public Empleado guardarNuevoEmpleado(EmpleadoDto dtoEmpleado) throws EmpleadoNotFoundException, EmpleadoExistsException, EmpleadoNotValidException;
    public void eliminarEmpleado(Long idEmpleado) throws EmpleadoNotFoundException;
    
    public Empleado buscarEmpleadoPorDni(String dniEmpleado) throws EmpleadoNotFoundException, NumberFormatException, ContratoNotValidException, EmpleadoNotValidException;
    public Empleado buscarEmpleadoPorTelefono(String telefonoEmpleado) throws EmpleadoNotFoundException;
    public Empleado buscarEmpleadoPorCorreo(String correoEmpleado) throws EmpleadoNotFoundException;
    public Empleado buscarEmpleadoPorId(String idEmpleado) throws EmpleadoNotFoundException, NumberFormatException, ContratoNotValidException;
    
}