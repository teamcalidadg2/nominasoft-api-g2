package nomina.soft.backend.servicios.implementacion;

import static nomina.soft.backend.statics.EmpleadoImplConstant.CORREO_YA_EXISTE;
import static nomina.soft.backend.statics.EmpleadoImplConstant.DNI_YA_EXISTE;
import static nomina.soft.backend.statics.EmpleadoImplConstant.EMPLEADO_NO_ENCONTRADO_POR_CORREO;
import static nomina.soft.backend.statics.EmpleadoImplConstant.EMPLEADO_NO_ENCONTRADO_POR_DNI;
import static nomina.soft.backend.statics.EmpleadoImplConstant.EMPLEADO_NO_ENCONTRADO_POR_ID;
import static nomina.soft.backend.statics.EmpleadoImplConstant.EMPLEADO_NO_ENCONTRADO_POR_TELEFONO;
import static nomina.soft.backend.statics.EmpleadoImplConstant.TELEFONO_YA_EXISTE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Objects;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nomina.soft.backend.dao.EmpleadoDao;
import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.entidades.Contrato;
import nomina.soft.backend.entidades.Empleado;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoExistsException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.servicios.declaracion.ServicioEmpleado;

@Service
@Transactional
public class ImplServicioEmpleado implements ServicioEmpleado {

    private EmpleadoDao repositorioEmpleado;

    @Autowired
    public ImplServicioEmpleado(EmpleadoDao repositorioEmpleado) {
        super();
        this.repositorioEmpleado = repositorioEmpleado;
    }

    @Override
    public ArrayList<Empleado> obtenerTodos() {
        return (ArrayList<Empleado>) repositorioEmpleado.findAll();
    }

    @Override
    public Empleado guardarNuevoEmpleado(EmpleadoDto empleadoDto)
            throws EmpleadoNotFoundException, EmpleadoExistsException, EmpleadoNotValidException {
        Empleado empleado = new Empleado();
        validarNuevoDni(EMPTY, empleadoDto.getDni());
        validarNuevoTelefono(EMPTY, empleadoDto.getTelefono());
        validarNuevoCorreo(EMPTY, empleadoDto.getCorreo());
        boolean esDniValido = empleado.validarDni(empleadoDto.getDni());
        if (esDniValido) {
            empleado.setNombres(empleadoDto.getNombres());
            empleado.setApellidos(empleadoDto.getApellidos());
            empleado.setDni(empleadoDto.getDni());
            empleado.setFechaNacimiento(empleadoDto.getFechaNacimiento());
            empleado.setTelefono(empleadoDto.getTelefono());
            empleado.setCorreo(empleadoDto.getCorreo());
            empleado.setDireccion(empleadoDto.getDireccion());
            empleado.setContratos(new ArrayList<>());
            repositorioEmpleado.save(empleado);
        }
        return empleado;
    }

    @Override
    public void eliminarEmpleado(Long idEmpleado) throws EmpleadoNotFoundException {
        Empleado empleadoEncontrado = repositorioEmpleado.findByIdEmpleado(idEmpleado);
        if (empleadoEncontrado == null) {
            throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_ID);
        } else {
            repositorioEmpleado.deleteById(idEmpleado);
        }
    }

    @Override
    public Empleado buscarEmpleadoPorDni(String dni) throws EmpleadoNotFoundException, NumberFormatException,
            ContratoNotValidException, EmpleadoNotValidException {
        Contrato contratoTemp = new Contrato();
        Empleado empleadoEncontrado = new Empleado();
        if (empleadoEncontrado.validarDni(dni) && contratoTemp.empleadoValido(dni))
            empleadoEncontrado = this.repositorioEmpleado.findByDni(dni);

        if (empleadoEncontrado == null)
            throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_DNI);
        return empleadoEncontrado;
    }

    @Override
    public Empleado buscarEmpleadoPorId(String idEmpleado)
            throws EmpleadoNotFoundException, NumberFormatException, ContratoNotValidException {
        Contrato contratoTemporal = new Contrato();
        if (contratoTemporal.empleadoValido(idEmpleado)) {
            Empleado empleadoEncontrado = this.repositorioEmpleado.findByIdEmpleado(Long.parseLong(idEmpleado));
            if (empleadoEncontrado == null)
                throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_ID);
            else
                return empleadoEncontrado;
        }
        return null;
    }

    @Override
    public Empleado buscarEmpleadoPorTelefono(String telefono) throws EmpleadoNotFoundException {
        Empleado empleado = this.repositorioEmpleado.findByTelefono(telefono);
        if (empleado == null)
            throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_TELEFONO);
        return empleado;
    }

    @Override
    public Empleado buscarEmpleadoPorCorreo(String correo) throws EmpleadoNotFoundException {
        Empleado empleado = this.repositorioEmpleado.findByCorreo(correo);
        if (empleado == null)
            throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_CORREO);
        return empleado;
    }

    private Empleado validarNuevoDni(String actualDni, String nuevoDni)
            throws EmpleadoNotFoundException, EmpleadoExistsException {
        Empleado empleadoConNuevoDni = repositorioEmpleado.findByDni(nuevoDni);
        if (StringUtils.isNotBlank(actualDni)) {
            Empleado actualEmpleado = repositorioEmpleado.findByDni(actualDni);
            if (actualEmpleado == null)
                throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_DNI);
            if (empleadoConNuevoDni != null
                    && (!Objects.equals(actualEmpleado.getIdEmpleado(), empleadoConNuevoDni.getIdEmpleado())))
                throw new EmpleadoExistsException(DNI_YA_EXISTE);

            return actualEmpleado;
        } else {
            if (empleadoConNuevoDni != null)
                throw new EmpleadoExistsException(DNI_YA_EXISTE);
            return null;
        }
    }

    private Empleado validarNuevoTelefono(String actualTelefono, String nuevoTelefono)
            throws EmpleadoNotFoundException, EmpleadoExistsException {
        Empleado empleadoConNuevoTelefono = repositorioEmpleado.findByTelefono(nuevoTelefono);
        if (StringUtils.isNotBlank(actualTelefono)) {
            Empleado actualEmpleado = repositorioEmpleado.findByTelefono(actualTelefono);
            if (actualEmpleado == null)
                throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_TELEFONO);

            if (empleadoConNuevoTelefono != null
                    && (!Objects.equals(actualEmpleado.getIdEmpleado(), empleadoConNuevoTelefono.getIdEmpleado())))
                throw new EmpleadoExistsException(TELEFONO_YA_EXISTE);

            return actualEmpleado;
        } else {
            if (empleadoConNuevoTelefono != null)
                throw new EmpleadoExistsException(TELEFONO_YA_EXISTE);

            return null;
        }
    }

    private Empleado validarNuevoCorreo(String actualCorreo, String nuevoCorreo)
            throws EmpleadoNotFoundException, EmpleadoExistsException {
        Empleado empleadoConNuevoCorreo = repositorioEmpleado.findByCorreo(nuevoCorreo);
        if (StringUtils.isNotBlank(actualCorreo)) {
            Empleado actualEmpleado = repositorioEmpleado.findByCorreo(actualCorreo);
            if (actualEmpleado == null)
                throw new EmpleadoNotFoundException(EMPLEADO_NO_ENCONTRADO_POR_CORREO);

            if (empleadoConNuevoCorreo != null
                    && (!Objects.equals(actualEmpleado.getIdEmpleado(), empleadoConNuevoCorreo.getIdEmpleado())))
                throw new EmpleadoExistsException(CORREO_YA_EXISTE);

            return actualEmpleado;
        } else {
            if (empleadoConNuevoCorreo != null)
                throw new EmpleadoExistsException(CORREO_YA_EXISTE);

            return null;
        }
    }

}
