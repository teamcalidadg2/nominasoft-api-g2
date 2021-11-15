package nomina.soft.backend.controladores;

import static nomina.soft.backend.constantes.ContratoImplConstant.CONTRATO_CANCELADO;
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

import nomina.soft.backend.dto.ContratoDto;
import nomina.soft.backend.excepciones.clases.AfpNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoExistsException;
import nomina.soft.backend.excepciones.clases.ContratoNotFoundException;
import nomina.soft.backend.excepciones.clases.ContratoNotValidException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotFoundException;
import nomina.soft.backend.excepciones.clases.EmpleadoNotValidException;
import nomina.soft.backend.models.Contrato;
import nomina.soft.backend.models.HttpResponse;
import nomina.soft.backend.servicios.declaracion.ServicioContrato;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

    private ServicioContrato servicioContrato;

    @Autowired
    public ContratoController(ServicioContrato servicioContrato) {
        super();
        this.servicioContrato = servicioContrato;
    }

    @GetMapping("/listar/{dni}")
    public ResponseEntity<List<Contrato>> obtenerContratos(@PathVariable("dni") String dni)
            throws EmpleadoNotFoundException {
        List<Contrato> lista = servicioContrato.buscarListaDeContratosPorEmpleado(dni);
        return new ResponseEntity<>(lista, OK);
    }

    @GetMapping("/buscarVigente/{dniEmpleado}")
    public ResponseEntity<Contrato> obtenerContratoVigentePorDni(@PathVariable("dniEmpleado") String dniEmpleado)
            throws ContratoNotFoundException, EmpleadoNotFoundException, EmpleadoNotValidException {
        Contrato contratoEncontrado = servicioContrato.buscarContratoVigentePorDni(dniEmpleado);
        return new ResponseEntity<>(contratoEncontrado, OK);
    }

    @GetMapping("/buscar/{idContrato}")
    public ResponseEntity<Contrato> obtenerContratoPorId(@PathVariable("idContrato") String idContrato)
            throws ContratoNotFoundException, NumberFormatException, ContratoNotValidException {
        Contrato contratoEncontrado = servicioContrato.buscarContratoPorId(idContrato);
        return new ResponseEntity<>(contratoEncontrado, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<Contrato> crearContratoPorJson(@RequestBody ContratoDto dtoContrato)
            throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException, NumberFormatException, ContratoNotFoundException {
        Contrato nuevoContrato = servicioContrato.guardarNuevoContrato(dtoContrato);
        return new ResponseEntity<>(nuevoContrato, OK);
    }

    @PostMapping("/guardar/form")
    public ResponseEntity<Contrato> crearContratoPorForm(
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin,
            @RequestParam("puesto") String puesto, @RequestParam("horasPorSemana") String horasPorSemana,
            @RequestParam("idAfp") String idAfp, @RequestParam("idEmpleado") String idEmpleado,
            @RequestParam(value = "tieneAsignacionFamiliar", required = false) Boolean tieneAsignacionFamiliar,
            @RequestParam("pagoPorHora") String pagoPorHora) throws NumberFormatException, ContratoNotValidException,
            AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException, ContratoNotFoundException {
        Contrato nuevoContrato = servicioContrato.guardarNuevoContrato(fechaInicio, fechaFin, idEmpleado, puesto,
                horasPorSemana, idAfp, tieneAsignacionFamiliar, pagoPorHora);
        return new ResponseEntity<>(nuevoContrato, OK);
    }

    @PostMapping("/editar")
    public ResponseEntity<Contrato> editarContrato(@RequestParam("idContrato") String idContrato,
            @RequestParam("puesto") String puesto, @RequestParam("horasPorSemana") String horasPorSemana,
            @RequestParam("idAfp") String idAfp,
            @RequestParam(value = "tieneAsignacionFamiliar", required = false) Boolean tieneAsignacionFamiliar,
            @RequestParam("pagoPorHora") String pagoPorHora)
            throws AfpNotFoundException, ContratoNotValidException, ContratoNotFoundException {
        Contrato contratoActualizado = servicioContrato.actualizarContrato(idContrato, puesto, horasPorSemana, idAfp,
                tieneAsignacionFamiliar, pagoPorHora);
        return new ResponseEntity<>(contratoActualizado, OK);
    }

    @DeleteMapping("/cancelar/{idContrato}")
    public ResponseEntity<HttpResponse> cancelarContrato(@PathVariable("idContrato") String idContrato)
            throws ContratoNotValidException, ContratoNotFoundException {
        servicioContrato.cancelarContrato(idContrato);
        return response(OK, CONTRATO_CANCELADO);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
                httpStatus);
    }

}
