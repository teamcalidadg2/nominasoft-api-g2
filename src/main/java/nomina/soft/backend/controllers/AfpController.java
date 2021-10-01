package nomina.soft.backend.controllers;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import nomina.soft.backend.dto.AfpDto;
import nomina.soft.backend.exception.domain.AfpExistsException;
import nomina.soft.backend.exception.domain.AfpNotFoundException;
import nomina.soft.backend.models.AfpModel;
import nomina.soft.backend.models.HttpResponse;
import nomina.soft.backend.services.AfpService;

@Controller
@RequestMapping("/afp")
public class AfpController {

	public static final String AFP_DELETED_SUCCESSFULLY = "Categoría AFP eliminada exitosamente";
	private AfpService afpService;

    @Autowired
    public AfpController(AfpService afpService) {
		super();
		this.afpService = afpService;
	}

	@GetMapping("/listar")
    public ResponseEntity<List<AfpModel>> getAfps(){
        List<AfpModel> lista = afpService.getAll();
        return new ResponseEntity<>(lista, OK);
    }
    
	@GetMapping("/buscar/{nombre}")
    public ResponseEntity<AfpModel> getUser(@PathVariable("nombre") String nombre) throws AfpNotFoundException {
		AfpModel afp = afpService.buscarAfpPorNombre(nombre);
        return new ResponseEntity<>(afp, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<AfpModel> create(@RequestBody AfpDto afpDto) throws AfpNotFoundException, AfpExistsException {
        AfpModel afp = afpService.guardarAFP(afpDto);
        return new ResponseEntity<>(afp, OK);
    }
    
    @PostMapping("/update")
    public ResponseEntity<AfpModel> update(@RequestParam("actualNombre") String actualNombre,
                                       @RequestParam("nombre") String nombre,
                                       @RequestParam("actualDescuento") float actualDescuento,
                                       @RequestParam("descuento") float descuento) throws AfpNotFoundException, AfpExistsException {
    	AfpModel updatedAfp = afpService.updateAfp(actualNombre, nombre, actualDescuento, descuento);
        return new ResponseEntity<>(updatedAfp, OK);
    }
	
	
	@DeleteMapping("/delete/{nombre}")
    public ResponseEntity<HttpResponse> deleteAfp(@PathVariable("nombre") String nombre) {
		afpService.deleteAfp(nombre);
        return response(OK, AFP_DELETED_SUCCESSFULLY);
    }
    
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
	
	
}
