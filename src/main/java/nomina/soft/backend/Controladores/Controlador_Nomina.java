package nomina.soft.backend.Controladores;

import static org.springframework.http.HttpStatus.OK;

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

import nomina.soft.backend.Data_Transfer_Object.DTO_Nomina;
import nomina.soft.backend.Entidades.BoletaDePago;
import nomina.soft.backend.Entidades.HttpResponse;
import nomina.soft.backend.Entidades.Nomina;
import nomina.soft.backend.Excepciones.Clases.AfpNotFoundException;
import nomina.soft.backend.Excepciones.Clases.ContratoExistsException;
import nomina.soft.backend.Excepciones.Clases.ContratoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.ContratoNotValidException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotFoundException;
import nomina.soft.backend.Excepciones.Clases.EmpleadoNotValidException;
import nomina.soft.backend.Excepciones.Clases.NominaNotFoundException;
import nomina.soft.backend.Excepciones.Clases.NominaNotValidException;
import nomina.soft.backend.Excepciones.Clases.PeriodoNominaNotFoundException;
import nomina.soft.backend.Servicios.Declaracion.Servicio_Nomina;
import static nomina.soft.backend.Constantes.NominaImplConstant.*;
@Controller
@RequestMapping("/nomina")
public class Controlador_Nomina {


    private Servicio_Nomina nominaService;

    @Autowired
    public Controlador_Nomina(Servicio_Nomina nominaService) {
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
    public ResponseEntity<List<BoletaDePago>> generate(@RequestBody DTO_Nomina nominaDto)
            throws ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException,
            NominaNotValidException, PeriodoNominaNotFoundException, EmpleadoNotValidException {
        List<BoletaDePago> listaBoletasDePago = nominaService.generarNuevaNomina(nominaDto);
        return new ResponseEntity<>(listaBoletasDePago, OK);
    }

    @PostMapping("/generar/form")
    public ResponseEntity<List<BoletaDePago>> generateByForm(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha,
            @RequestParam("descripcion") String descripcion, @RequestParam("idPeriodoNomina") String idPeriodoNomina)
            throws NumberFormatException, NominaNotValidException, PeriodoNominaNotFoundException,
            ContratoNotFoundException, EmpleadoNotFoundException, ContratoNotValidException, EmpleadoNotValidException {
        List<BoletaDePago> listaBoletasDePago = nominaService.generarNuevaNomina(fecha, descripcion, idPeriodoNomina);
        return new ResponseEntity<>(listaBoletasDePago, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<List<BoletaDePago>> save(@RequestBody DTO_Nomina nominaDto)
            throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException,
            ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException {
        List<BoletaDePago> listaBoletasDePago = nominaService.guardarNuevaNomina(nominaDto);
        return new ResponseEntity<>(listaBoletasDePago, OK);
    }

    @PostMapping("/guardar/form")
    public ResponseEntity<List<BoletaDePago>> saveByForm(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha,
            @RequestParam("descripcion") String descripcion, @RequestParam("idPeriodoNomina") String idPeriodoNomina)
            throws PeriodoNominaNotFoundException, ContratoNotFoundException, EmpleadoNotFoundException,
            ContratoNotValidException, NominaNotValidException, EmpleadoNotValidException {
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
