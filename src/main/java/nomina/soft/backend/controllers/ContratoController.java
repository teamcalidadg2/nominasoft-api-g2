package nomina.soft.backend.controllers;

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
import nomina.soft.backend.exception.domain.AfpExistsException;
import nomina.soft.backend.exception.domain.AfpNotFoundException;
import nomina.soft.backend.exception.domain.ContratoExistsException;
import nomina.soft.backend.exception.domain.ContratoNotFoundException;
import nomina.soft.backend.exception.domain.ContratoNotValidException;
import nomina.soft.backend.exception.domain.EmpleadoNotFoundException;
import nomina.soft.backend.exception.domain.EmpleadoNotValidException;
import nomina.soft.backend.models.ContratoModel;
import nomina.soft.backend.models.HttpResponse;
import nomina.soft.backend.services.ContratoService;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

    public static final String CONTRATO_CANCELADO_SUCCESFULLY = "Contrato cancelado exitosamente";
    private ContratoService contratoService;

    @Autowired
    public ContratoController(ContratoService contratoService) {
        super();
        this.contratoService = contratoService;
    }

    @GetMapping("/listar/{dni}")
    public ResponseEntity<List<ContratoModel>> obtenerContratos(@PathVariable("dni") String dni)
            throws EmpleadoNotFoundException {
        List<ContratoModel> lista = contratoService.getAll(dni);
        return new ResponseEntity<>(lista, OK);
    }

    @GetMapping("/buscarVigente/{dni}")
    public ResponseEntity<ContratoModel> getEmpleadoByDni(@PathVariable("dni") String dni)
            throws ContratoNotFoundException, EmpleadoNotFoundException, EmpleadoNotValidException {
        ContratoModel contrato = contratoService.buscarContratoPorDni(dni);
        return new ResponseEntity<>(contrato, OK);
    }

    @GetMapping("/buscar/{idContrato}")
    public ResponseEntity<ContratoModel> getContratoById(@PathVariable("idContrato") String idContrato)
            throws ContratoNotFoundException, EmpleadoNotFoundException, EmpleadoNotValidException, NumberFormatException, ContratoNotValidException {
        ContratoModel contrato = contratoService.buscarContratoPorId(idContrato);
        return new ResponseEntity<>(contrato, OK);
    }

    @PostMapping("/guardar")
    public ResponseEntity<ContratoModel> create(@RequestBody ContratoDto contratoDto)
            throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException {
        ContratoModel contrato = contratoService.guardarContrato(contratoDto);
        return new ResponseEntity<>(contrato, OK);
    }

    @PostMapping("/guardar/form")
    public ResponseEntity<ContratoModel> save(@RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
            @RequestParam(value = "fechaFin", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin, @RequestParam("puesto") String puesto,
            @RequestParam("horasPorSemana") String horasPorSemana, @RequestParam("idAfp") String idAfp,
            @RequestParam("idEmpleado") String idEmpleado,
            @RequestParam(value = "tieneAsignacionFamiliar", required = false) Boolean tieneAsignacionFamiliar,
            @RequestParam("pagoPorHora") String pagoPorHora) throws NumberFormatException, ContratoNotValidException,
            AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException {
        ContratoModel newContrato = contratoService.guardarContrato(fechaInicio, fechaFin, idEmpleado, puesto,
                horasPorSemana, idAfp, tieneAsignacionFamiliar, pagoPorHora);
        return new ResponseEntity<>(newContrato, OK);
    }

    @PostMapping("/editar")
    public ResponseEntity<ContratoModel> update(@RequestParam("idContrato") String idContrato,
            @RequestParam("puesto") String puesto, @RequestParam("horasPorSemana") String horasPorSemana,
            @RequestParam("idAfp") String idAfp,
            @RequestParam(value = "tieneAsignacionFamiliar", required = false) Boolean tieneAsignacionFamiliar,
            @RequestParam("pagoPorHora") String pagoPorHora)
            throws AfpNotFoundException, AfpExistsException, ContratoNotValidException, ContratoNotFoundException {
        ContratoModel updatedContrato = contratoService.updateContrato(idContrato, puesto, horasPorSemana, idAfp,
                tieneAsignacionFamiliar, pagoPorHora);
        return new ResponseEntity<>(updatedContrato, OK);
    }



    @DeleteMapping("/cancelar/{idContrato}")
    public ResponseEntity<HttpResponse> cancelar(@PathVariable("idContrato") String idContrato)
            throws ContratoNotValidException, AfpNotFoundException, EmpleadoNotFoundException, ContratoExistsException,
            ContratoNotFoundException {
        contratoService.cancelarContrato(idContrato);
        return response(OK, CONTRATO_CANCELADO_SUCCESFULLY);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
	

}
