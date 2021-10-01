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

import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.exception.domain.AfpNotFoundException;
import nomina.soft.backend.exception.domain.ContratoExistsException;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.HttpResponse;
import nomina.soft.backend.services.ContratoService;

@Controller
@RequestMapping("/contrato")
public class ContratoController {
	
	private ContratoService contratoService;

    @Autowired
    public ContratoController(ContratoService contratoService) {
		super();
		this.contratoService = contratoService;
	}

	@GetMapping("/listar/{dni}")
    public ResponseEntity<List<ContratoModel>> obtenerContratos(@PathVariable("dni") String dni) throws EmpleadoNotFoundException{
        List<ContratoModel> lista = contratoService.getAll(dni);
        return new ResponseEntity<>(lista, OK);
    }
    
	
	@GetMapping("/buscarVigente/{dni}")
    public ResponseEntity<ContratoModel> getEmpleadoByDni(@PathVariable("dni") String dni) throws ContratoNotFoundException, EmpleadoNotFoundException {
		ContratoModel contrato = contratoService.buscarContratoPorDni(dni);
        return new ResponseEntity<>(contrato, OK);
    }
	

    @PostMapping("/guardar")
    public ResponseEntity<ContratoModel> create(@RequestBody ContratoDto contratoDto) throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException {
        ContratoModel contrato = contratoService.guardarContrato(contratoDto);
        return new ResponseEntity<>(contrato, OK);
    }
    
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

}
