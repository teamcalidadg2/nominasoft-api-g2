package nomina.soft.backend.controladores;

import static nomina.soft.backend.statics.PeriodoNominaImplConstant.PERIODO_NOMINA_ELIMINADO;
import static org.springframework.http.HttpStatus.OK;

import java.text.ParseException;
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
import nomina.soft.backend.entidades.HttpResponse;
import nomina.soft.backend.entidades.PeriodoNomina;
import nomina.soft.backend.excepciones.clases.PeriodoNominaExistsException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotValidException;
import nomina.soft.backend.servicios.declaracion.ServicioPeriodoNomina;
@Controller
@RequestMapping("/periodoNomina")
public class PeriodoNominaController {

    private ServicioPeriodoNomina periodoNominaService;

    @Autowired
    public PeriodoNominaController(ServicioPeriodoNomina periodoNominaService) {
        super();
        this.periodoNominaService = periodoNominaService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PeriodoNomina>> getPeriodosNomina() throws PeriodoNominaNotFoundException {
        List<PeriodoNomina> listaDePeriodosNomina = periodoNominaService.obtenerTodosLosPeriodos();
        return new ResponseEntity<>(listaDePeriodosNomina, OK);
    }

    @GetMapping("/buscar/{idNomina}")
    public ResponseEntity<PeriodoNomina> getPeriodoNomina(@PathVariable("idNomina") Long idNomina)
            throws PeriodoNominaNotFoundException {
        PeriodoNomina periodoNomina = periodoNominaService.buscarPeriodoNominaPorId(idNomina);
        return new ResponseEntity<>(periodoNomina, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<PeriodoNomina> createPeriodoNomina(@RequestBody PeriodoNominaDto periodoNominaDto)
            throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException, ParseException {
        PeriodoNomina periodoNomina = periodoNominaService.guardarNuevoPeriodoNomina(periodoNominaDto);
        return new ResponseEntity<>(periodoNomina, OK);
    }

    @DeleteMapping("/delete/{idNomina}")
    public ResponseEntity<HttpResponse> deleteAfp(@PathVariable("idNomina") Long idNomina)
            throws PeriodoNominaNotFoundException {
        periodoNominaService.eliminarPeriodoNomina(idNomina);
        return response(OK, PERIODO_NOMINA_ELIMINADO);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
                httpStatus);
    }

}
