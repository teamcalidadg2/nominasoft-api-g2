package nomina.soft.backend.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.repositories.EmpleadoRepository;


@Service
public class EmpleadoService {
    @Autowired
    EmpleadoRepository empleadoRepository;
    
    public ArrayList<EmpleadoModel> getAll(){
        return(ArrayList<EmpleadoModel>)empleadoRepository.findAll();
    }
    public boolean existsByEmail(String email) {
        return empleadoRepository.existsByCorreo(email);
    }
    public EmpleadoModel guardarEmpleado(EmpleadoDto empleadoDto){
        EmpleadoModel empleado = new EmpleadoModel();

        empleado.setEmpleado_id(empleadoDto.getEmpleado_id());
        empleado.setNombres(empleadoDto.getNombres());
        empleado.setApellidos(empleadoDto.getApellidos());
        empleado.setDni(empleadoDto.getDni());
        empleado.setFechaNacimiento(empleadoDto.getFechaNacimiento());
        empleado.setTelefono(empleadoDto.getTelefono());
        empleado.setCorreo(empleadoDto.getCorreo());
        empleado.setDireccion(empleadoDto.getDireccion());
        //empleado.setContratos(empleadoDto.getContratos());
        empleadoRepository.save(empleado);
        return empleado;
    }

    public void delete(int id){
        empleadoRepository.deleteById(id);
    }


    public Optional<EmpleadoModel> obtenerPorCorreo(String email){
        return empleadoRepository.findByCorreo(email);
    }
}