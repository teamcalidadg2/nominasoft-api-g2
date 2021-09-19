package nomina.soft.backend.services.impl;

import static nomina.soft.backend.constant.EmpleadoImplConstant.CORREO_ALREADY_EXISTS;
import static nomina.soft.backend.constant.EmpleadoImplConstant.DNI_ALREADY_EXISTS;
import static nomina.soft.backend.constant.EmpleadoImplConstant.NO_EMPLEADO_FOUND_BY_CORREO;
import static nomina.soft.backend.constant.EmpleadoImplConstant.NO_EMPLEADO_FOUND_BY_DNI;
import static nomina.soft.backend.constant.EmpleadoImplConstant.NO_EMPLEADO_FOUND_BY_TELEFONO;
import static nomina.soft.backend.constant.EmpleadoImplConstant.TELEFONO_ALREADY_EXISTS;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.exception.domain.EmpleadoExistsException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.repositories.EmpleadoRepository;
import nomina.soft.backend.services.EmpleadoService;

@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService{

    private EmpleadoRepository empleadoRepository;
    
	@Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
		super();
		this.empleadoRepository = empleadoRepository;
	}
	
	@Override
	public ArrayList<EmpleadoModel> getAll(){
        return(ArrayList<EmpleadoModel>)empleadoRepository.findAll();
    }
	
	@Override
    public boolean existsByEmail(String email) {
        return empleadoRepository.existsByCorreo(email);
    }
    
	@Override
    public EmpleadoModel guardarEmpleado(EmpleadoDto empleadoDto) throws EmpleadoNotFoundException, EmpleadoExistsException{
        EmpleadoModel empleado = new EmpleadoModel();
		validateNewDni(EMPTY,empleadoDto.getDni());
		validateNewTelefono(EMPTY,empleadoDto.getTelefono());
		validateNewCorreo(EMPTY,empleadoDto.getCorreo());
		
        empleado.setNombres(empleadoDto.getNombres());
        empleado.setApellidos(empleadoDto.getApellidos());
        empleado.setDni(empleadoDto.getDni());
        empleado.setFechaNacimiento(empleadoDto.getFechaNacimiento());
        empleado.setTelefono(empleadoDto.getTelefono());
        empleado.setCorreo(empleadoDto.getCorreo());
        empleado.setDireccion(empleadoDto.getDireccion());
        empleadoRepository.save(empleado);
        return empleado;
    }

	@Override
    public void delete(int id){
        empleadoRepository.deleteById(id);
    }


	@Override
	public EmpleadoModel buscarEmpleadoPorDni(String dni) {
		return this.empleadoRepository.findByDni(dni);
	}
	
	@Override
	public EmpleadoModel buscarEmpleadoPorTelefono(String telefono) {
		return this.empleadoRepository.findByTelefono(telefono);
	}

	@Override
	public EmpleadoModel buscarEmpleadoPorCorreo(String correo) {
		return this.empleadoRepository.findByCorreo(correo);
	}
	
	
	private EmpleadoModel validateNewDni(String actualDni, String nuevoDni) throws EmpleadoNotFoundException, EmpleadoExistsException {
		EmpleadoModel empleadoConNuevoDni = buscarEmpleadoPorDni(nuevoDni);
        if(StringUtils.isNotBlank(actualDni)) {
        	EmpleadoModel actualEmpleado = buscarEmpleadoPorDni(actualDni);
            if(actualEmpleado == null) {
                throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + actualDni);
            }
            if(empleadoConNuevoDni != null && (actualEmpleado.getEmpleado_id() != empleadoConNuevoDni.getEmpleado_id())) {
                throw new EmpleadoExistsException(DNI_ALREADY_EXISTS);
            }
            return actualEmpleado;
        } else {
            if(empleadoConNuevoDni != null) {
                throw new EmpleadoExistsException(DNI_ALREADY_EXISTS);
            }
            return null;
        }
    }
	
	private EmpleadoModel validateNewTelefono(String actualTelefono, String nuevoTelefono) throws EmpleadoNotFoundException, EmpleadoExistsException {
		EmpleadoModel empleadoConNuevoTelefono = buscarEmpleadoPorTelefono(nuevoTelefono);
        if(StringUtils.isNotBlank(actualTelefono)) {
        	EmpleadoModel actualEmpleado = buscarEmpleadoPorTelefono(actualTelefono);
            if(actualEmpleado == null) {
                throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_TELEFONO + actualTelefono);
            }
            if(empleadoConNuevoTelefono != null && (actualEmpleado.getEmpleado_id() != empleadoConNuevoTelefono.getEmpleado_id())) {
                throw new EmpleadoExistsException(TELEFONO_ALREADY_EXISTS);
            }
            return actualEmpleado;
        } else {
            if(empleadoConNuevoTelefono != null) {
                throw new EmpleadoExistsException(TELEFONO_ALREADY_EXISTS);
            }
            return null;
        }
    }
	
	private EmpleadoModel validateNewCorreo(String actualCorreo, String nuevoCorreo) throws EmpleadoNotFoundException, EmpleadoExistsException {
		EmpleadoModel empleadoConNuevoCorreo = buscarEmpleadoPorCorreo(nuevoCorreo);
        if(StringUtils.isNotBlank(actualCorreo)) {
        	EmpleadoModel actualEmpleado = buscarEmpleadoPorCorreo(actualCorreo);
            if(actualEmpleado == null) {
                throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_CORREO + actualCorreo);
            }
            if(empleadoConNuevoCorreo != null && (actualEmpleado.getEmpleado_id() != empleadoConNuevoCorreo.getEmpleado_id())) {
                throw new EmpleadoExistsException(CORREO_ALREADY_EXISTS);
            }
            return actualEmpleado;
        } else {
            if(empleadoConNuevoCorreo != null) {
                throw new EmpleadoExistsException(CORREO_ALREADY_EXISTS);
            }
            return null;
        }
    }


	
	
}
