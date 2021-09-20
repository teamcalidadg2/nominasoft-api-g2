package nomina.soft.backend.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.exception.domain.EmpleadoExistsException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.repositories.EmpleadoRepository;


public interface EmpleadoService {
   
	public ArrayList<EmpleadoModel> getAll();
    public boolean existsByEmail(String email);
    public EmpleadoModel guardarEmpleado(EmpleadoDto empleadoDto) throws EmpleadoNotFoundException, EmpleadoExistsException;
    public void delete(int id);
    
    public EmpleadoModel buscarEmpleadoPorDni(String dni) throws EmpleadoNotFoundException;
    public EmpleadoModel buscarEmpleadoPorTelefono(String telefono) throws EmpleadoNotFoundException;
    public EmpleadoModel buscarEmpleadoPorCorreo(String correo) throws EmpleadoNotFoundException;
    
}