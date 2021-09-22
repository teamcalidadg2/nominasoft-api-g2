package nomina.soft.backend.controllers;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import nomina.soft.backend.exception.domain.EmpleadoExistsException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.HttpResponse;

import nomina.soft.backend.dto.EmpleadoDto;
import nomina.soft.backend.models.EmpleadoModel;
import nomina.soft.backend.services.EmpleadoService;
@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

    private EmpleadoService empleadoService;

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService) {
		super();
		this.empleadoService = empleadoService;
	}

	@GetMapping("/listar")
    public ResponseEntity<List<EmpleadoModel>> obtenerVentas(){
        List<EmpleadoModel> lista = empleadoService.getAll();
        return new ResponseEntity<>(lista, OK);
    }
    
	@GetMapping("/buscar/dni/{dni}")
    public ResponseEntity<EmpleadoModel> getEmpleadoByDni(@PathVariable("dni") String dni) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = empleadoService.buscarEmpleadoPorDni(dni);
        return new ResponseEntity<>(empleado, OK);
    }
	
	@GetMapping("/buscar/telefono/{telefono}")
    public ResponseEntity<EmpleadoModel> getEmpleadoByTelefono(@PathVariable("telefono") String telefono) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = empleadoService.buscarEmpleadoPorTelefono(telefono);
        return new ResponseEntity<>(empleado, OK);
    }
	
	@GetMapping("/buscar/correo/{correo}")
    public ResponseEntity<EmpleadoModel> getEmpleadoByCorreo(@PathVariable("correo") String correo) throws EmpleadoNotFoundException {
		EmpleadoModel empleado = empleadoService.buscarEmpleadoPorCorreo(correo);
        return new ResponseEntity<>(empleado, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<EmpleadoModel> create(@RequestBody EmpleadoDto empleadoDto) throws EmpleadoNotFoundException, EmpleadoExistsException {
        EmpleadoModel empleado = empleadoService.guardarEmpleado(empleadoDto);
        return new ResponseEntity<>(empleado, OK);
    }
    
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
    
}
