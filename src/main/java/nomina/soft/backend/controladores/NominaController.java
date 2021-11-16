package nomina.soft.backend.controladores;

import static nomina.soft.backend.statics.NominaImplConstant.NOMINA_CERRADA;
import static nomina.soft.backend.statics.NominaImplConstant.NOMINA_ELIMINADA;
import static org.springframework.http.HttpStatus.OK;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import nomina.soft.backend.dto.NominaDto;
import nomina.soft.backend.entidades.BoletaDePago;
import nomina.soft.backend.entidades.HttpResponse;
import nomina.soft.backend.entidades.Nomina;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.excepciones.clases.NominaNotFoundException;
import nomina.soft.backend.excepciones.clases.NominaNotValidException;
import nomina.soft.backend.excepciones.clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.servicios.declaracion.ServicioNomina;
@Controller
@RequestMapping("/nomina")
public class NominaController {


    private ServicioNomina nominaService;

    @Autowired
    public NominaController(ServicioNomina nominaService) {
        super();
        this.nominaService = nominaService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Nomina>> obtenerNominas() {
        List<Nomina> lista = nominaService.obtenerTodas();
        return new ResponseEntity<>(lista, OK);
    }

    @GetMapping("/listar/descripcion/{descripcion}")
    public ResponseEntity<List<Nomina>> obtenerNominasPorDescripcion(
            @PathVariable("descripcion") String descripcion) throws NominaNotFoundException {
        List<Nomina> lista = nominaService.obtenerTodasPorDescripcion(descripcion);
        return new ResponseEntity<>(lista, OK);
    }

    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<Nomina> getNomina(@PathVariable("id") String id)
            throws NominaNotFoundException, NumberFormatException, NominaNotValidException {
        Nomina nomina = nominaService.buscarNominaPorId(id);
        return new ResponseEntity<>(nomina, OK);

    }

    @PostMapping("/generar")
    public ResponseEntity<List<BoletaDePago>> generate(@RequestBody NominaDto nominaDto)
            throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException,
            NominaNotValidException, PeriodoNominaNotFoundException, EmpleadoNotValidException, ParseException {
        List<BoletaDePago> listaBoletasDePago = nominaService.generarNuevaNomina(nominaDto);
        return new ResponseEntity<>(listaBoletasDePago, OK);
    }

    @PostMapping("/generar/form")
    public ResponseEntity<List<BoletaDePago>> generateByForm(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha,
            @RequestParam("descripcion") String descripcion, @RequestParam("idPeriodoNomina") String idPeriodoNomina)
            throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException,
            ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException, ParseException {
        List<BoletaDePago> listaBoletasDePago = nominaService.generarNuevaNomina(fecha, descripcion, idPeriodoNomina);
        return new ResponseEntity<>(listaBoletasDePago, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<List<BoletaDePago>> save(@RequestBody NominaDto nominaDto)
            throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException,
            ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException, ParseException {
        List<BoletaDePago> listaBoletasDePago = nominaService.guardarNuevaNomina(nominaDto);
        return new ResponseEntity<>(listaBoletasDePago, OK);
    }

    @PostMapping("/guardar/form")
    public ResponseEntity<List<BoletaDePago>> saveByForm(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha,
            @RequestParam("descripcion") String descripcion, @RequestParam("idPeriodoNomina") String idPeriodoNomina)
            throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException,
            ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException, NumberFormatException, ParseException {
        List<BoletaDePago> listaBoletasDePago = nominaService.guardarNuevaNomina(fecha, descripcion, idPeriodoNomina);
        return new ResponseEntity<>(listaBoletasDePago, OK);
    }

    @DeleteMapping("/cerrar/{idNomina}")
    public ResponseEntity<HttpResponse> cerrarNomina(@PathVariable(value = "idNomina", required = false) String idNomina)
            throws NominaNotFoundException, NumberFormatException, NominaNotValidException, ContratoNotFoundException {
        nominaService.cerrarNomina(idNomina);
        return response(OK, NOMINA_CERRADA);
    }

    @DeleteMapping("/eliminar/{idNomina}")
    public ResponseEntity<HttpResponse> deleteNomina(@PathVariable("idNomina") String idNomina)
            throws NominaNotFoundException, NumberFormatException, NominaNotValidException, ContratoNotFoundException {
        nominaService.eliminarNomina(idNomina);
        return response(OK, NOMINA_ELIMINADA);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
                httpStatus);
    }

}
