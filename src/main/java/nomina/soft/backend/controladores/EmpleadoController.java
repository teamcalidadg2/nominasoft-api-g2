package nomina.soft.backend.controladores;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.entidades.Empleado;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoExistsException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.servicios.declaracion.ServicioEmpleado;
@CrossOrigin("http://localhost:4200")
@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

    private ServicioEmpleado empleadoService;

    @Autowired
    public EmpleadoController(ServicioEmpleado empleadoService) {
		super();
		this.empleadoService = empleadoService;
	}

	@GetMapping("/listar")
    public ResponseEntity<List<Empleado>> obtenerVentas(){
        List<Empleado> lista = empleadoService.obtenerTodos();
        return new ResponseEntity<>(lista, OK);
    }
    
	@GetMapping("/buscar/dni/{dni}")
    public ResponseEntity<Empleado> getEmpleadoByDni(@PathVariable("dni") String dni) throws EmpleadoNotFoundException, NumberFormatException, ContratoNotValidException, EmpleadoNotValidException {
		Empleado empleado = empleadoService.buscarEmpleadoPorDni(dni);
        return new ResponseEntity<>(empleado, OK);
    }

    @GetMapping("/buscar/id/{idEmpleado}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable("idEmpleado") String idEmpleado) throws EmpleadoNotFoundException, NumberFormatException, ContratoNotValidException{
		Empleado empleado = empleadoService.buscarEmpleadoPorId(idEmpleado);
        return new ResponseEntity<>(empleado, OK);
    }
	
	@GetMapping("/buscar/telefono/{telefono}")
    public ResponseEntity<Empleado> getEmpleadoByTelefono(@PathVariable("telefono") String telefono) throws EmpleadoNotFoundException {
		Empleado empleado = empleadoService.buscarEmpleadoPorTelefono(telefono);
        return new ResponseEntity<>(empleado, OK);
    }
	
	@GetMapping("/buscar/correo/{correo}")
    public ResponseEntity<Empleado> getEmpleadoByCorreo(@PathVariable("correo") String correo) throws EmpleadoNotFoundException {
		Empleado empleado = empleadoService.buscarEmpleadoPorCorreo(correo);
        return new ResponseEntity<>(empleado, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<Empleado> create(@RequestBody EmpleadoDto empleadoDto) throws EmpleadoNotFoundException, EmpleadoExistsException, EmpleadoNotValidException {
        Empleado empleado = empleadoService.guardarNuevoEmpleado(empleadoDto);
        return new ResponseEntity<>(empleado, OK);
    }
    
}
