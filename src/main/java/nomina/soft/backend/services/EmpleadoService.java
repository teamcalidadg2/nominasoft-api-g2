package nomina.soft.backend.services;

import java.util.List;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.exception.domain.EmpleadoExistsException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import nomina.soft.backend.models.EmpleadoModel;


public interface EmpleadoService {
   
	public List<EmpleadoModel> getAll();
    public boolean existsByEmail(String email);
    public EmpleadoModel guardarEmpleado(EmpleadoDto empleadoDto) throws EmpleadoNotFoundException, EmpleadoExistsException, EmpleadoNotValidException;
    public void delete(Long id);
    
    public EmpleadoModel buscarEmpleadoPorDni(String dni) throws EmpleadoNotFoundException;
    public EmpleadoModel buscarEmpleadoPorTelefono(String telefono) throws EmpleadoNotFoundException;
    public EmpleadoModel buscarEmpleadoPorCorreo(String correo) throws EmpleadoNotFoundException;
    
}