package nomina.soft.backend.Controladores;

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

import nomina.soft.backend.Data_Transfer_Object.DTO_PeriodoNomina;
import nomina.soft.backend.Entidades.HttpResponse;
import nomina.soft.backend.Entidades.PeriodoNomina;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaExistsException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotValidException;
import nomina.soft.backend.Servicios.Declaracion.Servicio_PeriodoNomina;
import static nomina.soft.backend.Constantes.PeriodoNominaImplConstant.*;
@Controller
@RequestMapping("/periodoNomina")
public class Controlador_PeriodoNomina {

    private Servicio_PeriodoNomina periodoNominaService;

    @Autowired
    public Controlador_PeriodoNomina(Servicio_PeriodoNomina periodoNominaService) {
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
    public ResponseEntity<PeriodoNomina> createPeriodoNomina(@RequestBody DTO_PeriodoNomina periodoNominaDto)
            throws PeriodoNominaNotFoundException, PeriodoNominaExistsException, PeriodoNominaNotValidException {
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
