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

import nomina.soft.backend.dto.PeriodoNominaDto;
import nomina.soft.backend.exception.domain.PeriodoNominaExistsException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotFoundException;
import nomina.soft.backend.exception.domain.PeriodoNominaNotValidException;
import nomina.soft.backend.models.HttpResponse;
import nomina.soft.backend.models.PeriodoNominaModel;
import nomina.soft.backend.services.PeriodoNominaService;

@Controller
@RequestMapping("/periodoNomina")
public class PeriodoNominaController {

	private PeriodoNominaService periodoNominaService;
    public static final String PERIODO_NOMINA_DELETED_SUCCESSFULLY = "Periodo de Nómina eliminado exitosamente";

    @Autowired
    public  PeriodoNominaController(PeriodoNominaService periodoNominaService){
        super();
        this.periodoNominaService = periodoNominaService;
    }

	@GetMapping("/listar")
    public ResponseEntity<List<PeriodoNominaModel>> getPeriodosNomina() throws PeriodoNominaNotFoundException{
        List<PeriodoNominaModel> lista = periodoNominaService.getAll();
        return new ResponseEntity<>(lista, OK);
    }
    
	@GetMapping("/buscar/{id}")
    public ResponseEntity<PeriodoNominaModel> getPeriodoNomina(@PathVariable("id") Long id) throws PeriodoNominaNotFoundException{
		PeriodoNominaModel periodoNomina = periodoNominaService.buscarPeriodoNominaPorId(id);
        return new ResponseEntity<>(periodoNomina, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<PeriodoNominaModel> createPeriodoNomina(@RequestBody PeriodoNominaDto periodoNominaDto) throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException {
    	PeriodoNominaModel periodoNomina = periodoNominaService.guardarPeriodoNomina(periodoNominaDto);
        return new ResponseEntity<>(periodoNomina, OK);
    }
	
	@DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse> deleteAfp(@PathVariable("id") Long id) throws PeriodoNominaNotFoundException {
		periodoNominaService.deletePeriodoNomina(id);
        return response(OK, PERIODO_NOMINA_DELETED_SUCCESSFULLY);
    }
    
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
	
}
