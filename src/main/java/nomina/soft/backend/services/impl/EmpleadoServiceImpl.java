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
import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.exception.domain.EmpleadoExistsException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import nomina.soft.backend.models.ContratoModel;
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
    public EmpleadoModel guardarEmpleado(EmpleadoDto empleadoDto) throws EmpleadoNotFoundException, EmpleadoExistsException, EmpleadoNotValidException{
        EmpleadoModel empleado = new EmpleadoModel();
		validateNewDni(EMPTY,empleadoDto.getDni());
		validateNewTelefono(EMPTY,empleadoDto.getTelefono());
		validateNewCorreo(EMPTY,empleadoDto.getCorreo());
		if(empleado.validarDni(empleadoDto.getDni())){
            empleado.setNombres(empleadoDto.getNombres());
            empleado.setApellidos(empleadoDto.getApellidos());
            empleado.setDni(empleadoDto.getDni());
            empleado.setFechaNacimiento(empleadoDto.getFechaNacimiento());
            empleado.setTelefono(empleadoDto.getTelefono());
            empleado.setCorreo(empleadoDto.getCorreo());
            empleado.setDireccion(empleadoDto.getDireccion());
            empleado.setContratos(new ArrayList<ContratoModel>());
            empleadoRepository.save(empleado);
        }
        return empleado;
    }

	@Override
    public void delete(Long id){
        empleadoRepository.deleteById(id);
    }


	@Override
	public EmpleadoModel buscarEmpleadoPorDni(String dni) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = this.empleadoRepository.findByDni(dni);
		if(empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + dni);
		}
		return empleado;
	}
	
	@Override
	public EmpleadoModel buscarEmpleadoPorTelefono(String telefono) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = this.empleadoRepository.findByTelefono(telefono);
		if(empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_TELEFONO + telefono);
		}
		return empleado;
	}

	@Override
	public EmpleadoModel buscarEmpleadoPorCorreo(String correo) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = this.empleadoRepository.findByCorreo(correo);
		if(empleado == null) {
			throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_CORREO + correo);
		}
		return empleado;
	}
	
	
	private EmpleadoModel validateNewDni(String actualDni, String nuevoDni) throws EmpleadoNotFoundException, EmpleadoExistsException {
		EmpleadoModel empleadoConNuevoDni = empleadoRepository.findByDni(nuevoDni);
        if(StringUtils.isNotBlank(actualDni)) {
        	EmpleadoModel actualEmpleado = empleadoRepository.findByDni(actualDni);
            if(actualEmpleado == null) {
                throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_DNI + actualDni);
            }
            if(empleadoConNuevoDni != null && (actualEmpleado.getIdEmpleado() != empleadoConNuevoDni.getIdEmpleado())) {
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
		EmpleadoModel empleadoConNuevoTelefono = empleadoRepository.findByTelefono(nuevoTelefono);
        if(StringUtils.isNotBlank(actualTelefono)) {
        	EmpleadoModel actualEmpleado = empleadoRepository.findByTelefono(actualTelefono);
            if(actualEmpleado == null) {
                throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_TELEFONO + actualTelefono);
            }
            if(empleadoConNuevoTelefono != null && (actualEmpleado.getIdEmpleado() != empleadoConNuevoTelefono.getIdEmpleado())) {
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
		EmpleadoModel empleadoConNuevoCorreo = empleadoRepository.findByCorreo(nuevoCorreo);
        if(StringUtils.isNotBlank(actualCorreo)) {
        	EmpleadoModel actualEmpleado = empleadoRepository.findByCorreo(actualCorreo);
            if(actualEmpleado == null) {
                throw new EmpleadoNotFoundException(NO_EMPLEADO_FOUND_BY_CORREO + actualCorreo);
            }
            if(empleadoConNuevoCorreo != null && (actualEmpleado.getIdEmpleado() != empleadoConNuevoCorreo.getIdEmpleado())) {
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
