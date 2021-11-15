package nomina.soft.backend.Servicios.Declaracion;

import java.util.List;

import nomina.soft.backend.Data_Transfer_Object.DTO_Empleado;
import nomina.soft.backend.Entidades.Empleado;
import nomina.soft.backend.Excepciones.Clases.ContratoNotValidException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoExistsException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;


public interface Servicio_Empleado {
   
	public List<Empleado> obtenerTodos();
    public Empleado guardarNuevoEmpleado(DTO_Empleado dtoEmpleado) throws EmpleadoNotFoundException, EmpleadoExistsException, EmpleadoNotValidException;
    public void eliminarEmpleado(Long idEmpleado) throws EmpleadoNotFoundException;
    
    public Empleado buscarEmpleadoPorDni(String dniEmpleado) throws EmpleadoNotFoundException, NumberFormatException, ContratoNotValidException, EmpleadoNotValidException;
    public Empleado buscarEmpleadoPorTelefono(String telefonoEmpleado) throws EmpleadoNotFoundException;
    public Empleado buscarEmpleadoPorCorreo(String correoEmpleado) throws EmpleadoNotFoundException;
    public Empleado buscarEmpleadoPorId(String idEmpleado) throws EmpleadoNotFoundException, NumberFormatException, ContratoNotValidException;
    
}