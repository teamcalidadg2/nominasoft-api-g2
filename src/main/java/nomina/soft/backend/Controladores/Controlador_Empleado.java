package nomina.soft.backend.Controladores;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import nomina.soft.backend.Data_Transfer_Object.DTO_Empleado;
import nomina.soft.backend.Entidades.Empleado;
import nomina.soft.backend.Entidades.HttpResponse;
import nomina.soft.backend.Excepciones.Clases.ContratoNotValidException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoExistsException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;
import nomina.soft.backend.Servicios.Declaracion.Servicio_Empleado;
@CrossOrigin("http://localhost:4200")
@Controller
@RequestMapping("/empleado")
public class Controlador_Empleado {

    private Servicio_Empleado empleadoService;

    @Autowired
    public Controlador_Empleado(Servicio_Empleado empleadoService) {
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
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable("idEmpleado") String idEmpleado) throws EmpleadoNotFoundException, NumberFormatException, ContratoNotValidException, EmpleadoNotValidException {
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
    public ResponseEntity<Empleado> create(@RequestBody DTO_Empleado empleadoDto) throws EmpleadoNotFoundException, EmpleadoExistsException, EmpleadoNotValidException {
        Empleado empleado = empleadoService.guardarNuevoEmpleado(empleadoDto);
        return new ResponseEntity<>(empleado, OK);
    }
    
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
    
}
